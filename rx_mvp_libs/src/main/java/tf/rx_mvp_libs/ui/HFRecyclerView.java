package tf.rx_mvp_libs.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/10 15:48.
 */

public class HFRecyclerView extends RecyclerView {

    private final static float OFFSET_RADIO = 1.8f;
    private final static int PULL_LOAD_MORE_DELTA = 50;
    private final static int SCROLL_DURATION = 400;

    private Scroller mScroller;
    private HFRecyclerViewHeader recyclerViewHeader;
    private HFRecyclerViewFooter recyclerViewFooter;

    private HFRecyclerViewAdapter hfAdapter;
    //数据观察者
    private HFAdapterDataObserver dataObserver;

    private boolean canLoadMore;

    private boolean canRefresh;

    private Adapter adapter;

    private HFRecyclerViewItemClickListener itemClickListener;
    private LinearLayoutManager layoutManager;
    private int mHeadViewHeight;

    public HFRecyclerView (Context context) {
        super (context);
    }

    public HFRecyclerView (Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        init (context);
    }

    public HFRecyclerView (Context context, @Nullable AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
    }

    public void setCanLoadMore (boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public void setCanRefresh (boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public void setItemClickListener (HFRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init (Context context) {
        mScroller = new Scroller (context, new DecelerateInterpolator ());
        recyclerViewHeader = new HFRecyclerViewHeader (context);
        recyclerViewFooter = new HFRecyclerViewFooter (context);
        recyclerViewHeader.getViewTreeObserver ().addOnGlobalLayoutListener (new ViewTreeObserver.
                OnGlobalLayoutListener () {
            @Override
            public void onGlobalLayout () {
                mHeadViewHeight = recyclerViewHeader.getHeadViewContentHeight ();
                getViewTreeObserver ().removeGlobalOnLayoutListener (this);
            }
        });
        layoutManager = new LinearLayoutManager (context);
        setLayoutManager (layoutManager);
        dataObserver = new HFAdapterDataObserver ();
        setOverScrollMode (OVER_SCROLL_NEVER);
    }

    @Override
    public void setAdapter (Adapter adapter) {
        this.adapter = adapter;
        hfAdapter = new HFRecyclerViewAdapter (adapter);
        adapter.registerAdapterDataObserver (dataObserver);
        hfAdapter.setRecyclerViewHeader (recyclerViewHeader);
        hfAdapter.setRecyclerViewFooter (recyclerViewFooter);
        hfAdapter.setLoadMore (canLoadMore);
        hfAdapter.setRefresh (canRefresh);
        hfAdapter.setItemClickListener (itemClickListener);
        super.setAdapter (hfAdapter);
    }

    /**
     * 记录上一次Y坐标的值
     */
    private float mLastY;
    /**
     * 是否正在下拉刷新
     */
    private boolean mIsRefresh;
    /**
     * 是否正在加载或者刷新数据
     */
    private boolean mPullLoad;

    @Override
    public boolean onTouchEvent (MotionEvent e) {
        //如果是手指抬起之后或者是第一次手指触摸
        if (mLastY == -1 || mLastY == 0) {
            mLastY = e.getRawY ();
            //如果不是在下拉刷新并且第一个可见位置是head时，更新文字
            if (!mIsRefresh && layoutManager.findFirstVisibleItemPosition () <= 1) {
                recyclerViewHeader.refreshHeadViewTextValue ();
            }
        }
        switch (e.getAction ()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY ();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = mLastY - e.getRawY ();
                mLastY = e.getRawY ();
                //如果可以下拉刷新，并且还没有在刷新，并且第一个可见的位置 是1或者0，且手势向下移动或者头部可见的view高度大于0
                if (canRefresh && !mPullLoad && layoutManager.findFirstVisibleItemPosition () <= 1 &&
                        (moveY > 0 || recyclerViewHeader.getVisibleHeight () > 0)) {
                    updateHeaderHeight (moveY / OFFSET_RADIO);
                } else if (canLoadMore && !mPullLoad && !mIsRefresh &&
                        layoutManager.findLastVisibleItemPosition () == hfAdapter.getItemCount () - 1
                        && adapter.getItemCount () > 0 && (moveY < 0 || recyclerViewFooter.getBottomMargin () > 0)) {
                    updateFooterHeight (-moveY/OFFSET_RADIO);
                }
                break;
        }

        return super.onTouchEvent (e);
    }

    /**
     * 更新footerview的信息
     * @param moveY
     */
    private void updateFooterHeight (float moveY) {
        int height = (int) moveY + recyclerViewFooter.getBottomMargin ();
        recyclerViewFooter.setBottomMargin(height);
        if(canLoadMore){

        }
    }

    /**
     * 更新头部view的信息
     *
     * @param moveY
     */
    private void updateHeaderHeight (float moveY) {
        recyclerViewHeader.setVisibleHeight ((int) moveY + recyclerViewHeader.getVisibleHeight ());
        if (canRefresh && !mIsRefresh) {  //如果处于为刷新状态，更新箭头
            //如果下拉的高度大于头部view本身的高度，那么就更新文字和箭头
            if (recyclerViewHeader.getVisibleHeight () > mHeadViewHeight) {
                recyclerViewHeader.setState (1);
            } else {
                recyclerViewHeader.setState (0);   //正常状态
            }
        }
    }

    /**
     * 数据观察者，
     */
    class HFAdapterDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged () {
            hfAdapter.notifyDataSetChanged ();
        }

        @Override
        public void onItemRangeChanged (int positionStart, int itemCount) {
            hfAdapter.notifyItemRangeChanged (positionStart + hfAdapter.getHeadViewCount (), itemCount);
        }

        @Override
        public void onItemRangeChanged (int positionStart, int itemCount, Object payload) {
            hfAdapter.notifyItemRangeChanged (positionStart + hfAdapter.getHeadViewCount (), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted (int positionStart, int itemCount) {
            hfAdapter.notifyItemRangeInserted (positionStart + hfAdapter.getHeadViewCount (), itemCount);
        }

        @Override
        public void onItemRangeRemoved (int positionStart, int itemCount) {
            hfAdapter.notifyItemRangeRemoved (positionStart + hfAdapter.getHeadViewCount (), itemCount);
        }

        @Override
        public void onItemRangeMoved (int fromPosition, int toPosition, int itemCount) {
            hfAdapter.notifyItemMoved (fromPosition + hfAdapter.getHeadViewCount (), toPosition + hfAdapter.getHeadViewCount ());
        }
    }
}
