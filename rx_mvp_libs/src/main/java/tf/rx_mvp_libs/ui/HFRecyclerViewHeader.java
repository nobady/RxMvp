package tf.rx_mvp_libs.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tf.rx_mvp_libs.R;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/10 17:04.
 */

public class HFRecyclerViewHeader extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public static final int STATE_REFRESH = 3;
    public static final int STATE_SUCCESS = 4;
    public static final int STATE_REFRESH_FAIL = 5;
    private final int ROTATE_ANIM_DURATION = 180;

    private int mState = 0;
    private LinearLayout container;
    private TextView headHintTv;
    private TextView headerTimeTv;
    private ImageView arrowIv;
    private RelativeLayout contentLayout;
    //箭头动画
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;
    private LoadView mProgressBar;


    public HFRecyclerViewHeader (Context context) {
        super (context);
        init (context);
    }

    public HFRecyclerViewHeader (Context context, AttributeSet attrs) {
        super (context, attrs);
        init (context);
    }

    public HFRecyclerViewHeader (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        init (context);

    }

    private void init (Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        container = (LinearLayout) LayoutInflater.from (context).inflate (R.layout.hfrecyclerview_header, null,false);

        LinearLayout.LayoutParams lp = new LayoutParams (LayoutParams.MATCH_PARENT, 0);
        addView (container, lp);
        setGravity (Gravity.BOTTOM);

        headHintTv = (TextView) findViewById (R.id.hfrecyclerview_header_hint_textview);
        headerTimeTv = (TextView) findViewById (R.id.hfrecyclerview_header_time);
        arrowIv = (ImageView) findViewById (R.id.hfrecyclerview_header_arrow);
        contentLayout = (RelativeLayout) findViewById (R.id.hfrecyclerview_header_content);
        mProgressBar = (LoadView) findViewById (R.id.hfrecyclerview_header_progressbar);

        mRotateUpAnim = new RotateAnimation (0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation (-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

    }

    /**
     * 一分钟的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * 一月的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;

    /**
     * 一年的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    private SharedPreferences preferences;

    /**
     * 上次更新时间的毫秒值
     */
    private long lastUpdateTime;
    /**
     * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
     */
    private int mId = -1;

    /**
     * 上次更新时间的字符串常量，用于作为SharedPreferences的键值
     */
    private static final String UPDATED_AT = "updated_at";

    /**
     * 更新头部的文字信息
     * 从sp里面读取上次更新的时间，然后和当前时间进行比较
     */
    public void refreshHeadViewTextValue () {

        lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(getResources().getString(R.string.updated_at),

                    value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(getResources().getString(R.string.updated_at),

                    value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(getResources().getString(R.string.updated_at),

                    value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(getResources().getString(R.string.updated_at),

                    value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(getResources().getString(R.string.updated_at),

                    value);
        }
        headerTimeTv.setText(updateAtValue);
    }

    /**
     * 获取view可见的高度
     *
     * @return
     */
    public int getVisibleHeight () {
        return container.getLayoutParams ().height;
    }

    /**
     * 设置view的可见高度
     *
     * @param height
     */
    public void setVisibleHeight (int height) {
        if(height<0){
            height=0;
        }
        ViewGroup.LayoutParams lp = container.getLayoutParams ();
        lp.height = height;
        container.setLayoutParams (lp);
    }

    public int getHeadViewContentHeight () {
        return contentLayout.getHeight ();
    }

    /**
     * 设置箭头状态和文字信息
     *
     * @param state
     */
    public void setState (int state) {
        if (state == mState) return;
        if (state == STATE_REFRESH) {    // 显示进度
            arrowIv.clearAnimation();
            arrowIv.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {    // 显示箭头图片
            arrowIv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                arrowIv.setImageResource(R.drawable.default_ptr_flip);
                if (mState == STATE_READY) {
                    arrowIv.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESH) {
                    arrowIv.clearAnimation();
                }
                headHintTv.setText(R.string.hfrecyclerview_header_hint_normal);

                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    arrowIv.clearAnimation();
                    arrowIv.startAnimation(mRotateUpAnim);
                    headHintTv.setText(R.string.hfrecyclerview_header_hint_ready);
                }
                break;
            case STATE_REFRESH:
                mProgressBar.startLoad();
                headHintTv.setText(R.string.hfrecyclerview_header_hint_loading);
                break;
            case STATE_SUCCESS:
                mProgressBar.stopLoad();
                headHintTv.setText(R.string.hfrecyclerview_header_hint_success);
                arrowIv.setImageResource(R.drawable.hfrecyclerview_success);
                SharedPreferences.Editor editor = preferences.edit();//获取编辑器
                editor.putLong(UPDATED_AT + mId, System.currentTimeMillis());
                editor.commit();//提交修改
                break;
            case STATE_REFRESH_FAIL:
                mProgressBar.stopLoad();
                headHintTv.setText(R.string.hfrecyclerview_header_hint_failt);
                arrowIv.setImageResource(R.drawable.hfrecyclerview_error);
                SharedPreferences.Editor editor1 = preferences.edit();//获取编辑器
                editor1.putLong(UPDATED_AT + mId, System.currentTimeMillis());
                editor1.commit();//提交修改
                break;
            default:
        }
        mState = state;
    }

    public void setHeadText (String text) {
        headHintTv.setText (text);
    }

    public void setTimeView (boolean isShow) {
        if(isShow){
            headerTimeTv.setVisibility (VISIBLE);
        }else {
            headerTimeTv.setVisibility (GONE);
        }
    }
}
