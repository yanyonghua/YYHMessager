package www.yyh.com.myapplication.frags.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.widget.EmptyView;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.presenter.search.SearchContract;
import www.yyh.com.factory.presenter.search.SearchGroupPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.PersonalActivity;
import www.yyh.com.myapplication.activities.SearchActivity;

/**
 * 搜索群的实现
 */
public class SearchGroupFragment  extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,SearchContract.GroupView{


    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecycle;

    private RecyclerAdapter<GroupCard> mRecycleAdapter;
    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        mRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycle.setAdapter(mRecycleAdapter=new RecyclerAdapter<GroupCard>() {
            @Override
            protected ViewHolder<GroupCard> onCreataViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }
            @Override
            protected int getItemViewType(int position, GroupCard userCard) {
                //返回一个布局
                return R.layout.cell_search_group_list;
            }
        });
        mEmptyView.bind(mRecycle);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {
        //Activity->Fragment->Presenter->net
        mPresenter.search(content);
    }

    @Override
    protected void initData() {
        super.initData();
        search("");
    }
    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> groupCards) {
        //数据成功的情况下返回数据
        mRecycleAdapter.replace(groupCards);
        //如果有数据则是OK没有数据就显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mRecycleAdapter.getItemCount()>0);

    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard>
            {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.text_name)
        TextView mTextName;

        @BindView(R.id.im_join)
        ImageView mJoin;

        public ViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortraitView.setup(Glide.with(SearchGroupFragment.this),groupCard.getPicture());
            mTextName.setText(groupCard.getName());
            //加入时间判断是否加入群
            mJoin.setEnabled(groupCard.getJoinAt()==null);
        }
        @OnClick(R.id.im_join)
        void onJoinClick(){
            //进入创建者个人界面
            PersonalActivity.show(getContext(),mData.getOwnerId());
        }
       }
}
