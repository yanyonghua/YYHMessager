package www.yyh.com.factory.presenter.user;

import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/8
 */
public interface UpdateInfoContract {
    interface Presenter extends BaseContract.Presenter{
        //更新
        void update(String photoFilePath,String desc,boolean isMan);
    }

    interface View extends BaseContract.View<Presenter>{
        //回调成功
        void updateSucceed();
    }
}
