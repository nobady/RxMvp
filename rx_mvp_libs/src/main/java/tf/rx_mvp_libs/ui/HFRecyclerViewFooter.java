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

public class HFRecyclerViewFooter extends LinearLayout {
    public HFRecyclerViewFooter (Context context) {
        super (context);
    }

    public HFRecyclerViewFooter (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public HFRecyclerViewFooter (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    /**
     * 获取view距离底部的距离
     * @return
     */
    public int getBottomMargin () {
        return 0;
    }

    public void setBottomMargin (int height) {

    }
}
