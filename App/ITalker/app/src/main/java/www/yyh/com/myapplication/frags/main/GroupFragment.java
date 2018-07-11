package www.yyh.com.myapplication.frags.main;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import www.yyh.com.common.app.Fragment;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.widget.EmptyView;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.model.db.Group;
import www.yyh.com.factory.presenter.group.GroupsContract;
import www.yyh.com.factory.presenter.group.GroupsPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.MessageActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends PresenterFragment<GroupsContract.Presenter>
implements GroupsContract.View{
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecycle;

    //适配器，user直接从数据库查询
    private RecyclerAdapter<Group> mRecycleAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        mRecycle.setLayoutManager(new GridLayoutManager(getContext(),2));

        mRecycle.setAdapter(mRecycleAdapter=new RecyclerAdapter<Group>() {
            @Override
            protected ViewHolder<Group> onCreataViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, Group user) {
                return R.layout.cell_group_list;
            }
        });

        mEmptyView.bind(mRecycle);
        setPlaceHolderView(mEmptyView);
        mRecycleAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>(){
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group user) {
                MessageActivity.show(getContext(),user);
            }
        });
    }

    @Override
    protected GroupsContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mRecycleAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mRecycleAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Group>{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.text_name)
        TextView mTextName;
        @BindView(R.id.text_desc)
        TextView mDesc;
        @BindView(R.id.text_members)
        TextView mMembers;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            mPortraitView.setup(Glide.with(GroupFragment.this),group.getPicture());
            mTextName.setText(group.getName());
            mDesc.setText(group.getDesc());
            if (group.holder!=null&&group.holder instanceof String){
                mMembers.setText((String)group.holder);
            }else
            mMembers.setText("");
        }
    }
}
