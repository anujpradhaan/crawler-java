package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import com.web.crawler.parser.ParsingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CrawlerApplication.class)
@DirtiesContext
@ActiveProfiles("test")
class CrawlerApplicationIntegrationTest {

	@Autowired
	@InjectMocks
	private CrawlingService crawlingService;

	@MockBean
	private ParsingService parsingService;

	@Autowired
	private ApplicationContext context;

	@Test
	void whenContextLoads_thenRunnersAreNotLoaded() {
		assertNotNull(context.getBean(CrawlingService.class));

		assertThrows(NoSuchBeanDefinitionException.class,
				() -> context.getBean(CommandLineExecutor.class),
				"CommandLineRunner should not be loaded during this integration test");
	}

	@Test
	void sendLinkWithoutProtocol() {
		when(parsingService.getLinksFromDocumentAtGivenUrl(any(String.class))).thenReturn(Set.of("www.facebook.com"));
		crawlingService.startCrawlingUsingUrl("www.facebook.com");
	}
}
