package www.yyh.com.factory.data.message;

import www.yyh.com.factory.data.DbDataSource;
import www.yyh.com.factory.model.db.Message;

/**
 *
 * 消息的数据源定义，他的实现是：MessageRepository，MessageGroupRepository
 * 关注的是Message表
 * Created by 56357 on 2018/6/27
 */
public interface MessageDataSource extends DbDataSource<Message> {
}
