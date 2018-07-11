package www.yyh.com.factory.presenter.message;

import www.yyh.com.factory.data.helper.UserHelper;
import www.yyh.com.factory.data.message.MessageRepository;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.model.db.User;

/**
 * Created by 56357 on 2018/6/27
 */
public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
implements ChatContract.Presenter{
    public ChatUserPresenter( ChatContract.UserView view, String receiverId ) {
        //数据源，View 接收者，接收者类型
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);

    }

    @Override
    public void start() {
        super.start();
        //从本地拿这个人的信息
        User  receiver= UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
