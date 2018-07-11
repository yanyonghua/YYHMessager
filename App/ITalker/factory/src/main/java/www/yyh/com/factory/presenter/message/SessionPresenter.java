package www.yyh.com.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import java.util.List;

import www.yyh.com.factory.data.message.SessionDataSource;
import www.yyh.com.factory.data.message.SessionRepository;
import www.yyh.com.factory.model.db.Session;
import www.yyh.com.factory.presenter.BaseSourcePresenter;
import www.yyh.com.factory.utils.DiffUiDataCallback;

/**
 * //最近聊天列表的Presenter
 * Created by 56357 on 2018/6/28
 */
public class SessionPresenter extends BaseSourcePresenter<Session,
        Session,SessionDataSource,SessionContract.View>implements SessionContract.Presenter
{

    public SessionPresenter( SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
            SessionContract.View view =getView();
            if (view==null)
                return;
            //差异对比
        List<Session> old =view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback=new DiffUiDataCallback<>(old,sessions);
        DiffUtil.DiffResult result=  DiffUtil.calculateDiff(callback);
        //刷新界面
        refreshData(result,sessions);
    }
}
