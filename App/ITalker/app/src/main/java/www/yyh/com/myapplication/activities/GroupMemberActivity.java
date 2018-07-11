package www.yyh.com.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.PresenterToolbarActivity;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.presenter.group.GroupMembersContract;
import www.yyh.com.factory.presenter.group.GroupMembersPresenter;
import www.yyh.com.myapplication.R;

public class GroupMemberActivity extends PresenterToolbarActivity<GroupMembersContract.Presenter>
implements GroupMembersContract.View{
    private static final String KEY_GROUP_ID ="KEY_GROUP_ID";
    private static final String KEY_GROUP_ADMIN ="KEY_GROUP_ADMIN";

    private  boolean isAdmin;
    private  String mGroupId;
    @BindView(R.id.recycle)
    RecyclerView mRecycler;

    private RecyclerAdapter<MemberUserModel> mAdapter;

    public static void show(Context context ,String groupId){
        show(context,groupId,false);

    }
    public static void showAdmin(Context context ,String groupId){
        show(context,groupId,true);
    }

    public static void show(Context context ,String groupId,boolean isAdmin){
        if (groupId==null)return;
        Intent intent = new Intent(context ,GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID,groupId);
        intent.putExtra(KEY_GROUP_ADMIN,isAdmin);
        context.startActivity(intent);
    }
    @Override
    protected int getContentLayout() {
        return R.layout.activity_group_member;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
       String groupId= bundle.getString(KEY_GROUP_ID);
       this.mGroupId =groupId;
       this.isAdmin = bundle.getBoolean(KEY_GROUP_ADMIN);
        return !TextUtils.isEmpty(mGroupId);

    }

    @Override
    protected void initWidget() {
        super.initWidget();

        setTitle(R.string.title_member_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter=new RecyclerAdapter<MemberUserModel>() {
            @Override
            protected ViewHolder<MemberUserModel> onCreataViewHolder(View root, int viewType) {
                return new GroupMemberActivity.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, MemberUserModel memberUserModel) {
                return R.layout.cell_group_create_contact;
            }
        });
    }

    @Override
    protected GroupMembersContract.Presenter initPresenter() {
        return new GroupMembersPresenter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        // 开始数据刷新
        mPresenter.refresh();
    }

    @Override
    public RecyclerAdapter<MemberUserModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //隐藏Loading就好
        hideLoading();
    }

    @Override
    public String getmGroupId() {
        return mGroupId;
    }

    class ViewHolder  extends RecyclerAdapter.ViewHolder<MemberUserModel>{
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.cb_select).setVisibility(View.GONE);
        }


        @Override
        protected void onBind(MemberUserModel viewModel) {
            mPortrait.setup(Glide.with(GroupMemberActivity.this),viewModel.portrait);
            mName.setText(viewModel.name);

        }
        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(GroupMemberActivity.this,mData.userId);
        }
    }
}
