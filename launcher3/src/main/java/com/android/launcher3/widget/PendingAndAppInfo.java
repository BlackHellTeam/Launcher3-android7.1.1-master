package com.android.launcher3.widget;

import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.model.PackageItemInfo;

/**
 * Created by wangcunxi on 5/11/17.
 */

public class PendingAndAppInfo extends PendingAddItemInfo {
    public PackageItemInfo mPackageItemInfo;

    public PendingAndAppInfo(PackageItemInfo packageItemInfo) {
        this.mPackageItemInfo = packageItemInfo;
    }

    @Override
    public String toString() {
        return String.format("PendingAndAppInfo package=%s, name=%s",
                mPackageItemInfo.packageName, mPackageItemInfo.title);
    }

}
