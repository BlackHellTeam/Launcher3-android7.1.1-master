package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.launcher3.CellLayout;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragController;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Folder;
import com.android.launcher3.IconCache;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.LauncherStateTransitionAnimation;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.Workspace;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetsModel;

import com.android.launcher3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangcunxi on 5/19/17.
 */

public class WidgetPageContainer extends FrameLayout implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, DragSource {

    private static final String TAG = "WidgetPageContainer";

    private Launcher mLauncher;
    private WidgetsModel mWidgetsModel;
    private AllWidgetsPager mAllWidgetsPager;
    private SubWidgetspager mSubWidgetspager;
    private List<PackageItemInfo> mWidgetsList;
    private List<Object> mSubWidgetsList;
    private final int mPageSize = 3;
    private boolean isInSubView = false;
    private LauncherStateTransitionAnimation mLauncherStateTransitionAnimation;

    private WidgetPreviewLoader mWidgetPreviewLoader;
    private IconCache mIconCache;
    private DragController mDragController;

    public WidgetPageContainer(Context context) {
        this(context, null);
    }

    public WidgetPageContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetPageContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLauncher = (Launcher) context;
        mLauncherStateTransitionAnimation = new LauncherStateTransitionAnimation(mLauncher);
        mIconCache = LauncherAppState.getInstance().getIconCache();
        mDragController = mLauncher.getDragController();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAllWidgetsPager = (AllWidgetsPager)findViewById(R.id.all_widget_page);
        mSubWidgetspager = (SubWidgetspager)findViewById(R.id.sub_widget_page);
    }

    public void setWidgetsModel(WidgetsModel model) {
        mWidgetsModel = model;
    }

    public boolean isInSubView() {
        return isInSubView;
    }

    public void loadData() {
        isInSubView = false;
        mWidgetsList = mWidgetsModel.getPackageItemInfos();
        int totalWidgetPage = (int) Math.ceil(mWidgetsList.size()* 1.0 / mPageSize);
        List<View> viewPagerList = new ArrayList<View>();
        viewPagerList.clear();
        for (int i = 0; i < totalWidgetPage; i++) {
            WidgetGridView gridView = (WidgetGridView)View.inflate(mLauncher,
                    R.layout.pager_item, null);
            Log.v(TAG,"gridview width = " + gridView.getWidth() + ", " + gridView.getLayoutParams());
            gridView.setAdapter(new WidgetGridViewAdapter(mLauncher, mWidgetsList, null, i,
                    mPageSize, mWidgetsModel, gridView.getColumnWidth()));
            gridView.setOnItemClickListener(this);
            gridView.setOnItemLongClickListener(this);
            viewPagerList.add(gridView);
        }
        mAllWidgetsPager.setAdapter(new WidgetsPagerAdapter(viewPagerList));
        //mAllWidgetsPager.setVisibility(View.VISIBLE);
        //mSubWidgetspager.setVisibility(View.INVISIBLE);
    }

    public void loadSubWidgetData(PackageItemInfo packageItemInfo) {
        isInSubView = true;
        mSubWidgetsList = mWidgetsModel.getSortedWidgets(packageItemInfo);
        int totalWidgetPage = (int) Math.ceil(mSubWidgetsList.size()* 1.0 / mPageSize);
        List<View> viewPagerList = new ArrayList<View>();
        viewPagerList.clear();
        for (int i = 0; i < totalWidgetPage; i++) {
            WidgetGridView gridView = (WidgetGridView)View.inflate(mLauncher,
                    R.layout.pager_item, null);
            int subPageSize = mPageSize;
            if(mSubWidgetsList.size() < mPageSize) {
                subPageSize = mSubWidgetsList.size();
                gridView.setNumColumns(subPageSize);
            }
            Log.v(TAG, "lmk mSubWidgetsList = " + mSubWidgetsList.size());
            gridView.setAdapter(new WidgetGridViewAdapter(mLauncher, null, mSubWidgetsList, i,
                    subPageSize, mWidgetsModel, gridView.getColumnWidth()));
            gridView.setOnItemClickListener(this);
            gridView.setOnItemLongClickListener(this);
            viewPagerList.add(gridView);
        }
        Log.v(TAG, "lmk viewPagerList = " + viewPagerList.size());
        mSubWidgetspager.setAdapter(new WidgetsPagerAdapter(viewPagerList));
        mLauncherStateTransitionAnimation.animationMenuEasy(mAllWidgetsPager, mSubWidgetspager, true);
    }

    public void onBackPressed() {
        if(isInSubView) {
            isInSubView = false;
            mLauncherStateTransitionAnimation.animationMenuEasy(mSubWidgetspager, mAllWidgetsPager, true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(view.getTag() instanceof PendingAndAppInfo) {
            mAllWidgetsPager.setVisibility(View.GONE);
            loadSubWidgetData(((PendingAndAppInfo)view.getTag()).mPackageItemInfo);
        } else {
            boolean showOutOfSpaceMessage = false;
            int currentScreen = mLauncher.getCurrentWorkspaceScreen();
            Log.v(TAG,"currentScreen = " + currentScreen );
            Workspace workspace = mLauncher.getWorkspace();
            CellLayout layout = (CellLayout) workspace.getChildAt(currentScreen);
            PendingAddItemInfo itemInfo = (PendingAddItemInfo)view.getTag();
            int[] targetCell = new int[2];
            if (layout != null) {
                showOutOfSpaceMessage =
                        !layout.findCellForSpan(targetCell, itemInfo.spanX, itemInfo.spanY);
                Log.v(TAG,"targetCell = " + targetCell[0] + ", " + targetCell[1]
                        + ", spanX = " + itemInfo.spanX + ", spanY = " + itemInfo.spanY);
            }
            if(showOutOfSpaceMessage) {
                mLauncher.showOutOfSpaceMessage(false);
            } else {
                Log.v(TAG,"onItemClick  addPendingItem ");
                mLauncher.addPendingItem(itemInfo, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                        currentScreen, targetCell, itemInfo.spanX, itemInfo.spanY);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        boolean status  = false;
        if(view.getTag() instanceof PendingAndAppInfo) {
            mAllWidgetsPager.setVisibility(View.GONE);
            loadSubWidgetData(((PendingAndAppInfo)view.getTag()).mPackageItemInfo);
        } else {
            if (!mLauncher.isDraggingEnabled()) return false;
            status = beginDragging(view);
        }
        return status;
    }

    private boolean beginDragging(View v) {
        if (!beginDraggingWidget(v)) {
            return false;
        }
        if (mLauncher.getDragController().isDragging()) {
            // Go into spring loaded mode (must happen before we startDrag())
            mLauncher.enterSpringLoadedDragModeForWidget();
        }
        return false;
    }

    private boolean beginDraggingWidget(View v) {
        ImageView image = (ImageView)v.findViewById(R.id.widget_preview);
        PendingAddItemInfo createItemInfo = (PendingAddItemInfo) v.getTag();

        if (image.getDrawable() == null) {
            return false;
        }

        // Compose the drag image
        Bitmap preview;
        float scale = 1f;
        final Rect bounds = image.getDrawable().getBounds();

        if (createItemInfo instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo createWidgetInfo = (PendingAddWidgetInfo) createItemInfo;
            int[] size = mLauncher.getWorkspace().estimateItemSize(createWidgetInfo, true);

            float minScale = 1.25f;
            int maxWidth = Math.min((int) (image.getWidth() * minScale), size[0]);

            int[] previewSizeBeforeScale = new int[1];
            preview = getWidgetPreviewLoader().generateWidgetPreview(mLauncher,
                    createWidgetInfo.info, maxWidth, null, previewSizeBeforeScale);

        } else  {
            PendingAddShortcutInfo createShortcutInfo = (PendingAddShortcutInfo) v.getTag();
            Drawable icon = mIconCache.getFullResIcon(createShortcutInfo.activityInfo);
            preview = Utilities.createIconBitmap(icon, mLauncher);
            createItemInfo.spanX = createItemInfo.spanY = 1;
            scale = ((float) mLauncher.getDeviceProfile().iconSizePx) / preview.getWidth();
        }
        // Don't clip alpha values for the drag outline if we're using the default widget preview
        boolean clipAlpha = !(createItemInfo instanceof PendingAddWidgetInfo &&
                (((PendingAddWidgetInfo) createItemInfo).previewImage == 0));

        // Start the drag
        mLauncher.lockScreenOrientation();
        mLauncher.getWorkspace().onDragStartedWithItem(createItemInfo, preview, clipAlpha);
        mDragController.startDrag(image, preview, this, createItemInfo,
                bounds, DragController.DRAG_ACTION_COPY, scale);

        preview.recycle();
        return true;
    }

    @Override
    public boolean supportsFlingToDelete() {
        return false;
    }

    @Override
    public boolean supportsAppInfoDropTarget() {
        return false;
    }

    @Override
    public boolean supportsDeleteDropTarget() {
        return false;
    }

    @Override
    public float getIntrinsicIconScaleFactor() {
        return 0;
    }

    @Override
    public void onFlingToDeleteCompleted() {

    }

    @Override
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        Log.v(TAG,"onDropCompleted");

        if (isFlingToDelete || !success || (target != mLauncher.getWorkspace() &&
                !(target instanceof DeleteDropTarget) && !(target instanceof Folder))) {
            // Exit spring loaded mode if we have not successfully dropped or have not handled the
            // drop in Workspace
            mLauncher.exitSpringLoadedDragModeDelayed(true,
                    Launcher.EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT, null);
        }
        mLauncher.unlockScreenOrientation(false);

        if (!success) {
            boolean showOutOfSpaceMessage = false;
            if (target instanceof Workspace) {
                int currentScreen = mLauncher.getCurrentWorkspaceScreen();
                Workspace workspace = mLauncher.getWorkspace();
                CellLayout layout = (CellLayout) workspace.getChildAt(currentScreen);
                PendingAddItemInfo itemInfo = (PendingAddItemInfo) d.dragInfo;
                int[] targetCell = new int[2];
                if (layout != null) {
                    showOutOfSpaceMessage =
                            !layout.findCellForSpan(null, itemInfo.spanX, itemInfo.spanY);
                }
                Log.v(TAG,"showOutOfSpaceMessage = " + showOutOfSpaceMessage);
                Log.v(TAG,"targetCell = " + targetCell[0] + ", " + targetCell[1]
                        + ", spanX = " + itemInfo.spanX + ", spanY = " + itemInfo.spanY);
                if (showOutOfSpaceMessage) {
                    mLauncher.showOutOfSpaceMessage(false);
                }
            }
            d.deferDragViewCleanupPostAnimation = false;
        }
    }

    private WidgetPreviewLoader getWidgetPreviewLoader() {
        if (mWidgetPreviewLoader == null) {
            mWidgetPreviewLoader = LauncherAppState.getInstance().getWidgetCache();
        }
        return mWidgetPreviewLoader;
    }
}
