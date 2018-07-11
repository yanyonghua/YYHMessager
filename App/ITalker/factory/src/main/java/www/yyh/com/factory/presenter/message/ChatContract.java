package www.yyh.com.factory.presenter.message;

import java.util.List;

import www.yyh.com.factory.model.db.Group;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/27
 */
public interface ChatContract {
    interface Presenter extends BaseContract.Presenter{
        //发送文字
        void pushText(String content);
        //发送语音
        void pushAudio(String path,long time);
        // 发送图片
        void pushImage(String[] paths);
        //重新发送一个消息，返回是否调度成功
        boolean rePush(Message message);
    }
    //界面的基类 RecycleView里面的ViewModel为Message
    interface View<InitModel> extends BaseContract.RecycleView<Presenter,Message>{
        void onInit(InitModel model);
    }
    // 人聊天的界面
    interface UserView extends View<User>{

    }
    //群聊天的界面
    interface GroupView extends View<Group>{
        //显示管理员菜单
        void showAdminOption(boolean isAdmin);

        //初始化成员信息
        void onInitGroupMembers(List<MemberUserModel> members,long  moreCount);
    }

}
