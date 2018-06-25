package www.yyh.com.factory.presenter.search;

import java.util.List;

import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.model.card.UserCard;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/11
 */
public interface SearchContract {
    interface Presenter extends BaseContract.Presenter {
        //搜索内容
        void search(String content);
    }
    //搜索人的界面
    interface UserView extends BaseContract.View<Presenter>{
        void onSearchDone(List<UserCard> userCards);
    }
    //搜索群的界面
    interface GroupView extends BaseContract.View<Presenter>{
        void onSearchDone(List<GroupCard> groupCards);
    }
}
