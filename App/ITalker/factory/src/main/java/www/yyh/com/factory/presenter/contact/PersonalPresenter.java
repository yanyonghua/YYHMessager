package www.yyh.com.factory.presenter.contact;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import www.yyh.com.factory.Factory;
import www.yyh.com.factory.data.helper.UserHelper;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.factory.presenter.BasePresenter;

/**
 * Created by 56357 on 2018/6/21
 */
public class PersonalPresenter extends BasePresenter<PersonalContract.View>
implements PersonalContract.Presenter{
    private User user;

    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();


        //个人界面用户数据优先从网络拉取
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view =getView();
                if (view!=null){
                    String id =view.getUserId();
                    User user = UserHelper.findFromNet(id);
                   onLoaded(view,user);
                }
            }
        });
    }
    private void onLoaded(final PersonalContract.View view , final User user){
        this.user=user;
        //是否就是我自己
        final boolean isSelf =user.getId().equals(Account.getUserId());
        //是否已经关注
        final boolean isFollow =isSelf||user.isFollow();

        final boolean allowSayHello =isFollow&&!isSelf;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onLoadDone(user);
                view.setFollowStates(isFollow);
                view.allowSayHello(allowSayHello);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
