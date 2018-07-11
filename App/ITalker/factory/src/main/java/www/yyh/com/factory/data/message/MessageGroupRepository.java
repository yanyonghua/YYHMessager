package www.yyh.com.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

import www.yyh.com.factory.data.BaseDbRepository;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.model.db.Message_Table;

/**
 * 跟群聊天的时候的聊天列表
 * 关注的内容一定是我发给这个群的，或者是别人发送给群的消息
 * Created by 56357 on 2018/6/27
 */
public class MessageGroupRepository extends BaseDbRepository<Message>
implements MessageDataSource {
    //群Id
    private String receiverId;

    public MessageGroupRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        //(sender_id == receiverId and group_id==null) or ( receiverId = receiver_id )
        //无论是自己发还是别人发，只要是发到这个群的
        //那么这个group_id就是receiverId
        SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        //如果消息的group不为空，则一定是发送到一个群的
        //如果群Id等于我们需要的，那就通过
        return  message.getGroup()!=null&&receiverId.equalsIgnoreCase(message.getGroup().getId());

    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);

    }
}
