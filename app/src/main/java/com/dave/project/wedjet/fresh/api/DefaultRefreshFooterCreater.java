package com.dave.project.wedjet.fresh.api;

import android.content.Context;

/**
 * 默认Footer创建器
 * Created by SCWANG on 2017/5/26.
 */

public interface DefaultRefreshFooterCreater {
    RefreshFooter createRefreshFooter(Context context, RefreshLayout layout);
}
