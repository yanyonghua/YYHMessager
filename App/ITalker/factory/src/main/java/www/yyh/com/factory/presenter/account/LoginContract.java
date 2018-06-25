package www.yyh.com.factory.presenter.account;

import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/1
 */
public interface LoginContract {
    interface View extends BaseContract.View<LoginContract.Presenter>{
        //登录成功
        void loginSuccess();
    }
    interface Presenter extends BaseContract.Presenter{
        //发起一个登录
        void login(String phone, String password);
    }
}
