package www.yyh.com.factory.presenter.contact;

import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/13
 */
public interface ContactContract {
    //什么都不要额外定义，开始就是调用start即可
    interface Presenter extends BaseContract.Presenter{

    }

    //都在基类里面完成了
    interface View extends BaseContract.RecycleView<Presenter,User>{

    }



















}
