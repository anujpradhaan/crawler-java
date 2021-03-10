package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.Arrays;

/**
 * @author : anuj.kumar
 **/
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class CommandLineExecutor implements CommandLineRunner {
	private final CrawlingService crawlingService;

	@Override
	public void run(String... args) throws Exception {
		log.info("Application started with command-line arguments: {}", Arrays.toString(args));

		if (args.length < 1) {
			log.error("No URL passed as argument");
			throw new IllegalArgumentException("No URL passed as argument");
		}

		String url = UrlUtils.normalizeUrl(args[0]);

		if (UrlUtils.isInvalidValidUrl(url)) {
			log.error("Invalid URL {}", url);
			throw new MalformedURLException("Invalid Url");
		}
		crawlingService.startCrawlingUsingUrl(url);
	}

}
