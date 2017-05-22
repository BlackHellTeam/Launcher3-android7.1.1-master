package com.android.launcher3.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wangcunxi on 5/19/17.
 */

public class WidgetsPagerAdapter extends PagerAdapter {
    private List<View> viewLists ;

    public WidgetsPagerAdapter(List<View> viewLists) {
        super();
        this.viewLists = viewLists;
    }
    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
