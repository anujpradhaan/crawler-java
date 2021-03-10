package com.web.crawler.exception;

import com.web.crawler.UrlUtils;
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

	public static final String TOPIC = "links-to-crawl";
	public static final String GROUP_ID = "crawler-group";
	private static final String CONCURRENCY = "3";

	private final ParsingService parsingService;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final VisitedLinksService visitedLinksService;

	@SneakyThrows
	public void startCrawlingUsingUrl(String url) {
		url = UrlUtils.normalizeUrl(url);

		if (UrlUtils.isInvalidValidUrl(url)) {
			throw new MalformedURLException("Invalid Url");
		}
		if (visitedLinksService.isNotVisited(url)) {
			this.kafkaTemplate.send(TOPIC, url);
		}
	}

	@KafkaListener(topics = TOPIC, groupId = GROUP_ID, concurrency = CONCURRENCY)
	public void crawlLinkFromConsumedMessage(String url) {
		log.info("Consumed URL for parsing {}", url);
		parsingService.getLinksFromDocumentAtGivenUrl(url)
				.forEach(this::startCrawlingUsingUrl);
	}
}
