package com.android.launcher3.widget;

import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.IconCache;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetsModel;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

/**
 * Created by wangcunxi on 5/19/17.
 */

public class WidgetGridViewAdapter extends BaseAdapter {

    private static final String TAG = "WidgetGridViewAdapter";

    private Launcher mLauncher;
    private List<PackageItemInfo> mList;
    private List<Object> mSubList;
    private int mIndex;
    private int mPargerSize;
    private WidgetsModel mWidgetsModel;
    private String mDimensionsFormatString;
    private final AppWidgetManagerCompat mWidgetManager;
    private IconCache mIconCache;
    private WidgetPreviewLoader mWidgetPreviewLoader;
    private int previewWidth;

    public WidgetGridViewAdapter(Launcher launcher, List<PackageItemInfo> list, List<Object> subList,
                                 int index, int pargerSize, WidgetsModel model, int width) {
        mLauncher = launcher;
        mList = list;
        mSubList = subList;
        mIndex = index;
        mPargerSize = pargerSize;
        mWidgetsModel = model;
        mDimensionsFormatString = mLauncher.getResources().getString(R.string.widget_dims_format);
        mWidgetManager = AppWidgetManagerCompat.getInstance(mLauncher);
        mIconCache = LauncherAppState.getInstance().getIconCache();
        previewWidth = mLauncher.getResources().getDimensionPixelSize(R.dimen.widget_preview_image_width);
    }

    @Override
    public int getCount() {
        int count = 0;
        if(mList != null) {
            count = mList.size() > (mIndex + 1) * mPargerSize ?
                    mPargerSize : (mList.size() - mIndex * mPargerSize);
        } else {
            count = mSubList.size() > (mIndex + 1) * mPargerSize ?
                    mPargerSize : (mSubList.size() - mIndex * mPargerSize);
        }
        return count;
    }

    @Override
    public Object getItem(int pos) {
        if(mList != null) {
            return (Object)(mList.get(pos + mIndex * mPargerSize));
        } else {
            Log.v(TAG,"lmk pos + mIndex * mPargerSize");
            return mSubList.get(pos + mIndex * mPargerSize);
        }
    }

