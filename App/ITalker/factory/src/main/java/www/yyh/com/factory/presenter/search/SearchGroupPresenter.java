package www.yyh.com.factory.presenter.search;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.GroupHelper;
import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.presenter.BasePresenter;

/**
 * Created by 56357 on 2018/6/11
 */
public class SearchGroupPresenter extends
        BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter
        ,DataSource.Callback<List<GroupCard>> {

    private Call searchCall;

    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }



    @Override
    public void search(String content) {
        start();

        Call call =searchCall;
        if (call!=null&&!call.isCanceled()){
            //如果有上一次的请求，并且没有取消
            // 则调用取消请求操作
            call.cancel();
        }
        searchCall = GroupHelper.search(content, this);

    }

    @Override
    public void onDataLoaded(final List<GroupCard> groupCards) {
        final SearchContract.GroupView view =getView();
        if (view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(groupCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final SearchContract.GroupView view =getView();
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
