package tf.rx_mvp_libs.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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

    private Scroller mScroller;
    private HFRecyclerViewHeader recyclerViewHeader;
    private HFRecyclerViewFooter recyclerViewFooter;

    private HFRecyclerViewAdapter hfAdapter;
    //数据观察者
    private HFAdapterDataObserver dataObserver;

    private boolean canLoadMore;

    private boolean canRefresh;

    private HFRecyclerViewItemClickListener itemClickListener;

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
     * @param context
     */
    private void init (Context context) {
        mScroller = new Scroller (context,new DecelerateInterpolator ());
        recyclerViewHeader = new HFRecyclerViewHeader (context);
        recyclerViewFooter = new HFRecyclerViewFooter (context);
        recyclerViewHeader.getViewTreeObserver ().addOnGlobalLayoutListener (new ViewTreeObserver.
                OnGlobalLayoutListener () {
            @Override
            public void onGlobalLayout () {
            }
        });
        setLayoutManager (new LinearLayoutManager (context));
        dataObserver = new HFAdapterDataObserver ();
        setOverScrollMode (OVER_SCROLL_NEVER);
    }
    @Override
    public void setAdapter (Adapter adapter) {
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
     * 数据观察者，
     */
    class HFAdapterDataObserver extends AdapterDataObserver{
        @Override
        public void onChanged () {
            hfAdapter.notifyDataSetChanged ();
        }

        @Override
        public void onItemRangeChanged (int positionStart, int itemCount) {
            hfAdapter.notifyItemRangeChanged (positionStart+hfAdapter.getHeadViewCount (),itemCount);
        }

        @Override
        public void onItemRangeChanged (int positionStart, int itemCount, Object payload) {
            hfAdapter.notifyItemRangeChanged (positionStart+hfAdapter.getHeadViewCount (),itemCount,payload);
        }

        @Override
        public void onItemRangeInserted (int positionStart, int itemCount) {
            hfAdapter.notifyItemRangeInserted (positionStart+hfAdapter.getHeadViewCount (),itemCount);
        }

        @Override
        public void onItemRangeRemoved (int positionStart, int itemCount) {
            hfAdapter.notifyItemRangeRemoved (positionStart+hfAdapter.getHeadViewCount (),itemCount);
        }

        @Override
        public void onItemRangeMoved (int fromPosition, int toPosition, int itemCount) {
            hfAdapter.notifyItemMoved (fromPosition+hfAdapter.getHeadViewCount (),toPosition+hfAdapter.getHeadViewCount ());
        }
    }
}
