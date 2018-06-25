package www.yyh.com.factory.presenter.contact;

import www.yyh.com.factory.model.card.UserCard;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * 关注的接口定义
 * Created by 56357 on 2018/6/12
 */
public interface FollowContract {
    //任务调度者
    interface Presenter extends BaseContract.Presenter{
        //关注一个人
        void follow(String id);

    }
    interface View extends BaseContract.View<Presenter>{
        void onFollowSucceed(UserCard userCard);
    }

}
