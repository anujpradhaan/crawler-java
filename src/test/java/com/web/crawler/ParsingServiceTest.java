package com.web.crawler;

import com.web.crawler.filter.URLFilteringService;
import com.web.crawler.parser.ParsingService;
import com.web.crawler.storage.VisitedLinksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParsingServiceTest {

	private ParsingService parsingService;

	@BeforeEach
	public void init() {
		VisitedLinksService visitedLinksService = new VisitedLinksService();
		URLFilteringService urlFilteringService = new URLFilteringService(visitedLinksService);
		parsingService = new ParsingService(urlFilteringService, visitedLinksService);
	}
	//
	//	@Test
	//	public void getLinksFromURL() throws IOException, URISyntaxException {
	//		URL url = new URL("https://monzo.com/");
	//		System.out.println(parsingService.getLinksFromDocumentAtGivenUrl(url));
	//	}

	@Test
	public void testEmptyUrl() {
		Set<String> linksInsideGivenUrl = parsingService.getLinksFromDocumentAtGivenUrl("");
		assertTrue(linksInsideGivenUrl.isEmpty());
	}

	@Test
	public void testBadUrl() {
		Set<String> linksInsideGivenUrl = parsingService.getLinksFromDocumentAtGivenUrl("http://www.dklsafjkald.com/");
		assertTrue(linksInsideGivenUrl.isEmpty());
	}

	@Test
	public void testValidUrl() {
		String givenUrl = "https://monzo.com";
		Set<String> linksInsideGivenUrl = parsingService.getLinksFromDocumentAtGivenUrl(givenUrl);
		assertFalse(linksInsideGivenUrl.isEmpty());
		URL givenUrlObject = UrlUtils.getUrlObjectFrom(givenUrl).get();

		linksInsideGivenUrl.forEach(link -> {
			URL url = UrlUtils.getUrlObjectFrom(link).get();
			assertEquals(givenUrlObject.getHost(), url.getHost());
		});
	}

}