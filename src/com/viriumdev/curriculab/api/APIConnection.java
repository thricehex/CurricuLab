package com.viriumdev.curriculab.api;

import java.io.*;

import java.util.*;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;

/**
 * Class for forming a connection to Pearson API's
 * 
* @author Garrett T. Smith
 */
public class APIConnection {

    public final static int DICTIONARY = 0;
    public final static int BRILLIANT_SERIES = 1;
    public final static int ARTICLE = 2;

    private String baseUrl = "https://api.pearson.com/";
    private final String apiKey = "&apikey=ew3mi87urBtRq3fgRRwNeT9pddywZkfA";

    public final static String SEARCH = "search=";
    public final static String SYNONYMS = "synonyms=";
    public final static String RELATED = "related_words=";
    public final static String HEADWORD = "headword=";
    public final static String TITLE = "title=";
    public final static String CONTENT = "content=";

    private HttpClient client;
    private int api;

    public APIConnection(int apiType) {
        api = apiType;
        switch (apiType) {

            case DICTIONARY:
                baseUrl = baseUrl.concat("v2/dictionaries/entries?");
                break;

            case BRILLIANT_SERIES:
                baseUrl = baseUrl.concat("pearson-education/brilliant/v1/articles?");
                break;
        }

        client = new DefaultHttpClient();
    }

    public APIResponse executeQuery(String queryType, String... keywords) {
        String query = baseUrl;

        query = query.concat(queryType);
        for (int i = 0; i < keywords.length; i++) {
            if (i != keywords.length - 1) {
                query = query.concat(keywords[i] + ",");
            } else {
                query = query.concat(keywords[i]);
            }

        }
        query = query.concat(apiKey);
        //  String jsonStripped = jsonStr.replaceAll("[^A-Za-z0-9 ]", "");
        return rawQuery(query);
    }

    public APIResponse rawQuery(String query) {
        String jsonStr = "";

        HttpGet getRequest = new HttpGet(query);

        try {
            HttpResponse response = client.execute(getRequest);
            HttpEntity respEntity = response.getEntity();
            BufferedInputStream in = new BufferedInputStream(respEntity.getContent());

            int bytesRead = 0;
            byte[] buffer = new byte[2048];

            while ((bytesRead = in.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, bytesRead);
                jsonStr = jsonStr.concat(chunk);

            }

        } catch (IOException ioe) {
            System.err.println("API Request Failed!");
            ioe.printStackTrace();
        }
        return new APIResponse(api, jsonStr);
    }

}
