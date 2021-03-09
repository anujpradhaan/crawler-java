package com.web.crawler.exception;

import com.web.crawler.CrawlerApplication;
import com.web.crawler.ParsingService;
import com.web.crawler.URLFilteringService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : anuj.kumar
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlingService {

	private final ParsingService parsingService;

	@SneakyThrows
	public void startCrawlingUsingUrl(String url) {
		if (CrawlerApplication.isInvalidValidUrl(url)) {
			throw new MalformedURLException("Invalid Url");
		}
		url = correctUrlForParsing(url);
		url = URLFilteringService.removeExtraCharactersFromURL(url);

		Set<String> allNonVisitedUrls = parsingService.getLinksFromDocumentAtGivenUrl(url);

		while (!allNonVisitedUrls.isEmpty()) {
			log.info("Total Remaining to Crawl {}", allNonVisitedUrls.size());
			allNonVisitedUrls = allNonVisitedUrls
					.stream()
					.map(parsingService::getLinksFromDocumentAtGivenUrl)
					.flatMap(Collection::stream)
					.collect(Collectors.toSet());
		}
		log.info("Finished Crawling");

	}

	private String correctUrlForParsing(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		return "http://".concat(url);
	}
}
