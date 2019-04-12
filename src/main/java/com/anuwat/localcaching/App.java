package com.anuwat.localcaching;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.anuwat.localcaching.model.Book;
import com.anuwat.localcaching.service.BookService;
import com.anuwat.localcaching.util.Util;
import com.google.common.cache.CacheStats;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, ExecutionException {
        Book book = BookService.getBookDetails("9780596009205").get();
        System.out.println(Util.getObjectMapper().writeValueAsString(book));
        book = BookService.getBookDetails("9780596009205").get();
        System.out.println(Util.getObjectMapper().writeValueAsString(book));
        book = BookService.getBookDetails("9780596009205").get();
        System.out.println(Util.getObjectMapper().writeValueAsString(book));
        book = BookService.getBookDetails("9780596009205").get();
        System.out.println(Util.getObjectMapper().writeValueAsString(book));
        book = BookService.getBookDetails("9780596009205").get();
        System.out.println(Util.getObjectMapper().writeValueAsString(book));
        CacheStats cacheStats = BookService.getCacheStats();
        System.out.println(cacheStats.toString());
    }
}
