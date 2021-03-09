package com.web.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : anuj.kumar
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ParsingService {
	private static final String ANCHOR_TAG_NAME = "a";
	private static final String LINK_TAG_NAME = "link";

	private final URLFilteringService urlFilteringService;
	private final VisitedLinksService visitedLinksService;

	public Set<String> getLinksFromDocumentAtGivenUrl(String url) {

		Document htmlDocument = null;
		try {
			htmlDocument = Jsoup.connect(url).get();
		} catch (IOException e) {
			log.error("Error fetching document for url {}", url);
			return Set.of();
		}

		List<String> allUrlsInsideDocument = getUrlsFromHtmlDocumentForAGivenHTMLTag(htmlDocument, ANCHOR_TAG_NAME);
		allUrlsInsideDocument.addAll(getUrlsFromHtmlDocumentForAGivenHTMLTag(htmlDocument, LINK_TAG_NAME));
		log.info("Total of {} links found in Base url {}", allUrlsInsideDocument.size(), url);

		Set<String> allNonVisitedLinks = urlFilteringService.filter(url, allUrlsInsideDocument);
		visitedLinksService.markVisited(url);
		log.info("Visited {}", url);
		//log.info("A total of {} links found to crawl again for link {}", allNonVisitedLinks.size(), url);
		return allNonVisitedLinks;
	}

	private List<String> getUrlsFromHtmlDocumentForAGivenHTMLTag(Document htmlDocument, String tagName) {
		return htmlDocument.select(tagName)
				.stream()
				.map(element -> element.attr("abs:href"))
				.collect(Collectors.toList());
	}
}
