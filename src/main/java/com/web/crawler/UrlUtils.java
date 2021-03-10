package com.web.crawler;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * @author : anuj.kumar
 **/
@UtilityClass
@Slf4j
public class UrlUtils {
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

	public static Optional<URL> getUrlObjectFrom(String url) {
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
			new URL(url);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public static String normalizeUrl(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		url = url.replace("//www.", "//.");
		url = "http://".concat(url);
		return UrlUtils.removeExtraCharactersFromURL(url);
	}
}
