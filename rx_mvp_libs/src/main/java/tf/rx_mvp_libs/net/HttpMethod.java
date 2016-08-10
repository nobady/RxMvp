package tf.rx_mvp_libs.net;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tf.rx_mvp_libs.exception.CustomException;
import tf.rx_mvp_libs.mvp.BaseMvpActivity;
import tf.rx_mvp_libs.utils.SysUtil;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/3 14:35.
 */

public class HttpMethod {

    private Retrofit mRetrofit;
    private static int TIME_OUT = 5*1000;
    private Context context;

    private HttpMethod(){}

    private static class SINGEINSTANCE{
        static HttpMethod httpMethod = new HttpMethod ();
    }

    public static HttpMethod getInstance(){
        return SINGEINSTANCE.httpMethod;
    }

    public void init(Context context){
        this.context = context;
    }

    public <T> Observable<T> call(Observable<HttpResult<T>> observable){

        return Observable.just (observable)
                //检测网络
                .doOnNext (new Action1<Observable<HttpResult<T>>> () {
                    @Override
                    public void call (Observable<HttpResult<T>> observable) {
                        if(!SysUtil.isConnected (context)){
                            CustomException customException = new CustomException ();
                            customException.setCode (CustomException.NETWORK_UNAVAILABLE);
                            customException.setMessage ("网络链接不可用,请稍候重试...");
                            throw customException;
                        }
                    }
                })
                //切换到io线程访问网络
                .observeOn (Schedulers.io ())
                .flatMap (new Func1<Observable<HttpResult<T>>, Observable<HttpResult<T>>> () {
                    @Override
                    public Observable<HttpResult<T>> call (Observable<HttpResult<T>> observable) {
                        return observable;
                    }
                })
                .observeOn (AndroidSchedulers.mainThread ())
                .subscribeOn (Schedulers.io ())
                .flatMap (new Func1<HttpResult<T>, Observable<T>> () {
                    @Override
                    public Observable<T> call (HttpResult<T> tHttpResult) {

                        if (tHttpResult.getCode() == 1) {   //接口调用失败
                            CustomException lCustomException = new CustomException();
                            lCustomException.setCode(1);
                            lCustomException.setMessage(tHttpResult.getMessage());
                            return Observable.error(lCustomException);
                        }
                        return Observable.just(tHttpResult.getData());
                    }
                });
    }

    private Retrofit initRetrofit(String url,int timeout){
        if(mRetrofit==null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder ();

            //Log信息拦截器
            if(BaseMvpActivity.IS_DEBUG){
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor ();
                //设置拦截的内容
                interceptor.setLevel (HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor (interceptor);
            }
            //超时时间
            builder.connectTimeout (timeout, TimeUnit.MILLISECONDS);
            //错误重连
            builder.retryOnConnectionFailure (true);

            OkHttpClient httpClient = builder.build ();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl (url)
                    //Json转换器
                    .addConverterFactory (GsonConverterFactory.create ())
                    //RXjava 适配器
                    .addCallAdapterFactory (RxJavaCallAdapterFactory.create ())
                    .client (httpClient)
                    .build ();
        }
        return mRetrofit;
    }

    public Retrofit initRetrofit(String url){
        return initRetrofit (url,TIME_OUT);
    }
}
