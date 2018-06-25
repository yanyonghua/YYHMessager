package www.yyh.com.factory.presenter.search;

import www.yyh.com.factory.presenter.BasePresenter;

/**
 * Created by 56357 on 2018/6/11
 */
public class SearchGroupPresenter extends
        BasePresenter<SearchContract.GroupView> implements SearchContract.Presenter{
    protected SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

    }
}
