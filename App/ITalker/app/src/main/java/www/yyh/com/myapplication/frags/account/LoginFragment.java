package www.yyh.com.myapplication.frags.account;


import android.content.Context;
import android.widget.EditText;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.factory.presenter.account.LoginContract;
import www.yyh.com.factory.presenter.account.LoginPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.MainActivity;

/**
 * 登录的界面
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter> implements LoginContract.View {

    private AccountTrigger mAccountTrigger;

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassword;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //拿到我们的Activity的引用
        mAccountTrigger= (AccountTrigger) context;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_login;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void loginSuccess() {
        MainActivity.show(getContext());
        getActivity().finish();
    }


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone =mPhone.getText().toString();
        String password =mPassword.getText().toString();
        mPresenter.login(phone,password);
    }


    @OnClick(R.id.txt_go_register)
    void onShowRegistClick(){
        // 让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        //当需要显示错误的时候触发，一定是结束了
        mLoading.stop();
        //让控件可以输入
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        //提交按钮可以继续点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //正在进行时，正在进行注册，界面不可操作
        mLoading.start();
        //让控件可以输入
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        //提交按钮可以继续点击
        mSubmit.setEnabled(false);
    }



}
