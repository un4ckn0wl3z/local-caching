package com.anuwat.localcaching.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.anuwat.localcaching.model.Author;
import com.anuwat.localcaching.model.Book;
import com.anuwat.localcaching.util.Util;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;


public class BookService {
    private static LoadingCache<String, Optional<Book>> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .recordStats()
            .build(new CacheLoader<String, Optional<Book>>() {
                @Override
                public Optional<Book> load(String s) throws IOException {
                    return getBookDetailsFromGoogleBooks(s);
                }
            });

    public static Optional<Book> getBookDetails(String isbn13) throws IOException, ExecutionException {
        Optional<Book> book = cache.get(isbn13);
        return book;
    }

    public static CacheStats getCacheStats(){
        return cache.stats();
    }


    private static Optional<Book> getBookDetailsFromGoogleBooks(String isbn13) throws IOException{
        //Properties properties = Util.getProperties();
        //String key = properties.getProperty(Constants.GOOGLE_API_KEY);
    	System.out.println("HIT HERE ONE TIME");
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn13;
        String response = Util.getHttpResponse(url);
        Map bookMap = Util.getObjectMapper().readValue(response,Map.class);
        Object bookDataListObj = bookMap.get("items");
        Book book = null;
        if ( bookDataListObj == null || !(bookDataListObj instanceof List)){
            return Optional.fromNullable(book);
        }

        List bookDataList = (List)bookDataListObj;
        if ( bookDataList.size() < 1){
            return Optional.fromNullable(null);
        }

        Map bookData = (Map) bookDataList.get(0);
        Map volumeInfo = (Map)bookData.get("volumeInfo");
        book = new Book();
        book.setTitle(getFromJsonResponse(volumeInfo,"title",""));
        book.setPublisher(getFromJsonResponse(volumeInfo,"publisher",""));
        List authorDataList = (List)volumeInfo.get("authors");
        for(Object authorDataObj : authorDataList){
            Author author = new Author();
            author.setName(authorDataObj.toString());
            book.addAuthor(author);
        }
        book.setIsbn13(isbn13);
        book.setSummary(getFromJsonResponse(volumeInfo,"description",""));
        book.setPageCount(Integer.parseInt(getFromJsonResponse(volumeInfo, "pageCount", "0")));
        book.setPublishedDate(getFromJsonResponse(volumeInfo,"publishedDate",""));

        return Optional.fromNullable(book);
    }

    private static String getFromJsonResponse(Map jsonData, String key, String defaultValue){
        return Optional.fromNullable(jsonData.get(key)).or(defaultValue).toString();
    }
}
