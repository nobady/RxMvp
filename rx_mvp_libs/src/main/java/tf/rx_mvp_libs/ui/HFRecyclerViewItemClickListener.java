package tf.rx_mvp_libs.ui;

import android.view.View;

/**
 * [Description]
 * <p/>  长按和短按事件监听器
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/10 17:19.
 */

public interface HFRecyclerViewItemClickListener {
    void onItemClick(View v,int position);
    void onLongItemClick(View v,int position);
}
