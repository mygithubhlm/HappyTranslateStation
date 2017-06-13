package com.marktony.translator.interfaze;

import android.view.View;

/**
 * Created by zhaoxinyu on 2017/5/3.
 */

public interface OnRecyclerViewOnClickListener {

    void OnItemClick(View view,int position);

    void OnSubViewClick(View view, int position);

}
