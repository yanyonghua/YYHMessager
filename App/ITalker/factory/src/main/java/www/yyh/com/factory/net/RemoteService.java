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
import www.yyh.com.factory.model.api.user.UserUpdateModel;
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


    //
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);






































}
