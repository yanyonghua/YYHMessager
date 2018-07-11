package www.yyh.com.factory.presenter;

/**
 * Created by 56357 on 2018/6/1
 */
public class BasePresenter<T extends BaseContract.View>
        implements BaseContract.Presenter {

    /**
     * 给子类使用的获取View的操作
     * @return
     */
    public final T getView() {
        return mView;
    }

    /**
     * 给子类使用获取View的操作 setview顺便把Presenter 的实体set到View里面去了
     * @param mView
     */
    public void setView(T mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    private T mView;


   public BasePresenter(T view){
       setView(view);
   }

    @Override
    public void start() {
       T view =mView;
       if (view!=null){
           view.showLoading();
       }
    }

    @Override
    public void destory() {
        T view =mView;
        mView=null;
        if (view!=null){
            view.setPresenter(null);
        }
    }
}
