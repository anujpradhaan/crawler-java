package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrawlerApplicationTest {

	private CommandLineExecutor commandLineExecutor;

	@Mock
	private CrawlingService crawlingService;

	@BeforeEach
	public void init() {
		commandLineExecutor = new CommandLineExecutor(crawlingService);
	}

	@Test
	public void testRunningWithoutAnyArgument() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			commandLineExecutor.run();
		});
	}

	@Test
	public void testRunningWithInvalidArgumentAsUrl() throws Exception {
		commandLineExecutor.run("www.facebook.com");
	}

	@Test
	public void testValidUrl() throws Exception {
		//Arrange
		doNothing().when(crawlingService).startCrawlingUsingUrl(any(String.class));

		//Act
		commandLineExecutor.run("https://www.facebook.com");

		//Assert
		verify(crawlingService, times(1)).startCrawlingUsingUrl(any(String.class));
	}
}