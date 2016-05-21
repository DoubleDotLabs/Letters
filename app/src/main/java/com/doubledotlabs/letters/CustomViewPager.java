package com.doubledotlabs.letters;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class CustomViewPager extends ViewPager {

    PagerAdapter adapter;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setLazyAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isAttachedToWindow()) super.setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter != null) super.setAdapter(adapter);
    }
}
