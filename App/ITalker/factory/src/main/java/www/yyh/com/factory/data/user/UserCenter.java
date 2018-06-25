package www.yyh.com.factory.data.user;

import www.yyh.com.factory.model.card.UserCard;

/**
 * 用户中心的基本定义
 * Created by 56357 on 2018/6/21
 */
public interface UserCenter {
    //分发处理一堆用户卡片的信息，并更新到数据库
    void dispatch(UserCard... cards);
}
