package www.yyh.com.factory.data.message;

import www.yyh.com.factory.model.card.MessageCard;

/**
 * 消息中心，进行消息卡片的消费
 * Created by 56357 on 2018/6/21
 */
public interface MessageCenter {
    void dispatch(MessageCard... cards);
}
