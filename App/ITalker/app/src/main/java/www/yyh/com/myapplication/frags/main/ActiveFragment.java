package www.yyh.com.myapplication.frags.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.Fragment;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.widget.EmptyView;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.face.Face;
import www.yyh.com.factory.model.db.Session;
import www.yyh.com.factory.presenter.message.SessionContract;
import www.yyh.com.factory.presenter.message.SessionPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.MessageActivity;
import www.yyh.com.myapplication.activities.PersonalActivity;
import www.yyh.com.utils.DateTimeUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
implements SessionContract.View{

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecycle;

    //适配器，user直接从数据库查询
    private RecyclerAdapter<Session> mRecycleAdapter;


    public ActiveFragment() {
        // Required empty public constructor
    }



    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        mRecycle.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecycle.setAdapter(mRecycleAdapter=new RecyclerAdapter<Session>() {
            @Override
            protected ViewHolder<Session> onCreataViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, Session user) {
                return R.layout.cell_chat_list;
            }
        });

        mEmptyView.bind(mRecycle);
        setPlaceHolderView(mEmptyView);
        mRecycleAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>(){
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                MessageActivity.show(getContext(),session);
            }
        });
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_active;
    }



    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mRecycleAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mRecycleAdapter.getItemCount()>0);
    }

    //界面的数据渲染
    class ViewHolder extends RecyclerAdapter.ViewHolder<Session>{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.text_name)
        TextView mTextName;
        @BindView(R.id.text_content)
        TextView mContent;
        @BindView(R.id.text_time)
        TextView mTime;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            //发起关注
            PersonalActivity.show(getActivity(),mData.getId());
        }
        @Override
        protected void onBind(Session session) {
            mPortraitView.setup(Glide.with(ActiveFragment.this),session.getPicture());
            mTextName.setText(session.getTitle());
            if (session.getContent().contains("oss-cn-shenzhen.aliyuncs.com/audio")){
                mContent.setText("[语音]");
            }else if (session.getContent().contains("oss-cn-shenzhen.aliyuncs.com/image")){
                mContent.setText("[图片]");
            }
           else{
                Spannable spannable =new SpannableString(TextUtils.isEmpty(session.getContent())?"":session.getContent());
                //解析表情
                Face.decode(mContent,spannable, (int) mContent.getTextSize());
                mContent.setText(spannable);
            }
            mTime.setText(session.getModifyAt()!=null?DateTimeUtil.getSimpleDate(session.getModifyAt()):"");
        }
    }
}
