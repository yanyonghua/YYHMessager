package www.yyh.com.myapplication.frags.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.qiujuer.genius.ui.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.widget.EmptyView;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.model.card.UserCard;
import www.yyh.com.factory.presenter.contact.FollowContract;
import www.yyh.com.factory.presenter.contact.FollowPresenter;
import www.yyh.com.factory.presenter.search.SearchContract;
import www.yyh.com.factory.presenter.search.SearchUserPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.PersonalActivity;
import www.yyh.com.myapplication.activities.SearchActivity;

/**
 * 搜索人的实现
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,SearchContract.UserView{

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecycle;

    private RecyclerAdapter mRecycleAdapter;
    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        mRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycle.setAdapter(mRecycleAdapter=new RecyclerAdapter<UserCard>() {
            @Override
            protected ViewHolder<UserCard> onCreataViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                //返回一个布局
                return R.layout.cell_search_list;

            }

        });
        mEmptyView.bind(mRecycle);
        setPlaceHolderView(mEmptyView);
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
        //初始化Presenter
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        //数据成功的情况下返回数据
        mRecycleAdapter.replace(userCards);
        //如果有数据则是OK没有数据就显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mRecycleAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.text_name)
        TextView mTextName;
        @BindView(R.id.im_follow)
        ImageView mIMFollow;
        private FollowContract.Presenter mPresenter;
        public ViewHolder(View itemView) {
            super(itemView);
             new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setup(Glide.with(SearchUserFragment.this),userCard);
            mTextName.setText(userCard.getName());
            mIMFollow.setEnabled(!userCard.isFollow());
        }
        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            //发起关注
            PersonalActivity.show(getActivity(),mData.getId());
        }
        @OnClick(R.id.im_follow)
        void onFollowClick(){
            //发起关注
            mPresenter.follow(mData.getId());
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            //更改当前界面状态
            if (mIMFollow.getDrawable() instanceof  LoadingDrawable){
                ((LoadingDrawable) mIMFollow.getDrawable()).stop();
                mIMFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            //发起更新
            updateData(userCard);
        }

        @Override
        public void showError(int str) {
            //更改当前界面状态
            if (mIMFollow.getDrawable() instanceof  LoadingDrawable){
                //失败则停止动画，并且显示一个圆圈
                LoadingDrawable drawable = (LoadingDrawable) mIMFollow.getDrawable();
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize= (int) Ui.dipToPx(getResources(),22);
            int maxSize= (int) Ui.dipToPx(getResources(),22);
            //初始化一个圆形动画的Drawable
            LoadingDrawable drawable =new LoadingCircleDrawable(minSize,maxSize);
            drawable.setBackgroundColor(0);

            int[] color=  new int[]{UiCompat.getColor(getResources(),R.color.white_alpha_208)};
            drawable.setForegroundColor(color);
            mIMFollow.setImageDrawable(drawable);
            //启动动画
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter=presenter;
        }
    }









}
