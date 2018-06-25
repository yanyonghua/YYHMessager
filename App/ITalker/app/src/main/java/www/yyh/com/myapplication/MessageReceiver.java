package www.yyh.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import www.yyh.com.factory.Factory;
import www.yyh.com.factory.data.helper.AccountHelper;
import www.yyh.com.factory.persistence.Account;

/**
 * 个推消息接收器
 * Created by 56357 on 2018/6/6
 */
public class MessageReceiver extends BroadcastReceiver{

    private static final String TAG=MessageReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent==null)return;

        Bundle bundle = intent.getExtras();

        //判断当前消息的意图
        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_CLIENTID:{
                Log.i(TAG, "onReceive: GET_CLIENTID"+bundle.toString());
                //当Id初始化的时候
                //获取设备id
                onClientInit(bundle.getString("clientid"));
                break;
            }
            case PushConsts.GET_MSG_DATA:{
                //常规消息送达
                byte[] payload =bundle.getByteArray("payload");
                if (payload!=null){
                    String message =new String(payload);
                    Log.i(TAG, "onReceive: GET_MSG_DATA"+message);
                    onMessageArrived(message);
                }
                break;
            }
            default:
                Log.i(TAG, "onReceive: other"+bundle.toString());
                break;
        }
    }

    /**
     * 当Id初始化的时候
     * @param cid
     */
    private void onClientInit(String cid){
        Account.setPushId(cid);
        if (Account.isLogin()){
            //账户登录状态，进行一次PushId绑定
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 消息到达时
     * @param message String
     */
    private void onMessageArrived(String message){
        //交给Factory处理
        Factory.dispatchPush(message);
    }


}
