package com.web.crawler.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : anuj.kumar
 **/
@Service
@Slf4j
public class VisitedLinksService {

	private ConcurrentHashMap<String, Boolean> alreadyVisitedLinksStorage = new ConcurrentHashMap<>();

	public boolean markVisited(String link) {
		log.debug("Adding Link {}", link);
		if (isNotVisited(link)) {
			alreadyVisitedLinksStorage.put(link, Boolean.TRUE);
		}
		return Boolean.TRUE;
	}

	public boolean isNotVisited(String link) {
		log.debug("Checking Links existence {}", link);
		return !alreadyVisitedLinksStorage.containsKey(link);
	}
}
