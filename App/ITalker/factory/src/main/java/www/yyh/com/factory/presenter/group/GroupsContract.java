package www.yyh.com.factory.presenter.group;

import www.yyh.com.factory.model.db.Group;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * 我的群列表契约
 *
 * Created by 56357 on 2018/6/13
 */
public interface GroupsContract {
    //什么都不要额外定义，开始就是调用start即可
    interface Presenter extends BaseContract.Presenter{

    }

    //都在基类里面完成了
    interface View extends BaseContract.RecycleView<Presenter,Group>{

    }



















}
