package com.trauma.youtube;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by traum_000 on 2017-07-05.
 */

public class Youtube {
    private static final String TAG = "YoutubeTest";
    private static final String API_KEY = "AIzaSyBtuo6iaPyREm5jjSJjJkLWyoU6KyRcr9w";
    private static final String LISTID = "PL-GHe9MyP7NwtmTe0dRN6maJlyhz5QGAE";
    //private static final String LISTID = "PLuHgQVnccGMBttjsCipjhWgf6urfjTn14";

    String nextPageToken = "";
    int totalResults;


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                            ": with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[2048];
            while ( (bytesRead = in.read(buffer)) > 0 ) {
                out.write(buffer, 0, bytesRead);
                //Log.i(TAG, "In loop " + bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<YoutubeItem> fetchItems() {
        List<YoutubeItem> items = new ArrayList<>();
        try {
            String url;
            do {
                url = Uri.parse("https://www.googleapis.com/youtube/v3/playlistItems")
                        .buildUpon()
                        .appendQueryParameter("part", "snippet")
                        .appendQueryParameter("pageToken", nextPageToken)
                        .appendQueryParameter("playlistId", LISTID)
                        .appendQueryParameter("key", API_KEY)
                        .build().toString();
                String jsonString = getUrlString(url);
                Log.i(TAG, "Received JSON: " + jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                parseItems(items, jsonObject);
            } while(nextPageToken != null);
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    private void parseItems(List<YoutubeItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        if(jsonBody.has("nextPageToken")) {
            nextPageToken = jsonBody.getString("nextPageToken");
        } else {
            nextPageToken = null;
        }
        totalResults = jsonBody.getJSONObject("pageInfo").getInt("totalResults");
        JSONArray youtubeItems = jsonBody.getJSONArray("items");
        Log.i(TAG, "youtubeItems length : " + youtubeItems.length()
                + " totalResults : " + totalResults);

        for(int i = 0; i < youtubeItems.length(); i++){
            JSONObject object = youtubeItems.getJSONObject(i);
            JSONObject snippet = object.getJSONObject("snippet");
            String title = snippet.getString("title");
            int pos = snippet.getInt("position");

            JSONObject resource = snippet.getJSONObject("resourceId");
            String temp = resource.getString("videoId");
            Log.i(TAG, "Youtube video id : " + temp);

            YoutubeItem item = new YoutubeItem();
            item.setTitle(title);
            item.setPosition(pos);
            item.setVideoId(temp);
            items.add(item);
        }
    }
}
