package com.trauma.youtube;

import android.net.Uri;

/**
 * Created by traum_000 on 2017-07-20.
 */

public class YoutubeItem {
    private String mTitle;
    private int mPosition;
    private String mVideoId;
    private String mThumbnailUrl;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
        mThumbnailUrl = "https://i.ytimg.com/vi/" + mVideoId + "/mqdefault.jpg";
    }

    public String getUrl() {
        return mThumbnailUrl;
    }

    public Uri getVideoUri() {
        return Uri.parse("http://www.youtube.com/embed/")
                .buildUpon()
                .appendPath(mVideoId)
                .appendQueryParameter("showinfo", "0")
                .appendQueryParameter("mdestbranding", "1")
                .appendQueryParameter("frameborder", "0")
                .appendQueryParameter("rel", "0")
                .build();
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
