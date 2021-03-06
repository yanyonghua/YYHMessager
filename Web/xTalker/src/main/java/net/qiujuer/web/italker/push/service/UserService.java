package net.qiujuer.web.italker.push.service;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.account.AccountRspModel;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.user.UpdateInfoModel;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.PushFactory;
import net.qiujuer.web.italker.push.factory.UserFactory;
import net.qiujuer.web.italker.push.utils.PushDispatcher;

import javax.jws.soap.SOAPBinding;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息处理的Service
 */
//http://127.0.0.1:8081/api/user/...
@Path("/user")
public class UserService extends BaseService {

    //用户信息修改接口
    //返回自己的gerenxinxi
    @PUT
    //    http://127.0.0.1:8081/api/user 不需要写就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(@HeaderParam("token") String token,
                                          UpdateInfoModel model) {

        if (Strings.isNullOrEmpty(token) || !UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        //更新用户信息
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        //架构自己的信息
        UserCard card = new UserCard(self, true);
        return ResponseModel.buildOk(card);
    }

    //拉去联系人
    @GET
    @Path("/contact")
    //    http://127.0.0.1:8081/api/user/contact
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact (){
        User self =getSelf();
        //拿到我的联系人
        List<User> users=UserFactory.contacts(self);
        //转换为UserCard
       List<UserCard> userCards= users.stream()
                .map(user -> {
                    return new UserCard(user,true);
                }).collect(Collectors.toList());//map操作，相当于转置操作User->UserCard
       //返回
        return ResponseModel.buildOk(userCards);
    }

    //关注人
    //简化 ：关注人的操作其实是双方同时关注，
    @PUT//修改类使用Put
    @Path("/follow/{followId}")
    //     http://127.0.0.1:8081/api/user/follow/
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId){
        User self = getSelf();
        //不能关注我自己
        if (self.getId().equalsIgnoreCase(followId)||Strings.isNullOrEmpty(followId)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        //找到我要关注的人
        User followUser =UserFactory.findById(followId);
        if (followUser==null){
            //未找到人
            return ResponseModel.buildNotFoundUserError(null);
        }
        //备注默认没有后面可以扩展
        followUser=UserFactory.follow(self,followUser,null);
        if (followUser==null){
            //关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }
        //  通知我关注的人我关注了他
        //给他发送我的信息
        PushFactory.pushFollow(followUser,new UserCard(self));

        //返回关注人的信息
        return ResponseModel.buildOk(new UserCard(followUser,true));
    }
    //获取默认的信息
    @GET//搜索人接口不涉及数据更改，只是查询，则为GET
    @Path("{id}")
    //  http://127.0.0.1/api/user
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id){
        if (Strings.isNullOrEmpty(id)){
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)){
            return ResponseModel.buildOk(new UserCard(self,true));
        }

        User user =UserFactory.findById(id);
        if (user==null){
            return ResponseModel.buildNotFoundUserError(null);
        }
        boolean isFollow =UserFactory.getUserFollow(self,user)!=null;
        return ResponseModel.buildOk(new UserCard(user,isFollow));

    }


    //搜索人的接口实现
    //为了简化分页：只返回20条数据
    @GET //搜索人接口不涉及数据更改，只是查询，则为GET
    @Path("/search/{name:(.*)?}")//名字为任意字符，可以为空
    //    http://127.0.0.1/api/user/search 不需要写就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact (@DefaultValue("")@PathParam("name")String name){
        User self =getSelf();
        //先查询数据
        List<User> searchUsers=UserFactory.search(name);
        //查询的人封装为UserCard
        //判断这些人是否有我已关注的人
        //如果有，则返回的关注状态应该已经设置后状态

        //拿出我的联系人

       final List<User> contacts =UserFactory.contacts(self);

       List<UserCard> userCards= searchUsers.stream()
                .map(user -> {
                    //判断这个人是否在我的联系人中
                    boolean isFollow=user.getId().equalsIgnoreCase(self.getId())
                            //进行联系人的任意匹配，匹配其中的Id字段
                            ||contacts.stream().anyMatch(
                                    contactUser->contactUser.getId().equalsIgnoreCase(user.getId())
                    );

                    return new UserCard(user,isFollow);
                }).collect(Collectors.toList());


        return ResponseModel.buildOk(userCards);


    }



}
