package www.yyh.com.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口定义
 * Created by 56357 on 2018/6/5
 */
public interface DataSource {

    /**
     * 同时包括了成功与失败的回调接口
     * @param <T> 任意类型
     */
    interface Callback<T> extends SucceedCallback<T>,FailedCallback<T>{

    }

    /**
     * 只关注成功的接口
     * @param <T>
     */
    interface SucceedCallback<T>{
        //数据加载成功，网络请求成功
        void onDataLoaded(T t);
    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback<T> {
        //数据加载失败，网络请求失败
        void onDataNotAvailable(@StringRes int strRes);
    }


    /**
     * 销毁操作
     */
    void dispose();
}
