package com.mei.daam.soundsync;

import com.google.api.services.youtube.model.SearchResultSnippet;

/**
 * Created by D01 on 30/03/2018.
 */

public class SearchResultObject {

    private String videoId;
    private SearchResultSnippet searchResultSnippet;

    public SearchResultObject(String videoId, SearchResultSnippet searchResultSnippet) {

        this.videoId = videoId;
        this.searchResultSnippet = searchResultSnippet;
    }

    public String getVideoId() {
        return videoId;
    }

    public SearchResultSnippet getSearchResultSnippet() {
        return searchResultSnippet;
    }
}
