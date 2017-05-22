package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by wangcunxi on 5/19/17.
 */

public class WidgetGridView extends GridView {
    public WidgetGridView(Context context) {
        this(context, null);
    }

    public WidgetGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
