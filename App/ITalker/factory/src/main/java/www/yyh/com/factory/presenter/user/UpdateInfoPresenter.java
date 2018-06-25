package www.yyh.com.factory.presenter.user;

import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import www.yyh.com.factory.Factory;
import www.yyh.com.factory.R;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.UserHelper;
import www.yyh.com.factory.model.api.user.UserUpdateModel;
import www.yyh.com.factory.model.card.UserCard;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.net.UploadHelper;
import www.yyh.com.factory.presenter.BasePresenter;

/**
 * Created by 56357 on 2018/6/8
 */
public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
        implements UpdateInfoContract.Presenter ,DataSource.Callback<UserCard>{
    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        start();
        final UpdateInfoContract.View view=getView();
        if (TextUtils.isEmpty(photoFilePath)||TextUtils.isEmpty(desc)){
            view.showError(R.string.data_account_update_invalid_parameter);
        }else {
            //上传头像
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if (url == null) {
                        //上传失败
                        view.showError(R.string.data_upload_error);
                    }else {
                        //构建Model
                        UserUpdateModel model = new UserUpdateModel("",
                                url,desc,isMan? User.SEX_MAN:User.SEX_WOMAN);
                        //进行网络请求上传
                        UserHelper.update(model,UpdateInfoPresenter.this);
                    }
                }
            });
        }
    }


    @Override
    public void onDataLoaded(UserCard userCard) {
        final UpdateInfoContract.View view= getView();
        if (view==null)
            return;
        //强制主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final UpdateInfoContract.View view= getView();
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
