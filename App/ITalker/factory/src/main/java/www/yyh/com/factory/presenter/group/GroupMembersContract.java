package www.yyh.com.factory.presenter.group;

import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/7/9
 */
public interface GroupMembersContract {
    interface Presenter extends BaseContract.Presenter{
        //具有一个刷新的方法
        void refresh();
    }

    interface View extends BaseContract.RecycleView<Presenter ,MemberUserModel>{
        String getmGroupId();
    }
}
