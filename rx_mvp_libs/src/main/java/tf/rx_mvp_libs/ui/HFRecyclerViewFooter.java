package tf.rx_mvp_libs.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import tf.rx_mvp_libs.R;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/10 17:04.
 */

public class HFRecyclerViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public static final int STATE_LOADING = 3;
    public static final int STATE_SUCCESS = 4;
    public static final int STATE_LOAD_FAIL = 5;

    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;
//    private RelativeLayout hfrecyclerview_footer_state;
    private LoadView hfrecyclerview_footer_loadview;

    private Context mContext;


    public HFRecyclerViewFooter (Context context) {
        super (context);
        initView (context);
    }

    public HFRecyclerViewFooter (Context context, AttributeSet attrs) {
        super (context, attrs);
        initView (context);
    }

    public HFRecyclerViewFooter (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        initView (context);
    }


    private void initView (Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from (mContext).inflate (R.layout.hfrecyclerview_footer, null);
        addView (moreView);
        moreView.setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//        hfrecyclerview_footer_state = (RelativeLayout) moreView.findViewById (R.id.hfrecyclerview_footer_state);
//        hfrecyclerview_footer_state.setVisibility (View.GONE);
        mContentView = moreView.findViewById (R.id.hfrecyclerview_footer_content);
        mProgressBar = moreView.findViewById (R.id.hfrecyclerview_footer_progressbar);
        mHintView = (TextView) moreView.findViewById (R.id.hfrecyclerview_footer_hint_textview);
        hfrecyclerview_footer_loadview = (LoadView) moreView.findViewById (R.id.hfrecyclerview_footer_loadview);
    }

    public void setBottomMargin (int height) {
        if (height < 0)
            return;
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams ();
        lp.bottomMargin = height;
        mContentView.setLayoutParams (lp);
    }

    /**
     * 获取view距离底部的距离
     *
     * @return
     */
    public int getBottomMargin () {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams ();
        return lp.bottomMargin;
    }

    public void setState (int state) {
        mHintView.setVisibility (View.GONE);
        mProgressBar.setVisibility (View.GONE);
        if (state == STATE_READY) {
            mHintView.setVisibility (View.VISIBLE);
            mHintView.setText (R.string.hfrecyclerview_footer_hint_ready);
        } else if (state == STATE_LOADING) {
            hfrecyclerview_footer_loadview.startLoad ();
            mProgressBar.setVisibility (View.VISIBLE);
        } else if (state == STATE_SUCCESS) {
            hfrecyclerview_footer_loadview.stopLoad ();
            mHintView.setVisibility (View.GONE);
        } else if (state == STATE_LOAD_FAIL) {
            hfrecyclerview_footer_loadview.stopLoad ();
            mHintView.setText (R.string.hfrecyclerview_footer_load_fail);
            mHintView.setVisibility (VISIBLE);
            mProgressBar.setVisibility (GONE);
        } else {
            mHintView.setVisibility (View.VISIBLE);
            mHintView.setText (R.string.hfrecyclerview_footer_hint_normal);
            mProgressBar.setVisibility (View.GONE);
        }
    }

    public void setFooterText (String text) {
        mHintView.setText (text);
    }
}
