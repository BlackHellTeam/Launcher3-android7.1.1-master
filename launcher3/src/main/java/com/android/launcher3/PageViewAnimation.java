package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

//add by steven zhang 20170105
public class PageViewAnimation {

    private static final String TAG = "PageViewAnimation";

    public static final int PAGEVIEW_ANIMATION_NORMAL 				= 0;//¾­µä¶¯»­
    public static final int PAGEVIEW_ANIMATION_TURNTABLE 			= 1;//×ªÅÌ¶¯»­
    public static final int PAGEVIEW_ANIMATION_LAYERED 				= 2;//²ãµþ¶¯»­
    public static final int PAGEVIEW_ANIMATION_ROTATE 				= 3;//Ðý×ª¶¯»­
    public static final int PAGEVIEW_ANIMATION_PAGETURN 			= 4;//·­Ò³¶¯»­
    public static final int PAGEVIEW_ANIMATION_ROTATEBYLEFTTOPPOINT = 5;//ÈÆ×óÉÏ½ÇÐý×ªµÄ¶¯»­
    public static final int PAGEVIEW_ANIMATION_ROTATEBYCENTERPOINT 	= 6;//ÈÆÖÐÐÄµãÐý×ªµÄ¶¯»­
    public static final int PAGEVIEW_ANIMATION_BLOCKS 				= 7;//ÉèÖÃ·½¿é¶¯»­

    private static ZInterpolator mZInterpolator = new ZInterpolator(0.5f);
    private static AccelerateInterpolator mAlphaInterpolator = new AccelerateInterpolator(0.9f);
    private static DecelerateInterpolator mLeftScreenAlphaInterpolator = new DecelerateInterpolator(4);
    private static PageViewAnimation mInstance;

    private static float CAMERA_DISTANCE = 6500;
    private static float TRANSITION_SCALE_FACTOR = 0.74f;
    private static float TRANSITION_PIVOT = 0.65f;
    private static float TRANSITION_MAX_ROTATION = 24;

    private float mTranslationX = 0f;
    private float mScale = 1f;
    private float mAlpha = 1f;
    private float mRotation = 0f;
    private float mRotationY = 0f;
    private float mPivotX = 0f;
    private float mPivotY = 0f;

    private int mAnimaType = PAGEVIEW_ANIMATION_NORMAL;

    public static PageViewAnimation getInstance() {
        if (mInstance == null) {
            synchronized (PageViewAnimation.class) {
                if (mInstance == null) {
                    mInstance = new PageViewAnimation();
                }
            }
        }

        return mInstance;
    }

    public void setPageViewAnime(int type) {
        mAnimaType = type;
    }

    public int getPageViewAnime() {
        return mAnimaType;
    }

    public void pageViewAnime(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        switch (mAnimaType) {
            case PAGEVIEW_ANIMATION_TURNTABLE:
                mRotation = -TRANSITION_MAX_ROTATION * scrollProgress;
                mPivotX = v.getMeasuredWidth() * 0.5f;
                mPivotY = v.getMeasuredHeight();
                break;
            case PAGEVIEW_ANIMATION_LAYERED:
            {
                float minScrollProgress = Math.min(0, scrollProgress);

                float interpolatedProgress;

                mTranslationX = minScrollProgress * v.getMeasuredWidth();
                interpolatedProgress = mZInterpolator.getInterpolation(Math.abs(minScrollProgress));

                mScale = (1 - interpolatedProgress) + interpolatedProgress * TRANSITION_SCALE_FACTOR;

                if ((scrollProgress < 0)) {
                    mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
                } else {
                    mAlpha = mLeftScreenAlphaInterpolator.getInterpolation(1 - scrollProgress);
                }

            }
            break;
            case PAGEVIEW_ANIMATION_ROTATE:
                mPivotX = v.getMeasuredWidth() * 0.5f;
                mPivotY = v.getMeasuredHeight();

                mRotationY = -90 * scrollProgress;

                mTranslationX = scrollProgress * v.getMeasuredWidth();

                mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
                break;
            case PAGEVIEW_ANIMATION_PAGETURN:
                mPivotX = 0.0f;
                mPivotY = 0.0f;

                mRotationY = -90 * scrollProgress;

                mTranslationX = scrollProgress * v.getMeasuredWidth();

                mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
                break;
            case PAGEVIEW_ANIMATION_ROTATEBYLEFTTOPPOINT:
                mPivotX = 0.0f;
                mPivotY = 0.0f;

                mRotation = -90 * scrollProgress;

                mTranslationX = scrollProgress * v.getMeasuredWidth();

                mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
                break;
            case PAGEVIEW_ANIMATION_ROTATEBYCENTERPOINT:
                mPivotX = v.getMeasuredWidth() * 0.5f;
                mPivotY = v.getMeasuredHeight() * 0.5f;

                mRotation = -90 * scrollProgress;

                mTranslationX = scrollProgress * v.getMeasuredWidth();

                mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
                break;

            case PAGEVIEW_ANIMATION_BLOCKS:
                if (scrollProgress < 0) {
                    mPivotX = 0.0f;
                } else {
                    mPivotX = v.getMeasuredWidth();
                }

                mRotationY = -90 * scrollProgress;
                break;
            case PAGEVIEW_ANIMATION_NORMAL:

            default:
                break;
        }


        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃ×ªÅÌ¶¯»­
     *
     * @param scrollProgress
     * @param view
     */
    public void setTurntableAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        mRotation = -TRANSITION_MAX_ROTATION * scrollProgress;
        mPivotX = v.getMeasuredWidth() * 0.5f;
        mPivotY = v.getMeasuredHeight();

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃ²ãµþ¶¯»­
     *
     * @param scrollProgress
     * @param density
     * @param v
     */
    public void setLayeredAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        float minScrollProgress = Math.min(0, scrollProgress);

        float interpolatedProgress;

        mTranslationX = minScrollProgress * v.getMeasuredWidth();
        interpolatedProgress = mZInterpolator.getInterpolation(Math.abs(minScrollProgress));

        mScale = (1 - interpolatedProgress) + interpolatedProgress * TRANSITION_SCALE_FACTOR;

        if ((scrollProgress < 0)) {
            mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
        } else {
            mAlpha = mLeftScreenAlphaInterpolator.getInterpolation(1 - scrollProgress);
        }

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ¾­µäÄ£Ê½,Ë®Æ½×ó³öÓÒ½ø
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void setNormalAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃÐý×ª¶¯»­
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void  setRotateAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        mPivotX = v.getMeasuredWidth() * 0.5f;
        mPivotY = v.getMeasuredHeight();

        mRotationY = -90 * scrollProgress;

        mTranslationX = scrollProgress * v.getMeasuredWidth();

        mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃ·­Ò³¶¯»­
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void setPageTurnAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        mPivotX = 0.0f;
        mPivotY = 0.0f;

        mRotationY = -90 * scrollProgress;

        mTranslationX = scrollProgress * v.getMeasuredWidth();

        mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃÒÔ×óÉÏ½ÇÎªÖÐÐÄµãµÄÐý×ª
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void setRotateByLeftTopPointAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        mPivotX = 0.0f;
        mPivotY = 0.0f;

        mRotation = -90 * scrollProgress;

        mTranslationX = scrollProgress * v.getMeasuredWidth();

        mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃÒÔÕýÖÐÐÄÎªÖÐÐÄµãµÄÐý×ª
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void setRotateByCenterPointAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        mPivotX = v.getMeasuredWidth() * 0.5f;
        mPivotY = v.getMeasuredHeight() * 0.5f;

        mRotation = -90 * scrollProgress;

        mTranslationX = scrollProgress * v.getMeasuredWidth();

        mAlpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }

