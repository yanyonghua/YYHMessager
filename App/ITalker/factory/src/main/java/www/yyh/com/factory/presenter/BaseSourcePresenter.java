package www.yyh.com.factory.presenter;

import java.util.List;

import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.DbDataSource;

/**
 * 基础的仓库源的Presenter定义
 * Created by 56357 on 2018/6/25
 */
public abstract class BaseSourcePresenter<Data,ViewModel,
        Source extends DbDataSource<Data>,
        View extends BaseContract.RecycleView> extends BaseRecyclerPresenter<ViewModel,View>
    implements DataSource.SucceedCallback<List<Data>>{
    protected Source mSource;

    public BaseSourcePresenter(Source source,View view) {
        super(view);
        mSource=source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource!=null)
        mSource.load(this);
    }

    @Override
    public void destory() {
        super.destory();
        mSource.dispose();
        mSource=null;
    }
}
