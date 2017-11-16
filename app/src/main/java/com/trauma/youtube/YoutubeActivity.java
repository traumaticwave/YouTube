package com.trauma.youtube;

import android.support.v4.app.Fragment;

public class YoutubeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return YoutubeFragment.newInstance();
    }
}
