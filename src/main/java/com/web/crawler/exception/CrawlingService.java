package com.web.crawler.exception;

import com.web.crawler.filter.URLFilteringService;
import com.web.crawler.parser.ParsingService;
import com.web.crawler.storage.VisitedLinksService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

/**
 * @author : anuj.kumar
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlingService {

	private static final String TOPIC = "links-to-crawl";

	private final ParsingService parsingService;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final VisitedLinksService visitedLinksService;

	@SneakyThrows
	public void startCrawlingUsingUrl(String url) {
		if (URLFilteringService.isInvalidValidUrl(url)) {
			throw new MalformedURLException("Invalid Url");
		}
		url = normalizeUrl(url);
		if (visitedLinksService.isNotVisited(url)) {
			this.kafkaTemplate.send(TOPIC, url);
		}
	}

	@KafkaListener(topics = TOPIC, groupId = "crawler-group", concurrency = "3")
	public void crawlLink(String url) {
		log.info("Consumed URL for parsing {}", url);
		parsingService.getLinksFromDocumentAtGivenUrl(url)
				.forEach(this::startCrawlingUsingUrl);
	}

	private String normalizeUrl(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		url = "http://".concat(url);
		return URLFilteringService.removeExtraCharactersFromURL(url);
	}
}
