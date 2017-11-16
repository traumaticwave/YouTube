package com.trauma.youtube;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by traum_000 on 2017-07-05.
 */

public class YoutubeFragment extends Fragment {
    private static final String TAG = "YoutubeFragment";
    private RecyclerView mYoutubeRecyclerView;
    private List<YoutubeItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<YoutubeHolder> mThumbnailDownloader;

    public static YoutubeFragment newInstance() {
        return new YoutubeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
            new ThumbnailDownloader.ThumbnailDownloadListener<YoutubeHolder>() {
                @Override
                public void onThumbnailDownloaded(YoutubeHolder holder, Bitmap bitmap) {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    holder.bindDrawable(drawable);
                }
            }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_youtube, container, false);

        mYoutubeRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_youtube_recycler_view);
        mYoutubeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if(isAdded()) {
            mYoutubeRecyclerView.setAdapter(new YoutubeAdapter(mItems));
        }
    }

    private class YoutubeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mView;
        private TextView mTitle;
        private YoutubeItem mItem;

        public YoutubeHolder(View itemView) {
            super(itemView);
            mView = (ImageView) itemView.findViewById(R.id.fragment_youtube_image_view);
            mView.setOnClickListener(this);
            mTitle = (TextView) itemView.findViewById(R.id.fragment_youtube_title);
        }

        public void bindDrawable(Drawable drawable) {
            mView.setImageDrawable(drawable);
            //mView.setContentDescription(mItem.getTitle());
            mTitle.setText("Trauma World");
        }

        public void bindYoutubeItem(YoutubeItem item) {
            mItem = item;
        }

        @Override
        public void onClick(View v) {
            Intent i = YoutubePageActivity.newIntent(getActivity(), mItem.getVideoUri());
            startActivity(i);
        }
    }

    private class YoutubeAdapter extends RecyclerView.Adapter<YoutubeHolder> {
        private List<YoutubeItem> mYoutubeItems;
        public YoutubeAdapter(List<YoutubeItem> youtubeItems) {
            mYoutubeItems = youtubeItems;
        }

        @Override
        public YoutubeHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.youtube_item, viewGroup, false);
            return new YoutubeHolder(view);
        }

        @Override
        public void onBindViewHolder(YoutubeHolder holder, int position) {
            YoutubeItem youtubeItem = mItems.get(position);
            holder.bindYoutubeItem(youtubeItem);
            Drawable placeholder = getResources().getDrawable(R.drawable.yellow);
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, youtubeItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mYoutubeItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<YoutubeItem>> {
        @Override
        protected List<YoutubeItem> doInBackground(Void... params) {
            return new Youtube().fetchItems();
        }

        @Override
        protected void onPostExecute(List<YoutubeItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
