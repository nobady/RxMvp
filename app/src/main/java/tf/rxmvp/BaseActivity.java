package tf.rxmvp;

import android.app.ProgressDialog;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import tf.rxmvp.exception.CustomException;
import tf.rxmvp.mvp.BaseMvpActivity;
import tf.rxmvp.mvp.BasePresenter;
import tf.rxmvp.mvp.BaseView;
import tf.rxmvp.utils.ToastUtil;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/3 14:13.
 */

public abstract class BaseActivity<V extends BaseView,P extends BasePresenter<V>> extends BaseMvpActivity<V,P> {

    private ProgressDialog dialog;

    public static boolean IS_DEBUG = true;

    @Override
    public void showLoadDialog (boolean isCancelable) {
        showDialog ("",getResourceString (R.string.load),isCancelable);
    }

    @Override
    public void showDialog (String title, String msg, boolean isCancelable) {
        hideDialog ();
        dialog = ProgressDialog.show (this, "", msg, true, isCancelable);
    }

    @Override
    public void hideDialog () {
        if(dialog!=null&&dialog.isShowing ()){
            dialog.dismiss ();
        }
        dialog = null;
    }

    @Override
    public void showToastMsg (String msg) {
        ToastUtil.showShort (this,msg);
    }

    @Override
    public void onErrorHandle (Throwable e) {
        if(e instanceof UnknownHostException){
            showToastMsg (getResourceString (R.string.net_error));
        }else if(e instanceof SocketTimeoutException){
            showToastMsg (getResourceString (R.string.request_time_out));
        }else if(e instanceof CustomException){
            showToastMsg (e.getMessage ());
        }
    }

    @Override
    public String getResourceString (int resId) {
        return getResources ().getString (resId);
    }
}
