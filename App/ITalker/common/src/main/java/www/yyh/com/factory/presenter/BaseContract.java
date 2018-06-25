package www.yyh.com.factory.presenter;

import android.support.annotation.StringRes;

import www.yyh.com.common.widget.recycler.RecyclerAdapter;

/**
 * MVP模式中公共的基本契约
 * Created by 56357 on 2018/6/1
 */
public interface BaseContract {
    //基本的界面职责
    interface View<T extends Presenter>{

        //显示一个字符串错误
        void showError(@StringRes int str);
        // 公共的：显示进度条
        void showLoading();

        //支持设置一个Presenter
        void setPresenter(T presenter);
    }
    //基本的Presenter职责
    interface Presenter{
        //公用的开始触发
        void start();
        // 公共的结束触发
        void destory();
    }

    //基本的一个列表的View的职责
    interface RecycleView<T extends Presenter,ViewMode> extends View<T>{
        //        void onDone(List<User> users);
        // 拿到一个适配器，然后自己自主的进行刷新
        RecyclerAdapter<ViewMode> getRecyclerAdapter();

        //当适配器数据更累的时候触发
        void onAdapterDataChanged();
    }








}
