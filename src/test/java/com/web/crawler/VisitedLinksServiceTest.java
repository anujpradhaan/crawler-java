package com.web.crawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisitedLinksServiceTest {

	private VisitedLinksService visitedLinksService = new VisitedLinksService();

	private static final String URL = "https://facebook.com";

	@Test
	void markVisited() {
		assertTrue(visitedLinksService.markVisited(URL));
		assertFalse(visitedLinksService.isNotVisited(URL));
	}

	@Test
	void isVisited() {
		assertTrue(visitedLinksService.isNotVisited("http://www.google.com"));
	}
}