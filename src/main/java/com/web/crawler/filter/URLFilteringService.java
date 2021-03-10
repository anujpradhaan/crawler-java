package com.web.crawler.filter;

import com.web.crawler.storage.VisitedLinksService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : anuj.kumar
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class URLFilteringService {

	private static final String REGEX_IGNORE = "([^\\s]+(\\.(?i)(jpe?g|png|gif|bmp|pdf|doc|docx|webmanifest))$)";
	private final Pattern pattern = Pattern.compile(REGEX_IGNORE);

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

		Predicate<URL> isUrlSameAsGivenHost = urlObject -> givenHost.equalsIgnoreCase(urlObject.getHost());

		return urlsToFilter.stream()
				.map(this::getUrlObjectFrom)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(isUrlSameAsGivenHost)
				.map(URL::toString)
				.filter(this::isNonMediaContent)
				.map(URLFilteringService::removeExtraCharactersFromURL)
				.filter(visitedLinksService::isNotVisited)
				.collect(Collectors.toSet());
	}

	private boolean isNonMediaContent(String link) {
		Matcher m = pattern.matcher(link);
		return !m.matches();
	}

	/*
		1. Remove Extra / from URL at the end.
		2. Remove fragmentIdentifier from URL because its the same URL like www.facebook.com/auth#SPORTS
		is same as www.facebook.com/auth
	*/
	public static String removeExtraCharactersFromURL(String url) {
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
			log.error("Malformed Url {}", url);
			return Optional.empty();
		}
	}

	public static boolean isInvalidValidUrl(String url) {
		/* Try creating a valid URL */
		try {
			new URL(url).toURI();
			return false;
		} catch (Exception e) {
			return true;
		}
	}
}
