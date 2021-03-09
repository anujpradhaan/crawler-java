package com.web.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author : anuj.kumar
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class URLFilteringService {

	private final VisitedLinksService visitedLinksService;

	public Set<String> filter(String url, List<String> urlsToFilter) {
		Assert.notNull(url, "");
		Assert.notNull(urlsToFilter, "");

		Optional<URL> optionalURLObject = getUrlObjectFrom(url);

		if (!optionalURLObject.isPresent()) {
			log.error("Unable to Parse the URL {}", url);
			return Set.of();
		}

		String givenHost = optionalURLObject.get().getHost();

		Predicate<String> isValidUrl = urlFromHTMlDocument -> !CrawlerApplication.isInvalidValidUrl(urlFromHTMlDocument);
		Predicate<URL> isUrlSameAsGivenDomain = urlObject -> givenHost.equalsIgnoreCase(urlObject.getHost());

		return urlsToFilter.stream()
				.filter(isValidUrl)
				.map(this::getUrlObjectFrom)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(isUrlSameAsGivenDomain)
				.map(URL::toString)
				.map(URLFilteringService::removeExtraCharactersFromURL)
				.filter(visitedLinksService::isNotVisited)
				.collect(Collectors.toSet());
	}

	public static String removeExtraCharactersFromURL(String url) {
		/*
		1. Remove Extra / from URL at the end.
		2. Remove fragmentIdentifier from URL because its the same URL like www.facebook.com/auth#SPORTS
		is same as www.facebook.com/auth
		 */
		if (url.contains("#")) {
			url = url.substring(0, url.lastIndexOf('#'));
		}

		if (url.endsWith("/")) {
			url = url.substring(0, url.lastIndexOf('/'));
		}
		return url;
	}

	private Optional<URL> getUrlObjectFrom(String url) {
		try {
			return Optional.of(new URL(url));
		} catch (MalformedURLException e) {
			return Optional.empty();
		}
	}
}
