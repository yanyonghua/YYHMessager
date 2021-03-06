package www.yyh.com.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

import www.yyh.com.factory.data.BaseDbRepository;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.model.db.Message_Table;

/**
 * 跟某人聊天的时候的聊天列表
 * 关注的内容一定是我发给这个人的，或者是他发送给我的
 * Created by 56357 on 2018/6/27
 */
public class MessageRepository extends BaseDbRepository<Message>
implements MessageDataSource {
    //聊天对象Id
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);
        //(sender_id == receiverId and group_id==null) or ( receiverId = receiver_id )
        //拉去本地聊天记录
        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause().
                        and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull())
                )
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {

        // receiverId 如果是发送者，那么Group==null 情况下一定是发送给我的消息
        //receiverId 如果消息接受者不为空，那么一定是发送给某个人的，这个人只能是我或者是某个人
        // 如果这个“某个人”就是receiverId，那么就是我需要关注的信息
        return receiverId.equalsIgnoreCase(message.getSender().getId())
                &&message.getGroup()==null
                ||(message.getReceiver()!=null
                 &&message.getReceiver().getId().equalsIgnoreCase(receiverId));


    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);

    }
}
