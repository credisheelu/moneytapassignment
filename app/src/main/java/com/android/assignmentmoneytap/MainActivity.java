package com.android.assignmentmoneytap;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.android.assignmentmoneytap.models.Page;
import com.android.assignmentmoneytap.models.SearchResults;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private SearchView wikiSearch;
    private RecyclerView mRecyclerView;
    private WikiSearchResultsAdapter adapter;

    Handler handler;

    List<Page> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //image loading config
        setConfigForImageLoading();

        //search view
        wikiSearch = findViewById(R.id.searchview);
        //recycker view to hold search results
        mRecyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        pages = new ArrayList<>();
        handler = new Handler();

        //search view on query text change listener
        wikiSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.trim().length() >= 2) {

                    final String query = newText;

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getSearchResults(query);
                        }
                    }, 600);
                }

                return false;
            }
        });
    }

    private void setConfigForImageLoading(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true).cacheInMemory(true).build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(defaultOptions) // default
                .build();

        ImageLoader.getInstance().init(config);
    }

    private void getSearchResults(String query){
        //protocols

        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(interceptor);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();



        Webservice webservice = retrofit.create(Webservice.class);
        Map<String, String> params = new HashMap<>();
        params.put("action","query");
        params.put("format", "json");
        params.put("formatversion", "2");
        params.put("prop", "pageimages|pageterms");
        params.put("generator", "prefixsearch");
        params.put("piprop", "thumbnail");
        params.put("pithumbsize", "50");
        params.put("pilimit", "10");
        params.put("gpssearch", query);
        params.put("gpslimit", "10");


        webservice.getWikiSearchResults(params).enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(@NonNull Call<SearchResults> call, Response<SearchResults> response) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);

                Log.v("response.123", "" + call.isExecuted());

                SearchResults searchResults = response.body();

                if(searchResults !=null && searchResults.getQuery()!=null
                        && searchResults.getQuery().getPages()!=null  &&
                        !searchResults.getQuery().getPages().isEmpty() && mRecyclerView != null){

                    pages.clear();
                    pages.addAll(searchResults.getQuery().getPages());

                    if(adapter == null) {
                        adapter = new WikiSearchResultsAdapter(MainActivity.this, pages);
                        mRecyclerView.setAdapter(adapter);
                    }
                    else
                        adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);

                //response failure
                Log.v("Error.123", t.getMessage());
            }
        });

    }


}
