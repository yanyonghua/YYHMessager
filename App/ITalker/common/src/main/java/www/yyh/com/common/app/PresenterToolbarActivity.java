package www.yyh.com.common.app;


import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/11
 */
public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter>{
    protected Presenter mPresenter;



    /**
     * 初始化Presenter
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    protected void initBefore() {
        super.initBefore();
        //初始化Presenter
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity关闭的时候，进行销毁
        if (mPresenter!=null){
            mPresenter.destory();
        }
    }

    @Override
    public void showError(int str) {
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerError(str);
        }else {
            //显示错误
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }
    }

    public void hideLoading() {
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerOk();
        }
    }



    @Override
    public void setPresenter(Presenter presenter) {
        //View中赋值Presenter
        this.mPresenter = presenter;
    }
}
