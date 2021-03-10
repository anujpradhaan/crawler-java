package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = CrawlerApplication.class)
@DirtiesContext
@ActiveProfiles("test")
class CrawlerApplicationIntegrationTest {

	@Autowired
	private CrawlingService crawlingService;

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
		crawlingService.startCrawlingUsingUrl("developers.monzo.com");
	}
}
