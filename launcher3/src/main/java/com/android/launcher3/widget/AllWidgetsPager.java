package com.android.launcher3.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by wangcunxi on 5/19/17.
 */

public class AllWidgetsPager extends ViewPager {

    private static final String TAG = "AllWidgetsPager";
    public AllWidgetsPager(Context context) {
        this(context, null);
    }

    public AllWidgetsPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
