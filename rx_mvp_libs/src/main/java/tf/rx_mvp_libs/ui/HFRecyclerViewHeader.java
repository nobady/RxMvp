package tf.rx_mvp_libs.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/10 17:04.
 */

public class HFRecyclerViewHeader extends LinearLayout {
    public HFRecyclerViewHeader (Context context) {
        super (context);
    }

    public HFRecyclerViewHeader (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public HFRecyclerViewHeader (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    /**
     * 更新头部的文字信息
     */
    public void refreshHeadViewTextValue () {
    }

    /**
     * 获取view可见的高度
     * @return
     */
    public int getVisibleHeight () {
        return 0;
    }

    /**
     * 设置view的可见高度
     * @param height
     */
    public void setVisibleHeight (int height) {
    }

    public int getHeadViewContentHeight () {
        return 0;
    }

    /**
     * 设置箭头状态和文字信息
     * @param state
     */
    public void setState (int state) {

    }
}
