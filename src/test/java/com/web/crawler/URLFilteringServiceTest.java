package com.web.crawler;

import com.web.crawler.filter.URLFilteringService;
import com.web.crawler.storage.VisitedLinksService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URLFilteringServiceTest {

	private final VisitedLinksService visitedLinksService = new VisitedLinksService();
	private final URLFilteringService urlFilteringService = new URLFilteringService(visitedLinksService);

	@Test
	void removeExtraSlashFromUrl() {
		String url = "http://www.cnn.com/news/";
		String correctUrl = url.substring(0, url.lastIndexOf('/'));
		url = UrlUtils.removeExtraCharactersFromURL(url);
		assertEquals(correctUrl, url);
	}

	@Test
	void removeExtraFragmentIdentifierFromUrl() {
		String url = "http://www.cnn.com/news/headlines.html#SPORTS";
		String correctUrl = url.substring(0, url.lastIndexOf('#'));
		url = UrlUtils.removeExtraCharactersFromURL(url);
		assertEquals(correctUrl, url);
	}

	@Test
	void removeExtraFragmentIdentifierFromUrl_WithoutHash() {
		String url = "http://www.cnn.com/news/headlines.html";
		String correctUrl = url;
		url = UrlUtils.removeExtraCharactersFromURL(url);
		assertEquals(correctUrl, url);
	}

	@Test
	void filter_NoUrls() {
		String facebookUrl = "https://www.facebook.com";
		Set<String> filteredUrls = urlFilteringService.filter(facebookUrl, List.of(
				facebookUrl,
				"https://www.facebook.com/auth",
				"https://www.facebook.com/login"
		));

		assertEquals(3, filteredUrls.size());
		assertTrue(visitedLinksService.isNotVisited(facebookUrl));
	}

	@Test
	void filter_ToBeVisitedMalFormedUrls() {
		String facebookUrl = "https://www.facebook.com";
		Set<String> filteredUrls = urlFilteringService.filter(facebookUrl, List.of(
				facebookUrl,
				"www.facebook.com/auth",
				"https://www.facebook.com/login"
		));

		assertEquals(2, filteredUrls.size());
		assertFalse(filteredUrls.contains("www.facebook.com/auth"));
		assertTrue(visitedLinksService.isNotVisited(facebookUrl));
	}

	@Test
	void filter_BaseMalFormedUrls() {
		String facebookUrl = "www.facebook.com";
		Set<String> filteredUrls = urlFilteringService.filter(facebookUrl, List.of(
				facebookUrl,
				"www.facebook.com/auth",
				"https://www.facebook.com/login"
		));

		assertEquals(0, filteredUrls.size());
		assertTrue(visitedLinksService.isNotVisited(facebookUrl));
	}

	@Test
	void malTest() {
		assertTrue(UrlUtils.isInvalidValidUrl("httpsss://www.facebook.com"));
	}

}