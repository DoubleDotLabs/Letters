package com.doubledotlabs.letters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

    Activity activity;

    public TutorialPagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Bundle args = new Bundle();
            args.putInt("image", R.drawable.ic_gesture);
            args.putString("title", activity.getString(R.string.tutorial_title_one));
            args.putString("content", activity.getString(R.string.tutorial_content_one));
            Fragment f = new TutorialFragment();
            f.setArguments(args);
            return f;
        } else if (position == 1) {
            Bundle args = new Bundle();
            args.putInt("image", R.drawable.ic_quote);
            args.putString("title", activity.getString(R.string.tutorial_title_two));
            args.putString("content", activity.getString(R.string.tutorial_content_two));
            Fragment f = new TutorialFragment();
            f.setArguments(args);
            return f;
        } else {
            return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
