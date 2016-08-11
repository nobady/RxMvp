package tf.rx_mvp_libs.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/10 17:05.
 */

public class HFRecyclerViewAdapter extends RecyclerView.Adapter {

    public static final int ITEM_RECYCLER_HEADER = 0xdf;
    public static final int ITEM_RECYCLER_FOOTER = 0xdd;
    public static final int ITEM_RECYCLER_CONTENT = 0xde;
    public static final int ITEM_HEAD_VIEW = 0xdc;

    /**
     * 列表头部（下拉时会出现）
     */
    private HFRecyclerViewHeader recyclerViewHeader;
    /**
     * 列表foot（上啦加载时出现）
     */
    private HFRecyclerViewFooter recyclerViewFooter;
    /**
     * 一直在recyclerview上方的view
     */
    private View headView;
    /**
     * item事件监听器
     */
    private HFRecyclerViewItemClickListener itemClickListener;
    /**
     * 是否可以下拉刷新
     */
    private boolean isRefresh;
    /*
     * 是否可以上啦加载
     */
    private boolean isLoadMore;
    /**
     * 列表头部个数
     */
    private int mHeaderCount;
    /**
     * 列表foot个数
     */
    private int mFooterCount;
    /**
     * 具体业务的adapter
     */
    private RecyclerView.Adapter mAdapter;


    public HFRecyclerViewAdapter (RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    /**
     * 设置自定义view
     * @param headView
     */
    public void setHeadView (View headView) {
        this.headView = headView;
    }

    /**
     * 设置是否可以下拉刷新
     * @param refresh  true 可以下拉
     */
    public void setRefresh (boolean refresh) {
        isRefresh = refresh;
        if (refresh) {
            mHeaderCount = 1;
        } else {
            mHeaderCount = 0;
        }

    }

    /**
     * 设置是否支持上拉加载
     * @param loadMore  true  支持
     */
    public void setLoadMore (boolean loadMore) {
        isLoadMore = loadMore;
        if (loadMore) {
            mFooterCount = 1;
        } else {
            mFooterCount = 0;
        }
    }

    /**
     * 设置头部view】
     * @param recyclerViewHeader
     */
    public void setRecyclerViewHeader (HFRecyclerViewHeader recyclerViewHeader) {
        this.recyclerViewHeader = recyclerViewHeader;
    }

    /**
     * 设置底部view
     * @param recyclerViewFooter
     */
    public void setRecyclerViewFooter (HFRecyclerViewFooter recyclerViewFooter) {
        this.recyclerViewFooter = recyclerViewFooter;
    }

    /**
     * 设置点击时间监听器
     * @param itemClickListener
     */
    public void setItemClickListener (HFRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        if (viewType == ITEM_RECYCLER_HEADER) {
            return new HFHolder (recyclerViewHeader);
        } else if (viewType == ITEM_RECYCLER_FOOTER) {
            return new HFHolder (recyclerViewFooter);
        } else if (viewType == ITEM_HEAD_VIEW) {
            return new HFHolder (headView);
        }
        return mAdapter.onCreateViewHolder (parent, viewType);
    }

    @Override
    public void onBindViewHolder (final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HFHolder) {
            return;
        }
        //要将头部view的数量除去 才算实际 的position
        int po = position - getHeadViewCount ();
        mAdapter.onBindViewHolder (holder, po);

        //设置长按短按监听
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    itemClickListener.onItemClick (v, holder.getLayoutPosition ());
                }
            });

            holder.itemView.setOnLongClickListener (new View.OnLongClickListener () {
                @Override
                public boolean onLongClick (View v) {
                    itemClickListener.onLongItemClick (v, holder.getLayoutPosition ());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount () {
        int itemCount = mAdapter.getItemCount ();
        itemCount+=getHeadViewCount ();
        if(isLoadMore){
            itemCount+=mFooterCount;
        }
        return itemCount;
    }

    @Override
    public int getItemViewType (int position) {
        if(isRefresh&&isHeaderView (position)){
            return ITEM_RECYCLER_HEADER;
        }else if(isLoadMore&&isFootrtView (position)){
            return ITEM_RECYCLER_FOOTER;
        }else if(isCustomHeadView (position)){
            return ITEM_HEAD_VIEW;
        }
        return ITEM_RECYCLER_CONTENT;
    }

    //判断当前item是否是头部view
    private boolean isHeaderView (int position) {
        return mHeaderCount != 0 && position == 0;
    }
    //判断当前item是否是底部view
    private boolean isFootrtView (int position) {
        return mFooterCount != 0 && position >= (mAdapter.getItemCount () + getHeadViewCount ());
    }
    //判断当前item是否是自定义view
    private boolean isCustomHeadView (int position) {
        return headView != null && position == 1;
    }

    /**
     * 获取头部的view总数
     *
     * @return
     */
    public int getHeadViewCount () {
        int count = 0;
        if (isRefresh) {
            count += 1;
        }
        if (headView != null) {
            count += 1;
        }
        return count;
    }

    class HFHolder extends RecyclerView.ViewHolder {

        public HFHolder (View itemView) {
            super (itemView);
        }
    }
}
