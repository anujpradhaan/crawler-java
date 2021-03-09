package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerApplicationTest {

	@Mock
	private CrawlingService crawlingService;

	@Test
	void run() {
		CrawlerApplication crawlerApplication = new CrawlerApplication(crawlingService);
		ApplicationArguments applicationArguments = new DefaultApplicationArguments();
	}

	@Test
	public void testUrlDomain() throws MalformedURLException {
		URL urlObject = new URL("http://auth.facebook.com/tst/abc");
		assertEquals("auth.facebook.com", urlObject.getHost());
	}
}