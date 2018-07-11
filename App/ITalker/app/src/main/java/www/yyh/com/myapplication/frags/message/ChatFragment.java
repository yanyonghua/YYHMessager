package www.yyh.com.myapplication.frags.message;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.Application;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.tools.AudioPlayHelper;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.adapter.TextWatcherAdapter;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.face.Face;
import www.yyh.com.factory.model.db.Message;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.factory.presenter.message.ChatContract;
import www.yyh.com.factory.utils.FileCache;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.panel.PanelFragment;

import static www.yyh.com.myapplication.activities.MessageActivity.KEY_RECEIVER_ID;

/**
 * Created by 56357 on 2018/6/27
 */
public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel>,PanelFragment.PanelCallback {

    protected String mReceiverId;
    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.appBar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    @Override
    public void onSendGallery(String[] paths) {
        // 图片回调回来
        mPresenter.pushImage(paths);
    }

    @Override
    public void onRecordDone(File file, long time) {
        //  语音回调回来
        mPresenter.pushAudio(file.getAbsolutePath(),time);

    }

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    ImageView mSubmit;
    //控制底部面板
    private AirPanel.Boss mPanelboss;

    private PanelFragment mPanelFragment;

    private FileCache<AudioHolder> mAudioFileCache;
    private AudioPlayHelper<AudioHolder> mAudioHelper;

    @Override
    public void onStart() {
        super.onStart();
        mAudioHelper=new AudioPlayHelper<>(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {
            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                //泛型的作用就在于此，绑定viewholder，用于最后绑定的
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                //直接停止
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                Application.showToast(R.string.toast_audio_play_error);
            }
        });

        //下载工具类
        mAudioFileCache=new FileCache<AudioHolder>("audio/cache", "mp3", new FileCache.CacheListener<AudioHolder>() {
            @Override
            public void onDownloadSuccessed(final AudioHolder audioHolder,final  File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mAudioHelper.trigger(audioHolder,file.getAbsolutePath());
                    }
                });
            }

            @Override
            public void onDownloadFailed(AudioHolder audioHolder) {
                Application.showToast(R.string.toast_download_error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioHelper.destroy();
    }

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId= bundle.getString(KEY_RECEIVER_ID);
    }

    //得到顶部布局的id资源
    @LayoutRes
    protected  abstract int getHeaderLayoutid();
    @Override
    protected void initWidget(View view) {
        //拿到占位布局
        //替换顶部布局一定需要发生在super之前
        ViewStub stub =view.findViewById(R.id.view_stub_header);
        stub.setLayoutResource(getHeaderLayoutid());
        stub.inflate();
        //在这里进行控件绑定
        super.initWidget(view);
        // 初始化面板操作
        mPanelboss = (AirPanel.Boss)view.findViewById(R.id.lay_content);
        mPanelboss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                //请求隐藏软键盘
                Util.hideKeyboard(mContent);
            }
        });
        mPanelFragment= (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);
        initToolbar();
        initAppBar();
        initEditContent();
        //基本设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter=new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        //添加适配器的监听器，进行点击实现
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Message>(){
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {
                if (message.getType()==Message.TYPE_AUDIO&&holder instanceof ChatFragment.AudioHolder){
                    //权限的判断，当然权限已经全局申请
                    mAudioFileCache.download((AudioHolder) holder,message.getContent());
                }

            }
        });
    }

    @Override
    protected final int getContentLayout() {
        return R.layout.fragment_chat_common;
    }
    @Override
    protected void initData() {
        super.initData();
        //进行初始化操作
        mPresenter.start();
    }

    // 初始化Toolbar
    protected void initToolbar(){
        Toolbar toolbar=mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    //初始化输入框监听
    private void initEditContent(){
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content =s.toString().trim();
                boolean needSendMsg =!TextUtils.isEmpty(content);
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    private void initAppBar(){
        mAppBarLayout.addOnOffsetChangedListener(this);
    }
    @OnClick(R.id.btn_face)
    void onFaceClick(){
        //仅仅只需要打开即可
        mPanelboss.openPanel();
        mPanelFragment.showFace();

    }
    @OnClick(R.id.btn_record)
    void onRecordClick(){
        mPanelboss.openPanel();
        mPanelFragment.showRecord();
    }
    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        if (mSubmit.isActivated()){
            //发送
            String content =mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        }else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        mPanelboss.openPanel();
        mPanelFragment.showGalley();
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 界面没有占位布局，Recycler是一直显示的
    }
    // 适配器
    private class Adapter extends RecyclerAdapter<Message>{

        @Override
        protected ViewHolder<Message> onCreataViewHolder(View root, int viewType) {
            switch (viewType){
                //左右都是同一个
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);

                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);

                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);

                    // 默认情况下，返回的就是Text类型的Holder进行处理
                    //文件的一些实现
                    default:
                        return new TextHolder(root);
            }

        }

        @Override
        protected int getItemViewType(int position, Message message) {

            //我发送的在右边，收到的在左边
                boolean isRight =Objects.equals(message.getSender().getId(), Account.getUserId());
                switch (message.getType()){
                    case Message.TYPE_STR:
                        //文字内容
                        return isRight?R.layout.cell_chat_text_right:R.layout.cell_chat_text_left;
                    case Message.TYPE_AUDIO:
                        //语音内容
                        return isRight?R.layout.cell_chat_audio_right:R.layout.cell_chat_audio_left;
                    case Message.TYPE_PIC:
                        //图片内容
                        return isRight?R.layout.cell_chat_pic_right:R.layout.cell_chat_pic_left;
                    default:
                        //文字内容
                        return isRight?R.layout.cell_chat_text_right:R.layout.cell_chat_text_left;

                }
        }
    }

    //holder 的基类
    class  BaseHolder extends RecyclerAdapter.ViewHolder<Message>{
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        //允许为空
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender =message.getSender();
            //进行数据加载
            sender.load();
            //头像加载
            mPortrait.setup(Glide.with(ChatFragment.this),sender);
            if (mLoading!=null){
                //当前的布局应该在右边
                int status = message.getStatus();
                if (status==Message.STATUS_DONE){
                    //正常状态
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                }else if (status==Message.STATUS_CREATED){
                    //正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(),R.color.colorAccent));
                    mLoading.start();
                }else if (status==Message.STATUS_FAILED){
                    //发送失败,允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(),R.color.alertImportant));
                }
                mPortrait.setEnabled(status==Message.STATUS_FAILED);
            }
        }
        @OnClick(R.id.im_portrait)
        void onRePushClick(){
            //重新发送
            if (mLoading!=null&&mPresenter.rePush(mData)){
                //必须是右边的才有可能重新发送
                //状态改变需要重新刷新界面当前信息
                updateData(mData);
            }
        }
    }

    //文字的Holder
    class TextHolder extends BaseHolder{

        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);

        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            Spannable spannable =new SpannableString(message.getContent());
            //解析表情
            Face.decode(mContent,spannable, (int) Ui.dipToPx(getResources(),20));
            //把内容设置到布局上
            mContent.setText(spannable);
        }
    }
    //语音的Holder
    class AudioHolder extends BaseHolder{

        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.im_audio_track)
        ImageView mAudioTrack;
        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
           String attach=TextUtils.isEmpty(message.getAttach())?"":message.getAttach();
           mContent.setText(formatTime(attach));

        }
        //当播放开始
        void  onPlayStart(){
            mAudioTrack.setVisibility(View.VISIBLE);
        }
        //当播放结束
        void  onPlayStop(){
            mAudioTrack.setVisibility(View.INVISIBLE);
        }
        private String formatTime(String attach){
            float time;
            try {
                //毫秒转换为妙
                time=Float.parseFloat(attach)/1000f;
            } catch (Exception e) {
                time=0;
            }
            //取整以为小数点1.234->1.2  1.02->1.0
            String shortTime =String.valueOf(Math.round(time*10f)/10f);
            //1.0->1  1.2000->1.2去零操作
            shortTime=shortTime.replaceAll("[.]0+?$|0+?$","");
            return String.format("%s",shortTime);
        }

    }
    //图片的Holder
    class PicHolder extends BaseHolder{
        @BindView(R.id.im_image)
        ImageView  mContent;

        public PicHolder(View itemView) {
            super(itemView);

        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // 当时图片类型的时候，Content中就是具体的地址
            String content=message.getContent();
            Glide.with(ChatFragment.this)
                    .load(content)
                    .fitCenter()
                    .into(mContent);
        }
    }

    @Override
    public EditText getInputEditText() {
        return mContent;
    }
}