    /**
     * ÉèÖÃ·½¿é¶¯»­µÄÐý×ª
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void setBlocksAnim(float scrollProgress, int i, int count, float density, View v) {
        resetAttr(v);

        if (scrollProgress < 0) {
            mPivotX = 0.0f;
        } else {
            mPivotX = v.getMeasuredWidth();
        }

        mRotationY = -90 * scrollProgress;

        overScrollAnimation(scrollProgress, i, count, density, v);

        setViewAttr(v);

        showOrHideView(v);
    }


    /**
     * ÉèÖÃViewµÄÊôÐÔ
     * @param v
     */
    private void setViewAttr(View v) {
        v.setPivotY(mPivotY);
        v.setPivotX(mPivotX);
        v.setRotation(mRotation);
        v.setRotationY(mRotationY);

        v.setTranslationX(mTranslationX);
        v.setScaleX(mScale);
        v.setScaleY(mScale);
        v.setAlpha(mAlpha);
    }

    /**
     * ¸ù¾ÝalphaµÄÖµÀ´ÅÐ¶ÏÊÇ·ñÏÔÊ¾view
     *
     * @param alpha
     * @param v
     */
    private void showOrHideView(View v) {
        if (mAlpha == 0) {
            v.setVisibility(View.INVISIBLE);
        } else if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ÖØÖÃÊôÐÔ
     */
    private void resetAttr(View v) {
        mTranslationX = 0f;
        mScale = 1f;
        mAlpha = 1f;
        mRotation = 0f;
        mRotationY = 0f;
        mPivotX = v.getMeasuredWidth() * 0.5f;
        mPivotY = v.getMeasuredHeight() * 0.5f;
    }

    /**
     * ÉèÖÃpageView×óÓÒÁ½¶ËÊ±µÄ¶¯»­
     *
     * @param scrollProgress
     * @param i
     * @param count
     * @param density
     * @param v
     */
    public void overScrollAnimation(float scrollProgress, int i,
                                    int count, float density, View v) {
        float xPivot = TRANSITION_PIVOT;
        boolean isOverscrollingFirstPage = scrollProgress < 0;
        boolean isOverscrollingLastPage = scrollProgress > 0;

        int pageWidth = v.getMeasuredWidth();
        int pageHeight = v.getMeasuredHeight();

        v.setCameraDistance(density * CAMERA_DISTANCE);

        if (i == 0 && isOverscrollingFirstPage) {
            mPivotX = xPivot * pageWidth;
            mPivotY = pageHeight / 2.0f;
            mRotation = 0f;
            mRotationY = -TRANSITION_MAX_ROTATION * scrollProgress;
            mScale = 1.0f;
            mAlpha = 1.0f;
            mTranslationX = 0f;
        } else if (i == count - 1 && isOverscrollingLastPage) {
            mPivotX = xPivot * pageWidth;
            mPivotY = pageHeight / 2.0f;
            mRotation = 0f;
            mRotationY = -TRANSITION_MAX_ROTATION * scrollProgress;
            mScale = 1.0f;
            mAlpha = 1.0f;
            mTranslationX = 0f;
        }
    }

    private static class ZInterpolator implements TimeInterpolator {
        private float focalLength;

        public ZInterpolator(float foc) {
            focalLength = foc;
        }

        public float getInterpolation(float input) {
            return (1.0f - focalLength / (focalLength + input)) /
                    (1.0f - focalLength / (focalLength + 1.0f));
        }
    }
}
