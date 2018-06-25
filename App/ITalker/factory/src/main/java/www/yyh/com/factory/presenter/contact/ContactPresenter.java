package www.yyh.com.factory.presenter.contact;

import android.support.v7.util.DiffUtil;

import java.util.List;

import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.UserHelper;
import www.yyh.com.factory.data.user.ContactDataSource;
import www.yyh.com.factory.data.user.ContactRepository;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.presenter.BaseSourcePresenter;
import www.yyh.com.utils.DiffUiDataCallback;

/**
 * 联系人的Presenter的实现
 * Created by 56357 on 2018/6/13
 */
public class ContactPresenter extends BaseSourcePresenter<User,User,ContactDataSource,ContactContract.View>
        implements ContactContract.Presenter,DataSource.SucceedCallback<List<User>>{


    public ContactPresenter(ContactContract.View view) {
        //初始化数据仓库
        super(new ContactRepository(),view);
    }

    @Override
    public void start() {
        super.start();

        //加载网络数据
        UserHelper.refreshContacts();

        //  问题：
        // 1.关注后虽然存储了数据库，但是没有刷新联系人
        // 2.如果刷新数据，或者从网络刷新，最终刷新的时候是全局刷新
        // 3.本地刷新和网络刷新，在添加到界面的时候会有可能冲突；导致数据显示异常
        // 4.如何识别已经在数据中有这样的数据了


    }

    private void diff(List<User> oldList,List<User> newList){
       DiffUtil.Callback callback =new DiffUiDataCallback<>(oldList,newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        // 在对比完成后进行数据的赋值
        getView().getRecyclerAdapter().replace(newList);
        //尝试刷新界面
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
        getView().onAdapterDataChanged();
    }

    //运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<User> users) {
        //无论怎么操作，数据变更，最终都会通知到这里来
        final ContactContract.View view =getView();
        if (view==null)return;
        RecyclerAdapter<User> adapter =view.getRecyclerAdapter();
        List<User> old =adapter.getItems();
        //进行数据对比
        DiffUtil.Callback callback =new DiffUiDataCallback<>(old,users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //调用基类方法进行数据刷新
        refreshData(result,users);
    }
}
