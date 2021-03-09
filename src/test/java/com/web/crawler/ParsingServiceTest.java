package com.web.crawler;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

class ParsingServiceTest {

	private ParsingService parsingService;

//	@BeforeEach
//	public void init() {
//		parsingService = new ParsingService();
//	}
//
//	@Test
//	public void getLinksFromURL() throws IOException, URISyntaxException {
//		URL url = new URL("https://monzo.com/");
//		System.out.println(parsingService.getLinksFromDocumentAtGivenUrl(url));
//	}

	@Test
	public void test() throws MalformedURLException {
		URL url = new URL("http://www.monzo.com");
		URL url1 = new URL("https://monzo.com/i/coronavirus-update/");
		System.out.println(url.getHost());
		System.out.println(url1.getHost());
	}

}