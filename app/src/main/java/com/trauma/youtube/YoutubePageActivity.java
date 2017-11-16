package com.trauma.youtube;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by traum_000 on 2017-07-29.
 */

public class YoutubePageActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context, Uri youtubePageUri){
        Intent i = new Intent(context, YoutubePageActivity.class);
        i.setData(youtubePageUri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return YoutubePageFragment.newInstance(getIntent().getData());
    }
}
