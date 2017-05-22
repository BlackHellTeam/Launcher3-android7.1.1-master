package com.android.launcher3.effect;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;

/**
 * Created by wangcunxi on 5/22/17.
 */

public class Effect {

    private int previewId;
    private int nameId;

    public Effect(Context context, int previewId, int nameId) {
        this.previewId = previewId;
        this.nameId = nameId;
    }

    public int getPreviewId() {
        return previewId;
    }

    public int getNameId() {
        return nameId;
    }

    public void setPreviewId(int previewId) {
        this.previewId = previewId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }
}
