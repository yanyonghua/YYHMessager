package www.yyh.com.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.Activity;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.main.ActiveFragment;
import www.yyh.com.myapplication.frags.main.ContactFragment;
import www.yyh.com.myapplication.frags.main.GroupFragment;
import www.yyh.com.myapplication.helper.NavHelper;


public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appBar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.im_search)
    ImageView mSearch;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;
    private NavHelper<Integer> mNavHelper;
    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        //从底部导航中接管我们的Menu，然后进行手动触发第一次点击
        Menu menu = mNavigation.getMenu();
        //触发首次选中Home
        menu.performIdentifierAction(R.id.action_home,0);
        mPortrait.setup(Glide.with(this),Account.getUser());
    }

    /**
     * MainActivity显示的入口
     * @param context 上下文
     */
    public static void show(Context context){
        context.startActivity(new Intent(context,MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()){//如果没有完善则跳转到UserActivity，完善了就走正常流程
            return super.initArgs(bundle);
        }else {
            UserActivity.show(this);
            return false;
        }

    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //初始化辅助工具类
         mNavHelper = new NavHelper(this,R.id.lay_container,
                 getSupportFragmentManager(),this);

        mNavHelper.add(R.id.action_home,new NavHelper.Tab<>(ActiveFragment.class,R.string.title_home))
        .add(R.id.action_group,new NavHelper.Tab<Integer>(GroupFragment.class,R.string.title_group))
        .add(R.id.action_contact,new NavHelper.Tab<Integer>(ContactFragment.class,R.string.title_contact));

        //添加对底部按钮的监听
        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View,GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });


    }


    @OnClick(R.id.im_search)
    void onSearchMenuClick(){
        //在群的界面的时候，点击顶部的搜索就进入群搜索界面
        // 其他都为人搜索界面
        int type =Objects.equals(mNavHelper.getCurrentTab().extra,R.string.title_group)?
                SearchActivity.TYPE_GROUP:SearchActivity.TYPE_USER;
        SearchActivity.show(this,type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick(){
        //浮动按钮点击时，判断当前界面是群还是联系人界面
        //如果是群 ，则打开群创建的界面
        if (Objects.equals(mNavHelper.getCurrentTab().extra,R.string.title_group)){
           GroupCreateActivity.show(this);
        }else{
            SearchActivity.show(this,SearchActivity.TYPE_USER);
        }
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(this,Account.getUserId());
    }

    /**
     * 当我们底部导航被点击的时候触发
     * @param item MenuItem
     * @return True 代表我们能够处理这个点击
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //转接事件留到工具类中
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper 处理后回调的方法
     * @param newTab new的Tab
     * @param oldTab 旧的Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //从额外字段中取出我们的Title资源id
        mTitle.setText(newTab.extra);

        //对浮动按钮进行隐藏于显示动画
        float transY=0;
        float rotation=0;
        if (Objects.equals(newTab.extra,R.string.title_home)){
            //主界面是隐藏
            transY = Ui.dipToPx(getResources(),76);
        }else {
            //TransY默认为0显示
            if (Objects.equals(newTab.extra,R.string.title_group)){
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation=-360;
            }else {
                //联系人
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation =360;
            }
        }
        //开始动画
        //旋转Y轴位移，弹性插值器，时间
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setDuration(480)
                .start();
    }
}
