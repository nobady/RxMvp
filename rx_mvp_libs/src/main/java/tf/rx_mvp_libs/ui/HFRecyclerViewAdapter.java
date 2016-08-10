package tf.rx_mvp_libs.ui;

import android.support.v7.widget.RecyclerView;
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


    public void setRefresh (boolean refresh) {
        isRefresh = refresh;
        if(refresh){
            mHeaderCount = 1;
        }else {
            mHeaderCount = 0;
        }

    }

    public void setLoadMore (boolean loadMore) {
        isLoadMore = loadMore;
        if(loadMore){
            mFooterCount = 1;
        }else {
            mFooterCount = 0;
        }
    }

    public void setRecyclerViewHeader (HFRecyclerViewHeader recyclerViewHeader) {
        this.recyclerViewHeader = recyclerViewHeader;
    }

    public void setRecyclerViewFooter (HFRecyclerViewFooter recyclerViewFooter) {
        this.recyclerViewFooter = recyclerViewFooter;
    }

    public void setItemClickListener (HFRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount () {
        return 0;
    }
}
