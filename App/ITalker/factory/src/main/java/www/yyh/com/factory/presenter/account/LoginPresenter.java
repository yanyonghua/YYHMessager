package www.yyh.com.factory.presenter.account;

import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import www.yyh.com.factory.R;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.AccountHelper;
import www.yyh.com.factory.model.api.account.LoginModel;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.factory.presenter.BasePresenter;

/**
 * 登录的逻辑实现
 * Created by 56357 on 2018/6/6
 */
public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter ,DataSource.Callback{


    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String phone, String password) {
        start();
        final LoginContract.View view =getView();
        if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(password)){
            view.showError(R.string.data_account_login_invalid_parameter);
        }else {
            LoginModel model =new LoginModel(phone,password, Account.getPushId());
            AccountHelper.login(model,this);
        }
    }


    @Override
    public void onDataLoaded(Object o) {
        //当网络请求成功，注册好了，回送一个用户信息回来
        //告知界面，注册成功
        final LoginContract.View view= getView();
        if (view==null)
            return;
        //强制主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final LoginContract.View view= getView();
        if (view==null)
            return;
        //此时是从网络回送回来的，并不保证处于主线程状态
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用主界面注册失败显示错误
                view.showError(strRes);
            }
        });
    }
}
