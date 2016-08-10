package tf.rx_mvp_libs.mvp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;


/**
 * @description <b>【功能】</b>
 * <p>
 * <p>
 * <b>【使用方法】</b>
 * <p>
 * <p>
 * <b>【注】</b>
 * <p>
 * <p>
 * @since 1.0.0
 * ${tags}
 */
public class BaseFragment extends Fragment {

    private static final String PAGE_TAG = "BaseMainFragment";
    private static final String PAGE_UNKNOW = "未知页面";
    private static final String LOG_JUMP_PAGE = "从：%1$s\n跳到：%2$s";
    private static final String LOG_CREATE_FRAGMENT = "在【%1$s】\n创建：【%2$s】";
    private static final String LOG_RRCEIVE_BROADCAST = "%1$s页收到广播：【Action = %2$s】";

    public static final String KEY_EX_PAGETAG = "KEY_EX_PAGETAG";
    public static final String KEY_CURRENT_PAGETAG = "KEY_CURRENT_PAGETAG";
    public static final String KEY_IFLOG = "KEY_IFLOG";
    public static final String KEY_ASPAGE = "KEY_ASPAGE";

    protected boolean mIfLog;
    protected String mCurPageTag = PAGE_UNKNOW;
    protected String mExPageTag = PAGE_UNKNOW;
    protected boolean mAsPage;
    protected FragmentManager mFragmentManager;

    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCurPageTag = arguments.getString(KEY_CURRENT_PAGETAG);
            mExPageTag = arguments.getString(KEY_EX_PAGETAG);
            mIfLog = arguments.getBoolean(KEY_IFLOG, false);
            mAsPage = arguments.getBoolean(KEY_ASPAGE, true);
        } else {
            mCurPageTag = PAGE_TAG;
            mExPageTag = PAGE_UNKNOW;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
//        Constant.ConstantState.CurrentPage = mCurPageTag;
//        if (mAsPage) {
//            L.e(mCurPageTag, LOG_JUMP_PAGE, mExPageTag, mCurPageTag);
//        } else {
//            L.e(mCurPageTag, LOG_CREATE_FRAGMENT, mExPageTag, mCurPageTag);
//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
//               L.d(mIfLog, mCurPageTag, LOG_RRCEIVE_BROADCAST, mCurPageTag, action);
                setReceiverAction(action, intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        setFilterAction(filter);
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    /**
     * 在{@link #onViewCreated(View, Bundle)}的生命周期执行创建，
     * 在{@link #onDestroyView()}的生命周期反注销
     *
     * @param action
     * @param intent
     */
    protected void setReceiverAction(String action, Intent intent) {
    }

    protected void setFilterAction(IntentFilter filter) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocalBroadcastManager.unregisterReceiver(broadcastReceiver);  //注销广播
    }
}
