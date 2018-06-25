package www.yyh.com.factory.presenter.account;

import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

import www.yyh.com.common.Common;
import www.yyh.com.factory.R;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.AccountHelper;
import www.yyh.com.factory.model.api.account.RegisterModel;
import www.yyh.com.factory.presenter.BasePresenter;



/**
 * Created by 56357 on 2018/6/1
 */
public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter,DataSource.Callback {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        // 调用开始方法，在start中默认启动了Loading
        start();

        //得到View 接口
        RegisterContract.View view= getView();

        //校验
        if (!checkMobike(phone)){
            //提示
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        }else if(name.length()<2){
            //姓名需要大于两位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        }else if (password.length()<6){
            //密码需要大于6位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        }else {
            //进行网络请求
            //构造Model，进行请求调用
            RegisterModel model =new RegisterModel(phone,password,name);
            //进行网络请求，并设置回送接口为自己
            AccountHelper.register(model,this);
        }
    }

    /**
     * 检查手机号是否合法
     * @param phone 手机号码
     * @return 合法为True
     */
    @Override
    public boolean checkMobike(String phone) {

        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE,phone);
    }

    @Override
    public void onDataLoaded(Object o) {
        //当网络请求成功，注册好了，回送一个用户信息回来
        //告知界面，注册成功
        final RegisterContract.View view= getView();
        if (view==null)
            return;
            //此时是从网络回送回来的，并不保证处于主线程状态

        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.registerSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final RegisterContract.View view= getView();
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
