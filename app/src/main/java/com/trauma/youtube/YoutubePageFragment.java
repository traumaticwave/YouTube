package com.trauma.youtube;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by traum_000 on 2017-07-29.
 */

public class YoutubePageFragment extends Fragment {
    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;

    public static YoutubePageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        YoutubePageFragment fragment = new YoutubePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_youtube_page, container, false);

        mWebView = (WebView) v.findViewById(R.id.fragment_youtube_page_webview);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        String last = "<html><head><style type=\"text/css\">body {background-color: transparent;"
                + "color: white;}</style></head><body style=\"margin:0\">"
                + "<iframe frameBorder=\"0\""
                + "height=\"auto\" width=\"100%\""
                + "src=" + mUri.toString() + "\"></iframe></body></html>";
        mWebView.loadData(last, "text/html", "UTF-8");

        return v;
    }

    @Override public void onPause() {
        super.onPause();
        if (mWebView != null){
            try {
                Class.forName("android.webkit.WebView")
                        .getMethod("onPause", (Class[]) null)
                        .invoke(mWebView, (Object[]) null);
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch(NoSuchMethodException nsme) {
                nsme.printStackTrace();
            } catch(InvocationTargetException ite) {
                ite.printStackTrace();
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
        }
    }
}
