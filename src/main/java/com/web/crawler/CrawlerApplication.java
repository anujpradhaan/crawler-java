package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import com.web.crawler.exception.NotFoundException;
import com.web.crawler.filter.URLFilteringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class CrawlerApplication implements ApplicationRunner {
	private static final String URL_OPTION = "url";

	private final CrawlingService crawlingService;

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));

		Predicate<String> isUrlParamPresent = url -> URL_OPTION.equalsIgnoreCase(url);

		String url = args.getOptionNames()
				.stream()
				.filter(isUrlParamPresent)
				.map(param -> args.getOptionValues(param))
				.flatMap(Collection::stream)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("URL parameter not passed"));

		log.info("Given URL is {}", url);

		if (URLFilteringService.isInvalidValidUrl(url)) {
			log.error("Invalid URL {}", url);
			throw new MalformedURLException("Invalid Url");
		}
		crawlingService.startCrawlingUsingUrl(url);
	}

}
