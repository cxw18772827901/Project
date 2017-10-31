package com.dave.project.wedjet.fresh.api;

import android.content.Context;

/**
 * 默认Header创建器
 * Created by SCWANG on 2017/5/26.
 */

public interface DefaultRefreshHeaderCreater {
    RefreshHeader createRefreshHeader(Context context, RefreshLayout layout);
}
