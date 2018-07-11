package www.yyh.com.factory.presenter.message;

import java.util.List;

import www.yyh.com.factory.data.helper.GroupHelper;
import www.yyh.com.factory.data.message.MessageGroupRepository;
import www.yyh.com.factory.model.db.Group;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.persistence.Account;

/**
 * //群聊天的逻辑
 * Created by 56357 on 2018/6/27
 */
public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter{
    public ChatGroupPresenter( ChatContract.GroupView view, String receiverId ) {
        //数据源，View 接收者，接收者类型
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);

    }

    @Override
    public void start() {
        super.start();
        //从本地拿群的信息
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group!=null){
            //初始化操作
            ChatContract.GroupView view =getView();

            boolean isAdmin =   group.getOwner().getId().equalsIgnoreCase(Account.getUserId());

            view.showAdminOption(isAdmin);
            //基础信息初始化
            view.onInit(group);
            //成员初始化
            List<MemberUserModel> models =group.getLatetyGroupMembers();
            final long memberCount =group.getGroupMemberCount();
            //没有显示的成员的数量
            long moreCount =memberCount-models.size();

             view.onInitGroupMembers(models,moreCount);

        }
    }
}
