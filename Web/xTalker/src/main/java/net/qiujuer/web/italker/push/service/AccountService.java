package net.qiujuer.web.italker.push.service;

import net.qiujuer.web.italker.push.bean.api.account.RegisterModel;
import net.qiujuer.web.italker.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.rmi.registry.Registry;

/**
 * @author qiujur
* */
//127.0.0.1/api/account/...
@Path("/account")
public class AccountService {


    @POST
    @Path("/register")
    //指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RegisterModel register(RegisterModel registerModel) {
        return registerModel;
     /*   User user = new User();
        user.setName(registerModel.getName());
        user.setSex(2);
        return user;*/
    }
}
