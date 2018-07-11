package www.yyh.com.factory.presenter.group;

import www.yyh.com.factory.model.Author;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/7/1
 */
public interface GroupCreateContract {
    interface Presenter extends BaseContract.Presenter{
        //创建
        void create(String name,String desc,String picture);
        // 更改一个Model的选中状态
        void changeSelect(ViewModel model,boolean isSelect);
    }

    interface View extends BaseContract.RecycleView<Presenter,ViewModel>{
        //创建成功
        void onCreateSuccessed();
    }

    //recycle里面的ViewModel
    class ViewModel{
        //用户信息
      public   Author author;
        // 是否选中
       public  boolean isSelect;
    }
}
