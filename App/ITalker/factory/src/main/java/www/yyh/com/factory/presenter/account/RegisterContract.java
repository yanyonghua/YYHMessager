package www.yyh.com.factory.presenter.account;

import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/1
 */
public interface RegisterContract {
    interface View extends BaseContract.View<RegisterContract.Presenter>{
        //注册成功
        void registerSuccess();
    }
    interface Presenter extends BaseContract.Presenter{
        //发起一个注册
        void register(String phone ,String name,String password);

        //检查手机号是否正确
        boolean checkMobike(String phone);
    }
}
