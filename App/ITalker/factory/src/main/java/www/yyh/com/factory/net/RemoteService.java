package www.yyh.com.factory.net;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import www.yyh.com.factory.model.api.RspModel;
import www.yyh.com.factory.model.api.account.AccountRspModel;
import www.yyh.com.factory.model.api.account.LoginModel;
import www.yyh.com.factory.model.api.account.RegisterModel;
import www.yyh.com.factory.model.api.group.GroupCreateModel;
import www.yyh.com.factory.model.api.group.GroupMemberAddModel;
import www.yyh.com.factory.model.api.message.MsgCreateModel;
import www.yyh.com.factory.model.api.user.UserUpdateModel;
import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.model.card.GroupMemberCard;
import www.yyh.com.factory.model.card.MessageCard;
import www.yyh.com.factory.model.card.UserCard;

/**
 * 网络请求的所有的接口
 * Created by 56357 on 2018/6/5
 */
public interface RemoteService {

    /**
     *网络请求一个注册接口
     * @param model 传递的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     * @param model
     * @return RspModel<LoginModel>
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备Id
     * @param pushId 设备Id
     * @return RspModel<AccountRspModel>
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true,value = "pushId") String pushId);

    /**
     *
     * @param model 设备Id
     * @return RspModel<AccountRspModel>
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 用户搜索的接口
     * @param name
     * @return
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    //用户关注接口
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String userId);

    //获取联系人列表
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();


    // 查询某人的信息
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);

    //发送消息的接口
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);


    //创建群
    @POST("group")
    Call<RspModel<GroupCard>> createGroup(@Body GroupCreateModel model);
    //搜索群
    @GET("group/{groupId}")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId") String groupId);

    //搜索群
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value = "name",encoded = true) String name);

    //搜索群
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date",encoded = true) String date);


    //我的群成员列表
    @GET("group/{groupId}/members")
    Call<RspModel<List<GroupMemberCard>>> groupMember(@Path("groupId") String groupId);


    //get群添加成员
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path("groupId") String groupId,
                                                         @Body GroupMemberAddModel model);






































}