    @Override
    public long getItemId(int pos) {
        return pos + mIndex * mPargerSize;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mLauncher)
                    .inflate(R.layout.widget_gridview_item, viewGroup, false);
        }
        ImageView preview= (ImageView) convertView.findViewById(R.id.widget_preview);
        TextView name = (TextView)convertView.findViewById(R.id.widget_name);
        TextView dims = (TextView) convertView.findViewById(R.id.widget_dims);
        Object widget = getItem(pos);

        PendingAddItemInfo createItemInfo = null;
        if(widget instanceof PackageItemInfo) {
            List<Object> widgetList = mWidgetsModel.getSortedWidgets(pos + mIndex * mPargerSize);
            if(widgetList.size() > 1) {
                name.setText(((PackageItemInfo) widget).title);
                dims.setText(mWidgetsModel.getSortedWidgets(pos + mIndex * mPargerSize).size() + "");
                createItemInfo = new PendingAndAppInfo((PackageItemInfo)widget);
            } else {
                if(widgetList.get(0) instanceof LauncherAppWidgetProviderInfo) {
                    LauncherAppWidgetProviderInfo info
                            = (LauncherAppWidgetProviderInfo)widgetList.get(0);
                    createItemInfo = new PendingAddWidgetInfo(mLauncher, info, null);
                    name.setText(AppWidgetManagerCompat.getInstance(mLauncher).loadLabel(info));
                    InvariantDeviceProfile profile =
                            LauncherAppState.getInstance().getInvariantDeviceProfile();
                    int hSpan = Math.min(info.spanX, profile.numColumns);
                    int vSpan = Math.min(info.spanY, profile.numRows);
                    dims.setText(String.format(mDimensionsFormatString, hSpan, vSpan));
                } else if(widgetList.get(0) instanceof ResolveInfo) {
                    ResolveInfo info = (ResolveInfo)widgetList.get(0);
                    createItemInfo = new PendingAddShortcutInfo(info.activityInfo);
                    name.setText(info.loadLabel(mLauncher.getPackageManager()));
                    dims.setText(String.format(mDimensionsFormatString, 1, 1));
                }
            }
            widget = widgetList.get(0);
        } else if(widget instanceof LauncherAppWidgetProviderInfo) {
            LauncherAppWidgetProviderInfo info
                    = (LauncherAppWidgetProviderInfo)widget;
            createItemInfo = new PendingAddWidgetInfo(mLauncher, info, null);
            name.setText(AppWidgetManagerCompat.getInstance(mLauncher).loadLabel(info));
            InvariantDeviceProfile profile =
                    LauncherAppState.getInstance().getInvariantDeviceProfile();
            int hSpan = Math.min(info.spanX, profile.numColumns);
            int vSpan = Math.min(info.spanY, profile.numRows);
            dims.setText(String.format(mDimensionsFormatString, hSpan, vSpan));
        } else if(widget instanceof ResolveInfo) {
            ResolveInfo info = (ResolveInfo)widget;
            createItemInfo = new PendingAddShortcutInfo(info.activityInfo);
            name.setText(info.loadLabel(mLauncher.getPackageManager()));
            dims.setText(String.format(mDimensionsFormatString, 1, 1));
        }

        if((widget != null) && (getWidgetPreviewLoader() != null)) {
            Log.v(TAG,"previewWidth = " + preview.getWidth() + ", height = " + preview.getHeight());
            WidgetPreviewLoaderTask previewTask
                    = new WidgetPreviewLoaderTask(preview,widget,previewWidth,previewWidth);
            previewTask.execute();
        }
        Log.v(TAG,"lmk name = " + name.getText().toString());
        convertView.setTag(createItemInfo);
        return convertView;
    }


    class WidgetPreviewLoaderTask extends AsyncTask<Void, Void, Bitmap> {

        private ImageView mImageView;
        private Object mInfo;
        private int mWidth;
        private int mHeight;

        public WidgetPreviewLoaderTask(ImageView imageView, Object info, int width, int height) {
            mImageView = imageView;
            mInfo = info;
            mWidth = width;
            mHeight = height;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap preview = null;
            Log.v(TAG,"mWidtth = " + mWidth + ", mHeight = " + mHeight);
            if (mInfo instanceof LauncherAppWidgetProviderInfo) {
                //preview = mWidgetManager.loadPreview((LauncherAppWidgetProviderInfo)mInfo);
                preview = mWidgetPreviewLoader.generateWidgetPreview(mLauncher, (LauncherAppWidgetProviderInfo)mInfo,
                        mWidth, preview, null);
                /*if(preview == null) {
                    preview = mWidgetManager.loadIcon((LauncherAppWidgetProviderInfo)mInfo, mIconCache);
                }*/
                if(preview == null) {
                    Log.v(TAG,"preview = null for LauncherAppWidgetProviderInfo and mInfo = "
                            + mInfo + ", previewImage = " + ((LauncherAppWidgetProviderInfo)mInfo).previewImage);
                }
            } else {
                //preview = mIconCache.getFullResIcon(((ResolveInfo)mInfo).activityInfo);
                preview = mWidgetPreviewLoader.generateShortcutPreview(mLauncher, (ResolveInfo)mInfo, (int)(mWidth*0.9), (int)(mWidth*0.9), preview);
                if(preview == null) {
                    Log.v(TAG,"preview = null for ResolveInfo and mInfo = "
                            + mInfo);
                }
            }
            return preview;
        }

        @Override
        protected void onPostExecute(Bitmap preview) {
            mImageView.setImageBitmap(preview);
        }

    }

    private WidgetPreviewLoader getWidgetPreviewLoader() {
        if (mWidgetPreviewLoader == null) {
            mWidgetPreviewLoader = LauncherAppState.getInstance().getWidgetCache();
        }
        return mWidgetPreviewLoader;
    }
}
