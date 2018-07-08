package com.android.assignmentmoneytap;

import com.android.assignmentmoneytap.models.SearchResults;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


public interface Webservice {

    //get apollo promo codes
    @GET("w/api.php")
    Call<SearchResults> getWikiSearchResults(@QueryMap Map<String, String> params);
}
