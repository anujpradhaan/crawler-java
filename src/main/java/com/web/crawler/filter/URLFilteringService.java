package com.web.crawler.filter;

import com.web.crawler.UrlUtils;
import com.web.crawler.storage.VisitedLinksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

		Optional<URL> optionalURLObject = UrlUtils.getUrlObjectFrom(url);

		if (!optionalURLObject.isPresent()) {
			log.error("Unable to Parse the URL {}", url);
			return Set.of();
		}

		String givenHost = optionalURLObject.get().getHost();

		Predicate<URL> isUrlSameAsGivenHost = urlObject -> givenHost.equalsIgnoreCase(urlObject.getHost());

		return urlsToFilter.stream()
				.map(UrlUtils::getUrlObjectFrom)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(isUrlSameAsGivenHost)
				.map(URL::toString)
				.filter(this::isNonMediaContent)
				.map(UrlUtils::removeExtraCharactersFromURL)
				.filter(visitedLinksService::isNotVisited)
				.collect(Collectors.toSet());
	}

	private boolean isNonMediaContent(String link) {
		Matcher m = pattern.matcher(link);
		return !m.matches();
	}
}
