package net.qiujuer.web.italker.push.service;

import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.message.MessageCreateModel;
import net.qiujuer.web.italker.push.bean.api.user.MessageCard;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.Group;
import net.qiujuer.web.italker.push.bean.db.Message;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.GroupFactory;
import net.qiujuer.web.italker.push.factory.MessageFactory;
import net.qiujuer.web.italker.push.factory.PushFactory;
import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息发送的入口
 */
@Path("/msg")
public class MessageService extends BaseService{
    //发送一条消息到服务器
    @POST
    //    http://127.0.0.1:8081/api/msg
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage (MessageCreateModel model){
        if (!MessageCreateModel.check(model)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }

        User self =getSelf();
        Message message =MessageFactory.findById(model.getId());
        if (message!=null){
            //正常返回
            return ResponseModel.buildOk(new MessageCard(message));
        }
        if (model.getReceiverType()==Message.RECEIVER_TYPE_GROUP){
            return pushToGroup(self,model);
        }else {
            return pushToMessage(self,model);
        }
    }

    //发送到人
    private ResponseModel<MessageCard> pushToMessage(User sender, MessageCreateModel model) {
        User receiver =UserFactory.findById(model.getReceiverId());
        if (receiver==null){
            // 没有找到接收者
            return ResponseModel.buildNotFoundUserError("Con't find receiver user");
        }
        if (receiver.getId().equals(sender.getId())){
            //发送者接受者是同一个人就返回创建消息失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //存储数据库
        Message message =MessageFactory.add(sender,receiver,model);
        return buildAndPushResponse(sender,message);
    }


    //发送到群
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
      //找群是有权限性质的找
       Group group = GroupFactory.findById(sender,model.getReceiverId());
        if (group==null){
            //没有找到接收者群，有可能是你不是群成员
            return ResponseModel.buildNotFoundUserError("Con't find receiver group");
        }
        //添加到数据库
        Message message = MessageFactory.add(sender,group,model);
        //走通用的推送逻辑
        return buildAndPushResponse(sender,message);
    }

    //推送并构建一个返回消息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {

        if (message==null){
            //存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //进行推送
        PushFactory.pushNewMessage(sender,message);

        return ResponseModel.buildOk(new MessageCard(message));
    }
}
