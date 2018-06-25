package www.yyh.com.factory.data.helper;

import android.text.TextUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.yyh.com.factory.Factory;
import www.yyh.com.factory.R;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.model.api.RspModel;
import www.yyh.com.factory.model.api.account.AccountRspModel;
import www.yyh.com.factory.model.api.account.LoginModel;
import www.yyh.com.factory.model.api.account.RegisterModel;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.net.Network;
import www.yyh.com.factory.net.RemoteService;
import www.yyh.com.factory.persistence.Account;

/**
 * Created by 56357 on 2018/6/5
 */
public class AccountHelper {


    /**
     * 注册的接口
     * @param model 传递一个注册的Model进来
     * @param callback 成功与失败的回调
     */
    public static void register(final RegisterModel model, final DataSource.Callback<User> callback){
        //调用Retrofit 对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 登录的接口
     * @param model 传递一个注册的Model进来
     * @param callback 成功与失败的回调
     */
    public static void login(final LoginModel model, final DataSource.Callback<User> callback){
        //调用Retrofit 对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        call.enqueue(new AccountRspCallback(callback));
    }
    /**
     * 对设备Id进行绑定的操作
     * @param callback Callback
     */
    public static void  bindPush(final DataSource.Callback<User> callback){

        String pushId =Account.getPushId();
        if (TextUtils.isEmpty(pushId))
            return;
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> rspModelCall = service.accountBind(pushId);
        rspModelCall.enqueue(new AccountRspCallback(callback));
    }

    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>>{

        final DataSource.Callback<User> callback;
        AccountRspCallback(DataSource.Callback<User> callback){
            this.callback=callback;
        }
        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            // 请求成功返回
            //从返回中得到我们的全局Model，内部是使用的Gson
            RspModel<AccountRspModel> rspModel =response.body();
            if(rspModel.success()){
                //拿到实体
                AccountRspModel accountRspModel =rspModel.getResult();
                //获取我的信息
                final User user =accountRspModel.getUser();
                DbHelper.save(User.class,user);

               /* //第一种，直接保存
                user.save();*/

                    /*    //第二种使用ModelAdapter进行保存，进行集合的存储
                        FlowManager.getModelAdapter(User.class)
                                .save(user);
                        //第三种，事物中
                        DatabaseDefinition definition =FlowManager.getDatabase(AppDatabase.class);
                        definition.beginTransactionAsync(new ITransaction() {
                            @Override
                            public void execute(DatabaseWrapper databaseWrapper) {
                                FlowManager.getModelAdapter(User.class)
                                        .save(user);
                            }
                        }).build().execute();*/
                //同步到XML持久化中
                Account.login(accountRspModel);

                if (accountRspModel.isBind()){
                    //设置绑定状态
                    Account.setBind(true);
                    //然后返回
                    if (callback!=null)
                    callback.onDataLoaded(user);
                }else {
                    //对绑定进行唤起
                    bindPush(callback);
                }
            }else {
                // 对返回的RspModel中的失败的Code进行解析，解析到对应的String资源上面
                Factory.decodeRspCode(rspModel,callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            //网络请求失败
            if (callback!=null)
            callback.onDataNotAvailable(R.string.data_network_error);
        }
    }



}
