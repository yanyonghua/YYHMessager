package www.yyh.com.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.res.Resource;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.PresenterToolbarActivity;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.presenter.contact.PersonalContract;
import www.yyh.com.factory.presenter.contact.PersonalPresenter;
import www.yyh.com.myapplication.R;

public class PersonalActivity extends PresenterToolbarActivity<PersonalContract.Presenter>
    implements PersonalContract.View {

    private static final String BOUND_KEY_ID="BOUND_KEY_ID";
    private String userId;


    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.im_header)
    ImageView mHeader;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_name)
    TextView mName;
    @BindView(R.id.text_follows)
    TextView mFollows;
    @BindView(R.id.text_following)
    TextView mFollowing;
    @BindView(R.id.text_desc)
    TextView mDesc;
    @BindView(R.id.btn_say_hello)
    TextView mSayHello;
    private MenuItem mFollowItem;

    private boolean mIsFollowUser=false;

    public static void show(Context context,String userId){
        Intent intent = new Intent(context,PersonalActivity.class);
        intent.putExtra(BOUND_KEY_ID,userId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_personal;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {

        userId = bundle.getString(BOUND_KEY_ID);
        return !TextUtils.isEmpty(userId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
//        setTitle("");
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();//获得menu的填充
        menuInflater.inflate(R.menu.personal,menu);//实例化布局
        mFollowItem = menu.findItem(R.id.action_follow);//实例化控件
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_follow==item.getItemId()){
            //进行关注操作
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_say_hello)
    void onSayHelloClick(){
        User user = mPresenter.getUserPersonal();
         if (user==null)
             return;
         MessageActivity.show(this,user);
    }

    /**
     * 更改关注菜单状态
     */
    private void changeFollowItemState(){
        if (mFollowItem==null){
            return;
        }
        //根据状态设置颜色
        Drawable drawable =mIsFollowUser?getResources().getDrawable(R.drawable.ic_favorite):
                getResources().getDrawable(R.drawable.ic_favorite_border);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable,Resource.Color.WHITE);
        mFollowItem.setIcon(drawable);
    }

    @Override
    protected PersonalContract.Presenter initPresenter() {
        return new PersonalPresenter(this);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onLoadDone(User user) {
        if (user==null)
            return;
        mPortrait.setup(Glide.with(this),user);
        mName.setText(user.getName());
        mDesc.setText(user.getDescription());
        mFollows.setText(String.format(getString(R.string.label_follows),user.getFollows()));
        mFollowing.setText(String.format(getString(R.string.label_following),user.getFollowing()));
        hideLoading();
    }

    @Override
    public void allowSayHello(boolean isAllow) {
        mSayHello.setVisibility(isAllow? View.VISIBLE:View.GONE);
    }

    @Override
    public void setFollowStates(boolean isFollow) {
        mIsFollowUser=isFollow;
        changeFollowItemState();
    }
}
