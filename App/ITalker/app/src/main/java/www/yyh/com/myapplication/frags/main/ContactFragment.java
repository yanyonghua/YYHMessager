package www.yyh.com.myapplication.frags.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.widget.EmptyView;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.presenter.contact.ContactContract;
import www.yyh.com.factory.presenter.contact.ContactPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.MessageActivity;
import www.yyh.com.myapplication.activities.PersonalActivity;

/**
 * 联系人列表
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
        implements ContactContract.View{

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecycle;

    //适配器，user直接从数据库查询
    private RecyclerAdapter mRecycleAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        mRecycle.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecycle.setAdapter(mRecycleAdapter=new RecyclerAdapter<User>() {
            @Override
            protected ViewHolder<User> onCreataViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, User user) {
                return R.layout.cell_contact_list;
            }
        });

        mEmptyView.bind(mRecycle);
        setPlaceHolderView(mEmptyView);
        mRecycleAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>(){
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                MessageActivity.show(getContext(),user);
            }
        });
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mRecycleAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mRecycleAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<User>{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.text_name)
        TextView mTextName;
        @BindView(R.id.text_desc)
        TextView mDesc;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            //发起关注
            PersonalActivity.show(getActivity(),mData.getId());
        }
        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(ContactFragment.this),user);
            mTextName.setText(user.getName());
            mDesc.setText(user.getDescription());
        }
    }
}
