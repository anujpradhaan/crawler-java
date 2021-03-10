# Crawler Implemented in Spring(Java)

## Problem Statement:
We'd like you to write a simple web crawler in a programming language you're familiar with.
Given a starting URL, the crawler should visit each URL it finds on the same domain.
It should print each URL visited, and a list of links found on that page.
The crawler should be limited to one subdomain - so when you start with *https://abc.com/*,
it would crawl all pages on the abc.com website, but not follow external links,
for example to facebook.com or community.abc.com. 

Ideally, write it as you would a production piece of code.
This exercise is not meant to show us whether you can write code –
we are more interested in how you design software.
This means that we care less about a fancy UI or sitemap format,
and more about how your program is structured: the trade-offs you've made,
what behaviour the program exhibits, and your use of concurrency, test coverage, and so on.

## Edge cases:
1. The crawler should work only for the given sub-domain of the page.
    a. If say base URL is https://abc.website.com/contact-us, then we should only print the pages for https://abc.website.com. Not any other website,
    Even domains like website.com will be rejected as it will not match the provided base URL.
2. Remove Duplicates
3. Ignore all the other sub-domains than the given one.
4. Handle cases where given URL is:
    a. is null
    b. Incorrect URL
    c. blank URL
    d. Valid URL but domain is unreachable.
    e. Just one page is resulting in some HTTP Error.
5. Avoid calling the same page for avoiding infinite loops.
6. IN Anchor Tag, ingore the href's pointing to the ID's of the same page. Because ideally the page will remain the same.
7. Print, images if they are on the same domain, otherwise ignore. Usually, images are on CDN but still an edge case.
8. How to handle relative paths.
    a. What if the base url is "https://abc.com/a/b/c" and one the URL on the page is "../d" which is equal to
    https://abc.com/a/b/d
    b. First check if relative path will result in correct URL.


## Planning:
1. Use Jsoup Library in Java for HTML parsing.
2. Read about various ways to add link to a page.
    a. Anchor tag,
    b. CSS
    c. JavaScript.
3. Figure out how to find domains as well as subdomains from the given URL in Java.


## Steps followed for filtering and Normalizing a URL:
1. Validate the URL, and error in case invalid URL.
2. If valid, Find All LINK tags as well as Anchor tags on the given URL.
3. Strip, Fragment Identifiers from the URL Example: http://www.cnn.com/news/headlines.html#SPORTS
4. Trim URL's or spaces.
5. Re-Construct all the URL's such that the domain is made to lowercase and rest of the URL remains the same.
6. Convert Absolute URL's to Absolute URL's
7. Filter only Unique.
8. Filter URL's belonging to the same domain as in the Given URL.
9. Now repeat the same process.

## Assumptions:
1. Considering the URL's are case sensitives.
2. Maintaining a concurrent hashmap as a local storage to keep track of already visited URL's. This is to avoid any kind of loop.


# How to Run
###  There are 2 implementation of crawler:
* Single threaded -> Available in *master* Branch
* Multi Threaded -> Available in *parallel_kafka* branch

### Pre-requisites:
* No Pre-Requisites for Single Threaded Model.
* Kafka Should be running at localhost:9092 for Multi Threaded Model.

Executables: 
1. java -jar crawler-single-threaded.jar www.monzo.com
2. java -jar crawler-with-kafka.jar www.monzo.com

Test Coverage : 
Single Threaded Model -> 94%
Multi Threaded Model -> 92%
