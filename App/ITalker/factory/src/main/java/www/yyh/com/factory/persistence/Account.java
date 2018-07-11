package www.yyh.com.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import www.yyh.com.factory.Factory;
import www.yyh.com.factory.model.api.account.AccountRspModel;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.model.db.User_Table;

/**
 * Created by 56357 on 2018/6/6
 */
public class Account {
    private static final String KEY_PUSH_ID="KEY_PUSH_ID";
    private static final String KEY_IS_BIND="KEY_IS_BIND";
    private static final String KEY_TOKEN="KEY_TOKEN";
    private static final String KEY_USERID="KEY_USERID";
    private static final String KEY_ACCOUNT="KEY_ACCOUNT";

    //设备推送Id
    private static String pushId;
    //设备ID是否已经绑定到服务器
    private static boolean isBind;
    //登录状态的Token，用来获取用户信息
    private static String token;
    //登录的用户ID
    private static String userId;
    //登录的账户
    private static String account;
    /**
     * 存储数据到XML文件，持久化
     */
    private static void save(Context context){
        //获取数据持久化的sp
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_PUSH_ID,pushId)
                .putString(KEY_TOKEN,token)
                .putString(KEY_USERID,userId)
                .putString(KEY_ACCOUNT,account)
                .putBoolean(KEY_IS_BIND,isBind)
                .apply();
    }

    /**
     * 开启app时操作
     * @param context
     */
    public static void load(Context context){
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId=sp.getString(KEY_PUSH_ID,"");
        token=sp.getString(KEY_TOKEN,"");
        userId=sp.getString(KEY_USERID,"");
        account=sp.getString(KEY_ACCOUNT,"");
        isBind=sp.getBoolean(KEY_IS_BIND,false);
    }
    /**
     * 设置并存储设备的Id
     * @param pushId 设备的推送ID
     */
    public static void setPushId(String pushId){
        Account.pushId=pushId;
        Account.save(Factory.app());
    }



    /**
     * 获取推送Id
     * @return 推送ID
     */
    public static String getPushId(){
        return pushId;
    }

    /**
     * 返回当前账户是否登录
     * @return True已登录
     */
    public static boolean isLogin(){

        return !TextUtils.isEmpty(userId)
                &&!TextUtils.isEmpty(token);
    }

    /**
     * 是否已经完善了用户信息
     * @return True 是完成了
     */
    public static boolean isComplete(){
        //首先保证登陆成功
        if (isLogin()){
            User self =getUser();
            return !TextUtils.isEmpty(self.getDescription())
                    &&!TextUtils.isEmpty(self.getPortrait())
                    &&self.getSex()!=0;
        }
        // TODO
        return false;
    }

    /**
     * 是否已经绑定到了服务器
     * @return
     */
    public static boolean isBind(){
        return isBind;
    }

    /**
     * 设置绑定状态
     * @param isBind
     */
    public static void setBind(boolean isBind){
        Account.isBind=isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到持久化XML中
     * @param model AccountRspModel
     */
    public static void login(AccountRspModel model){
           // 存储当前登录的账户、token，用户Id，方便从数据库中查询
        Account.token=model.getToken();
        Account.account=model.getAccount();
        Account.userId=model.getUser().getId();
        save(Factory.app());
    }

    public static User getUser() {
        return TextUtils.isEmpty(userId)?new User(): SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    /**
     * 返回用户id
     * @return id
     */
    public static String getUserId(){
        return getUser().getId();
    }

    public static String getToken(){
        return Account.token;
    }
}
