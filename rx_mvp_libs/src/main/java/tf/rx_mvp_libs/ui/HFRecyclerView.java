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

    private final static int SCROLL_HEADER = 1;
    private final static int SCROLL_FOOTER = 2;

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

    private int mScrollBack;

    private HFRecyclerViewListener recyclerViewListener;

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

    public void setRecyclerViewListener (HFRecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (adapter!=null){
            adapter.unregisterAdapterDataObserver(dataObserver);
        }
        mScroller=null;
        hfAdapter=null;
        itemClickListener=null;
        recyclerViewFooter=null;
        recyclerViewHeader=null;
        layoutManager=null;
        recyclerViewListener=null;
        adapter=null;
        dataObserver=null;
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
        setHasFixedSize (true);
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
     * 是否正在上拉加载
     */
    private boolean mIsPullLoad;


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
                float moveY = e.getRawY () - mLastY;
                mLastY = e.getRawY ();
                //如果可以下拉刷新，并且还没有在刷新，并且第一个可见的位置 是1或者0，且手势向下移动或者头部可见的view高度大于0
                if (canRefresh && !mIsRefresh && !mIsPullLoad && layoutManager.findFirstVisibleItemPosition () <= 1
                        && (moveY > 0 || recyclerViewHeader.getVisibleHeight () > 0)) {
                    updateHeaderHeight (moveY / OFFSET_RADIO);
                } else if (!checkIsLoadMore()&&canLoadMore && !mIsPullLoad && !mIsRefresh &&
                        layoutManager.findLastVisibleItemPosition () == hfAdapter.getItemCount () - 1
                        && adapter.getItemCount () > 0 && (moveY < 0 || recyclerViewFooter.getBottomMargin () > 0)) {
                    updateFooterHeight (-moveY / OFFSET_RADIO);
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastY = -1;
                //判断是否需要下拉刷新
                //如果没有在刷新，并且可以下拉刷新，且第一个可见位置是头部view，并且头部view的高度大于本身的高度，说明触发了下拉刷新
                if (!mIsRefresh && canRefresh && layoutManager.findFirstVisibleItemPosition () == 0
                        && recyclerViewHeader.getVisibleHeight () > mHeadViewHeight) {
                    mIsRefresh = true;
                    recyclerViewHeader.setState (HFRecyclerViewHeader.STATE_REFRESH);
                    if (recyclerViewListener != null) {
                        recyclerViewListener.onRefresh ();
                    }
                }
                //判断是否需要上拉加载
                if (canLoadMore && !mIsPullLoad && layoutManager.findLastVisibleItemPosition () ==
                        hfAdapter.getItemCount () - 1 && recyclerViewFooter.getBottomMargin () > PULL_LOAD_MORE_DELTA) {
                    mIsPullLoad = true;
                    recyclerViewFooter.setState (HFRecyclerViewFooter.STATE_LOADING);
                    if (recyclerViewListener != null) {
                        recyclerViewListener.onLoadMore ();
                    }

                }
                resetHeadViewHeight ();
                resetFooterViewHeight ();
                break;
        }

        return super.onTouchEvent (e);
    }

    /**
     * 判断当前的数据列表中，是否超过了一屏，如果超过了一屏，就可以加载更多
     * @return
     */
    private boolean checkIsLoadMore(){
        int totalCount = layoutManager.getItemCount ();
        int childCount = layoutManager.getChildCount ();
        return totalCount==childCount;
    }
    /**
     * 重置head高度 1.如果head高度为0,不处理 2.如果head高度在指定高度范围内，不处理 3.如果head高度大于本身高度了，就让head的高度变为自身的高度
     */
    private void resetHeadViewHeight () {
        int height = recyclerViewHeader.getVisibleHeight ();
        if (height == 0) {
            return;
        }
        if (mIsRefresh && height <= mHeadViewHeight) {
            return;
        }
        int finalHeight = 0;
        if (mIsRefresh && height > mHeadViewHeight) {
            finalHeight = mHeadViewHeight;
        }

        mScrollBack = SCROLL_HEADER;
        mScroller.startScroll (0, height, 0, finalHeight - height, SCROLL_DURATION);

        invalidate ();
    }

    private void resetFooterViewHeight () {
        int bottomMargin = recyclerViewFooter.getBottomMargin ();
        if (bottomMargin > 0) {
            mScrollBack = SCROLL_FOOTER;
            mScroller.startScroll (0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate ();
        }
    }

    @Override
    public void computeScroll () {
        if (mScroller.computeScrollOffset ()) {  //如果为true，说明view滚动完成
            if (mScrollBack == SCROLL_HEADER) {
                recyclerViewHeader.setVisibleHeight (mScroller.getCurrY ());
            } else {
                recyclerViewFooter.setBottomMargin (mScroller.getCurrY ());
            }
            postInvalidate ();
        }
        super.computeScroll ();
    }

    /**
     * 停止刷新
     *
     * @param isSuccess
     *         true,刷新成功，更新头部view为刷新成功：false,更新头部view为刷新失败
     */
    public void stopRefresh (boolean isSuccess) {
        if (mIsRefresh) {
            if (isSuccess) {
                recyclerViewHeader.setState (HFRecyclerViewHeader.STATE_SUCCESS);
            } else {
                recyclerViewHeader.setState (HFRecyclerViewHeader.STATE_REFRESH_FAIL);
            }
            recyclerViewHeader.postDelayed (new Runnable () {
                @Override
                public void run () {
                    mIsRefresh = false;
                    resetHeadViewHeight ();
                }
            }, 1000);
        }
    }

    public void stopLoadMore (boolean isSuccess) {
        if (mIsPullLoad) {
            if (isSuccess) {
                recyclerViewFooter.setState (HFRecyclerViewFooter.STATE_SUCCESS);
            } else {
                recyclerViewFooter.setState (HFRecyclerViewFooter.STATE_LOAD_FAIL);
            }
            recyclerViewFooter.postDelayed (new Runnable () {
                @Override
                public void run () {
                    mIsPullLoad = false;
                    resetFooterViewHeight ();
                }
            }, 1000);
        }
    }

    /**
     * 更新footerview的信息
     *
     * @param moveY
     */
    private void updateFooterHeight (float moveY) {
        int height = (int) moveY + recyclerViewFooter.getBottomMargin ();
        recyclerViewFooter.setBottomMargin (height);
        //如果处于为刷新状态
        if (canLoadMore && !mIsPullLoad) {
            if (height > 50) {    //滑动距离大于指定距离，更新文字
                recyclerViewFooter.setState (HFRecyclerViewFooter.STATE_READY);
            } else {
                recyclerViewFooter.setState (HFRecyclerViewFooter.STATE_NORMAL);
            }
        }
    }

    /**
     * 更新头部view的信息
     *
     * @param moveY
     */
    private void updateHeaderHeight (float moveY) {
        recyclerViewHeader.setVisibleHeight ((int) moveY + recyclerViewHeader.getVisibleHeight ());
        if (canRefresh && !mIsRefresh) {  //如果处于未刷新状态，更新箭头
            //如果下拉的高度大于头部view本身的高度，那么就更新文字和箭头
            if (recyclerViewHeader.getVisibleHeight () > mHeadViewHeight) {
                recyclerViewHeader.setState (1);
            } else {
                recyclerViewHeader.setState (0);   //正常状态
            }
        }
    }

    @Override
    public void setLayoutManager (LayoutManager layout) {
        if(!(layout instanceof LinearLayoutManager)){
            throw new RuntimeException ("必须使用LinearLayoutManager");
        }
        super.setLayoutManager (layout);
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

    /**
     * 刷新和加载的事件监听器
     */
    public interface HFRecyclerViewListener {
        void onRefresh ();

        void onLoadMore ();
    }

    /**
     * 设置头部文字
     * @param text
     */
    public void setHeadViewText(String text){
        recyclerViewHeader.setHeadText(text);
    }

    /**
     * 设置底部文字
     * @param text
     */
    public void setFooterViewText(String text){
        recyclerViewFooter.setFooterText(text);
    }

}

