package www.yyh.com.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import java.util.List;

import www.yyh.com.factory.data.helper.MessageHelper;
import www.yyh.com.factory.data.message.MessageDataSource;
import www.yyh.com.factory.model.api.message.MsgCreateModel;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.factory.presenter.BaseSourcePresenter;
import www.yyh.com.factory.utils.DiffUiDataCallback;

/**
 * 聊天Presenter 基础类
 * Created by 56357 on 2018/6/27
 */
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message,Message,MessageDataSource,View>
        implements ChatContract.Presenter{

    //接收者Id
    protected String mReceiverId;
    //区分是人还是群ID
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view,String receiverId,int receiverType) {
        super(source, view);
        mReceiverId =receiverId;
        mReceiverType=receiverType;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view=getView();
        if (view==null)
            return;
        //拿到老数据
        List <Message> old =view.getRecyclerAdapter().getItems();
        //差异计算
        DiffUiDataCallback<Message> callback=new DiffUiDataCallback<>(old,messages);
        final DiffUtil.DiffResult result =DiffUtil.calculateDiff(callback);
        //进行数据刷新
        refreshData(result,messages);
    }

    @Override
    public void pushText(String content) {
        MsgCreateModel model =new MsgCreateModel.Builder()
                .receiver(mReceiverId,mReceiverType)
                .content(content,Message.TYPE_STR)
                .build();
        //进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path,long time) {
        //  发送语音
        if (TextUtils.isEmpty(path))return;
        MsgCreateModel model =new MsgCreateModel.Builder()
                .receiver(mReceiverId,mReceiverType)
                .content(path,Message.TYPE_AUDIO)
                .attach(String.valueOf(time))
                .build();
        //进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushImage(String[] paths) {
       if (paths==null||paths.length==0)return;
       //此时路径是本地的手机上的路径
        for (String path : paths) {
            //构建一个新的消息
            MsgCreateModel model =new MsgCreateModel.Builder()
                    .receiver(mReceiverId,mReceiverType)
                    .content(path,Message.TYPE_PIC)
                    .build();
            //进行网络发送
            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        // 确定消息可以重复发送的
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                &&message.getStatus()==Message.STATUS_FAILED){
            //更改状态
            message.setStatus(Message.STATUS_CREATED);
            //构建发送Model
            MsgCreateModel model =MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }












}
