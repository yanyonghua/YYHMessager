package www.yyh.com.factory.presenter.contact;

import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * 联系人
 * Created by 56357 on 2018/6/21
 */
public interface PersonalContract {
    interface Presenter extends BaseContract.Presenter{
        //获取用户信息
          User getUserPersonal();
    }
    interface View extends BaseContract.View<Presenter>{
        //获取用户ID
        String getUserId();
        //加载数据完成
        void onLoadDone(User user);
        //是否发起聊天
        void allowSayHello(boolean isAllow);
        //设置关注状态
        void setFollowStates(boolean isFollow);

    }
}
