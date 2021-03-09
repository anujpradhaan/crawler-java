package com.web.crawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class URLFilteringServiceTest {

	@Test
	public void removeExtraSlashFromUrl() {
		String url = "http://www.cnn.com/news/";
		String correctUrl = url.substring(0, url.lastIndexOf('/'));
		System.out.println(correctUrl);
	}

	@Test
	public void removeExtraFragmentIdentifierFromUrl() {
		String url = "http://www.cnn.com/news/headlines.html#SPORTS";
		String correctUrl = url.substring(0, url.lastIndexOf('#'));
		System.out.println(correctUrl);
	}

	@Test
	public void removeExtraFragmentIdentifierFromUrl_WithoutHash() {
		String url = "http://www.cnn.com/news/headlines.html";
		String correctUrl = url.substring(0, url.lastIndexOf('#'));
		System.out.println(correctUrl);
	}

}