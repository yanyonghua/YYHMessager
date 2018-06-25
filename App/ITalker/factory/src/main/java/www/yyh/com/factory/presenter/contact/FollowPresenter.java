package www.yyh.com.factory.presenter.contact;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.UserHelper;
import www.yyh.com.factory.model.card.UserCard;
import www.yyh.com.factory.presenter.BasePresenter;

/**
 * Created by 56357 on 2018/6/12
 */
public class FollowPresenter extends BasePresenter<FollowContract.View>
        implements FollowContract.Presenter ,DataSource.Callback<UserCard>{

    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();
        UserHelper.follow(id,this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view=getView();

        if (view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final FollowContract.View view=getView();

        if (view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}
