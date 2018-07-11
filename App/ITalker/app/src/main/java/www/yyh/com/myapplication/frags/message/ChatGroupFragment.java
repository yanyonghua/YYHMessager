package www.yyh.com.myapplication.frags.message;


import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.List;

import butterknife.BindView;
import www.yyh.com.factory.model.db.Group;
import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.presenter.message.ChatContract;
import www.yyh.com.factory.presenter.message.ChatGroupPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.GroupMemberActivity;
import www.yyh.com.myapplication.activities.PersonalActivity;

/**
 * 群聊天的界面
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group>
implements ChatContract.GroupView {

    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_members)
    LinearLayout mLayMembers;

    @BindView(R.id.text_member_more)
    TextView mMemberMore;

    public ChatGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getHeaderLayoutid() {
        return R.layout.lay_chat_header_group;
    }



    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this,mReceiverId);
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        Glide.with(this)
                .load(R.drawable.default_banner_group)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout,GlideDrawable>(mCollapsingLayout) {

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }
    @Override
    public void onInit(Group group) {
        mCollapsingLayout.setTitle(group.getName());
        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(mHeader);
    }


    @Override
    public void showAdminOption(boolean isAdmin) {
        if (isAdmin){
            mToolbar.inflateMenu(R.menu.chat_group);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId()== R.id.action_add){
                        //mReceiverId 就是群ID 群成员添加
                        GroupMemberActivity.show(getContext(),mReceiverId);
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    //进行高度的综合运算，透明我们的头像和icon
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        View view =mLayMembers;
        if (view==null){
            return;
        }


        if (verticalOffset==0){//相对父类的偏移量
            //完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);

        }else {
            //abs 运算
            verticalOffset =Math.abs(verticalOffset);
            final int totalScoll =appBarLayout.getTotalScrollRange();//返回总高度
            if (verticalOffset>=totalScoll){
                //关闭状态
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

            }else {
                // 中间状态
                float progress =1-verticalOffset/(float)totalScoll;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
            }
        }
    }
    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, long moreCount) {
        if (members==null||members.size()==0){
            return;
        }
        LayoutInflater inflater =LayoutInflater.from(getContext());

        for (final MemberUserModel member : members) {
           ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait,mLayMembers,false);
            mLayMembers.addView(p,0);
            Glide.with(this)
                    .load(member.portrait)
                    .placeholder(R.drawable.default_portrait)
                    .centerCrop()
                    .dontAnimate()
                    .into(p);
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.show(getContext(),member.userId);
                }
            });
        }

        if (moreCount>0){
            mMemberMore.setText(String.format("+%s",moreCount));
            mMemberMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mReceiverId 就是群ID 群成员添加
                    GroupMemberActivity.showAdmin(getContext(),mReceiverId);
                }
            });
        }else mMemberMore.setVisibility(View.GONE);

    }
}
