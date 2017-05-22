package com.android.launcher3.effect;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.Launcher;
import com.android.launcher3.PageViewAnimation;
import com.android.launcher3.R;
import com.android.launcher3.widget.WidgetsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by wangcunxi on 5/19/17.
 */

public class EffectContainer extends FrameLayout implements AdapterView.OnItemClickListener{

    private static final String TAG = "EffectContainer";

    private ViewPager effectPager;
    private Launcher mLauncher;
    private List<Effect> effectList;
    private static final int mPageSize = 4;

    public EffectContainer(Context context) {
        this(context, null);
    }

    public EffectContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLauncher = (Launcher)context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        effectPager = (ViewPager)findViewById(R.id.effect_pager);
        effectList = new ArrayList<Effect>();
        effectList.add(new Effect(mLauncher, R.drawable.anim_normal_button, R.string.pageanim_normal));
        effectList.add(new Effect(mLauncher, R.drawable.anim_turntable_button, R.string.pageanim_turntable));
        effectList.add(new Effect(mLauncher, R.drawable.anim_layered_button, R.string.pageanim_layered));
        effectList.add(new Effect(mLauncher, R.drawable.anim_rotate_button, R.string.pageanim_rotate));
        effectList.add(new Effect(mLauncher, R.drawable.anim_pageturn_button, R.string.pageanim_pageturn));
        effectList.add(new Effect(mLauncher, R.drawable.anim_card_button, R.string.pageanim_rotatebylefttoppoint));
        effectList.add(new Effect(mLauncher, R.drawable.anim_compass_button, R.string.pageanim_rotatebycenterpoint));
        effectList.add(new Effect(mLauncher, R.drawable.anim_blocks_button, R.string.pageanim_blocks));
        //loadData();
    }

    public void loadData() {
        int page = (int) Math.ceil(effectList.size()* 1.0 / mPageSize);
        List<View> viewPagerList = new ArrayList<View>();
        viewPagerList.clear();
        for(int i = 0; i < page; i++) {
            GridView effectGrid = (GridView)View.inflate(mLauncher, R.layout.page_item_effect, null);
            effectGrid.setAdapter(new EffectGridAdapter(mLauncher, effectList, i, mPageSize));
            effectGrid.setOnItemClickListener(this);
            Log.v(TAG,"effectGrid = " + effectGrid + ", effectList = " + effectList.size());
            viewPagerList.add(effectGrid);
        }
        effectPager.setAdapter(new WidgetsPagerAdapter(viewPagerList));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.v(TAG, "onItemClick i = " + i + " , l = " + l);
        PageViewAnimation.getInstance().setPageViewAnime((int)l);
        mLauncher.setPageViewAnimaType((int)l);
    }

    class EffectGridAdapter extends BaseAdapter {

        private Launcher adapterLauncher;
        private List<Effect> adapterList;
        private int mIndex;
        private int adapterPageSize;

        public EffectGridAdapter(Launcher launcher, List<Effect> effects, int page, int pageSize) {
            adapterLauncher = launcher;
            adapterList = effects;
            mIndex = page;
            adapterPageSize = pageSize;
            Log.v(TAG,"EffectGridAdapter adapterList = " + adapterList.size() + ", mIndex = " + mIndex
                + ", adapterPageSize = " + adapterPageSize);
        }
        @Override
        public int getCount() {
            int count = adapterList.size() > (mIndex + 1) * adapterPageSize ?
                adapterPageSize : (adapterList.size() - mIndex * adapterPageSize);
            Log.v(TAG,"EffectGridAdapter count = " + count);
            return count;
        }

        @Override
        public Effect getItem(int pos) {
            Log.v(TAG,"EffectGridAdapter count = " + (pos + mIndex * adapterPageSize));
            return adapterList.get(pos + mIndex * adapterPageSize);
        }

        @Override
        public long getItemId(int pos) {
            return pos + mIndex * adapterPageSize;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            EffectViewHolder effectViewHolder = null;
            Log.v(TAG,"EffectGridAdapter getView");
            if(convertView == null) {
                convertView = LayoutInflater.from(adapterLauncher).inflate(R.layout.effect_grid_item,
                        parent, false);
                effectViewHolder = new EffectViewHolder();
                effectViewHolder.imageView = (ImageView)convertView.findViewById(R.id.effect_preview);
                effectViewHolder.textView = (TextView)convertView.findViewById(R.id.effect_name);
                convertView.setTag(effectViewHolder);
            } else {
                effectViewHolder = (EffectViewHolder)convertView.getTag();
            }

            effectViewHolder.imageView.setImageResource(getItem(pos).getPreviewId());
            effectViewHolder.textView.setText(getItem(pos).getNameId());
            Log.v(TAG,"EffectGridAdapter textView = " + effectViewHolder.textView.getText());
            return convertView;
        }
    }

    class EffectViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
