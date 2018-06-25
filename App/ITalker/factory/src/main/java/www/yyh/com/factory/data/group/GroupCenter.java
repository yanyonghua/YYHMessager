package www.yyh.com.factory.data.group;

import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.model.card.GroupMemberCard;

/**
 * 群中心的基本定义
 * Created by 56357 on 2018/6/21
 */
public interface GroupCenter {

    void dispatch(GroupCard... cards);
    void dispatch(GroupMemberCard... cards);
}
