package com.android.assignmentmoneytap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.assignmentmoneytap.models.Page;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.List;

public class WikiSearchResultsAdapter  extends RecyclerView.Adapter<WikiSearchResultsAdapter.ViewHolder> {

    private List<Page> pages;
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;

    public WikiSearchResultsAdapter(Context context, List<Page> pages) {
        this.context = context;
        this.pages = pages;
        imageLoader = ImageLoader.getInstance();
        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .build();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextViewTitle;
        TextView mTextViewInfo;
        ImageView imgView;

        public ViewHolder(View v) {
            super(v);
            mTextViewTitle = v.findViewById(R.id.title_txv);
            mTextViewInfo = v.findViewById(R.id.info_txv);
            imgView = v.findViewById(R.id.imgview);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wiki_search_result, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //reser view
        holder.mTextViewInfo.setText(null);
        holder.mTextViewTitle.setText(null);

        Page page = pages.get(position);
        String title = page.getTitle();

        //get info small description
        if(page.getTerms() != null) {
            if(page.getTerms().getDescription() !=null && !page.getTerms().getDescription().isEmpty()) {
                String info = page.getTerms().getDescription().get(0);
                holder.mTextViewInfo.setText(info);
            }
        }

        holder.mTextViewTitle.setText(title);

        title = title.replaceAll(" ", "_");

        final String link = "https://en.wikipedia.org/wiki/"+title;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showSearchListing(link);
            }
        });

        if(page.getThumbnail() != null) {
            String img_url = page.getThumbnail().getSource();
            imageLoader.displayImage(img_url, holder.imgView, displayImageOptions);
        }


    }


    private void showSearchListing(String link) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("URL", link);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

}
