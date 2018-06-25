package net.qiujuer.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息推送工具类
 */
public class PushDispatcher {
    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    private final static String appId = "mJar8UxBc5AXBGi2NnfWe4";
    private final static String appKey = "waNZMOuq108n1hSfjqoCpA";
    private final static String masterSecret = "YGVdRfcLQr51BJXrZCxnr5";
    static String host = "http://sdk.open.api.igexin.com/apiex.htm";
    private final IGtPush push;


    public PushDispatcher() {
        //最根本的发送者
         push = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 添加一条小心
     * @param receiver 接收者
     * @param model 接收的推送Model
     * @return 是否添加成功
     */
    public boolean  add(User receiver, PushModel model){
        //基础检查，必须有接收者的设备的id
        if (receiver==null||model==null||
                Strings.isNullOrEmpty(receiver.getPushid())){
            return false;
        }
        String pushString =model.getPushString();
        if (Strings.isNullOrEmpty(pushString))
            return false;
        BatchBean bean =buildMessage(receiver.getPushid(),pushString);
        beans.add(bean);
        return true;
    }

    /**
     * 对咬发送的数据进行格式化封装
     * @param clientId 接受者的设备Id
     * @param text 要接收的数据
     * @return BatchBean
     */
    private BatchBean buildMessage(String clientId,String text){
        //透传消息，不是通知栏显示，而是在客户端广播接受者收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(0); // 这个Type为int型，填写1则自动启动app
        //构建消息
        SingleMessage message = new SingleMessage();
        message.setData(template);//把透传消息设置到单消息模板中
        message.setOffline(true);//是否运行离线发送
        message.setOfflineExpireTime(24*3600 * 1000);//离线消息时长
        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);
        //返回一个封装
        return new BatchBean(message,target);
    }


    //进行消息最终的发送
    public boolean submit(){
        //構建打包的工具类
        IBatch batch = push.getBatch();
        //是否有数据需要发送
        boolean havaData =false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message,bean.target);
                havaData=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //没有数据就返回
        if (!havaData)return false;

        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            // 失败情况下尝试重复发送一次
            try {
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (result!=null){
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO,(String) result.getResponse().get("result"));
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING,("推送服务器响应异常！！"));
        return false;
    }
    //要收到消息的人和内容的列表
    private List<BatchBean> beans =new ArrayList<>();
    //给每个人发送消息的一个Bran封装
    private static class  BatchBean{
        SingleMessage message;
        Target target;

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }
}
