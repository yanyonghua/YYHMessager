package net.qiujuer.web.italker.push.service;

import com.google.common.base.Strings;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import net.qiujuer.web.italker.push.bean.api.account.AccountRspModel;
import net.qiujuer.web.italker.push.bean.api.account.LoginModel;
import net.qiujuer.web.italker.push.bean.api.account.RegisterModel;
import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.rmi.registry.Registry;

/**
 * @author qiujur
 */
//127.0.0.1/api/account/...
@Path("/account")
public class AccountService extends BaseService {
    //登录
    @POST
    @Path("/login")
    //指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel loginModel) {
        if (!LoginModel.check(loginModel)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.login(loginModel.getAccount(), loginModel.getPassword());
        if (user != null) {
            //如果有携带PushId
            if (!Strings.isNullOrEmpty(loginModel.getPushId())) {
                return bind(user, loginModel.getPushId());
            }
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        } else {
            return ResponseModel.buildLoginError();
        }
    }

    //注册
    @POST
    @Path("/register")
    //指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel registerModel) {
        if (!RegisterModel.check(registerModel)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.findByPhone(registerModel.getAccount().trim());

        if (user != null) {
            //已有账户
            return ResponseModel.buildHaveAccountError();
        }
        user = UserFactory.findByName(registerModel.getName().trim());

        if (user != null) {
            //已有名字
            return ResponseModel.buildHaveNameError();
        }

        //开始注册逻辑
        user = UserFactory.register(
                registerModel.getAccount(),
                registerModel.getPassword(), registerModel.getName());
        if (user != null) {

            //如果有鞋带PushId
            if (!Strings.isNullOrEmpty(registerModel.getPushId())) {
                return bind(user, registerModel.getPushId());
            }

            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        } else {
            return ResponseModel.buildRegisterError();
        }

    }


    //绑定设备id
    @POST
    @Path("/bind/{pushId}")
    //指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //从请求头中获取Token字段
    //pushId从Url地址中获取
    public ResponseModel<AccountRspModel> bind(
                                               @PathParam("pushId") String pushId) {
        if (Strings.isNullOrEmpty(pushId)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        //拿到自己的个人信息
        User self = getSelf();
        return bind(self, pushId);
    }


    /**
     * 绑定的操作
     *
     * @param self   自己
     * @param pushId pushId
     * @return User
     */
    private ResponseModel<AccountRspModel> bind(User self, String pushId) {
        //进行设备Id绑定操作
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {
            //绑定失败则是服务器异常
            return ResponseModel.buildServiceError();
        }
        //返回当前的账户
        AccountRspModel rspModel = new AccountRspModel(user, true);
        return ResponseModel.buildOk(rspModel);

    }


}
