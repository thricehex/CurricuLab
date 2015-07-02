package com.viriumdev.curriculab.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.ArrayList;

/**
 * APIResponse and parser class
 * 
 * @author Garrett T. Smith
 */
public class APIResponse {

    private final int apiType;
    private final String json;

    List<APIElement> parsedValues = new ArrayList<>();

    public static final int TITLE = 0;

    public APIResponse(int apiType, String json) {

        this.apiType = apiType;
        this.json = json;

        switch (apiType) {
            case APIConnection.DICTIONARY:
                parseDictionaryValue();
                break;
            case APIConnection.BRILLIANT_SERIES:
                parseSeriesValue();
                break;
            case APIConnection.ARTICLE:
                parseArticleValue();
                break;
        }

    }

    protected void parseDictionaryValue() {

        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(json);
        JsonArray array = (JsonArray) obj.get("results");

        for (int i = 0; i < array.size(); i++) {
            JsonObject next = (JsonObject) array.get(i);
            String headword = next.get("headword").toString();
            headword = headword.replaceAll("\\[", "").replaceAll("\\]", "");

            JsonArray child = (JsonArray) next.get("senses");
            JsonObject childObj = (JsonObject) child.get(0);

            String definition = "";
            if (childObj.get("definition") != null) {
                definition = childObj.get("definition").toString();
                definition = definition.replaceAll("\\[", "").replaceAll("\\]", "");

                parsedValues.add(new APIElement(headword, definition));

            }

        }
    }

    protected void parseSeriesValue() {

        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(json);
        JsonArray jArray = (JsonArray) obj.get("articles");

        for (int i = 0; i < jArray.size(); i++) {
            JsonObject next = (JsonObject) jArray.get(i);
            String title = next.get("title").toString();
            title = title.replaceAll("\\[", "").replaceAll("\\]", "");

            String url = next.get("url").toString();
            url = url.replaceAll("\\[", "").replaceAll("\\]", "");

            parsedValues.add(new APIElement(title, url, url));
        }

    }

    protected void parseArticleValue() {

        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(json);
        JsonObject subContent = (JsonObject) obj.get("article");
        String articleBody = subContent.get("content").toString();
        articleBody = articleBody.replaceAll("\"", "");
        parsedValues.add(new APIElement(null, articleBody));
    }

    public List<APIElement> getAllParsedElements() {

        return parsedValues;
    }

}
