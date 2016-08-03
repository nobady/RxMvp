package tf.rxmvp.mvp;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/1 15:17.
 */

public interface BaseView extends MvpView {

    //dialog

    void showLoadDialog (boolean isCancelable);

    void showDialog (String title, String msg, boolean isCancelable);

    void hideDialog ();


    //toast

    void showToastMsg (String msg);

    //error handle
    void onErrorHandle (Throwable e);


    String getResourceString (int resId);

}
