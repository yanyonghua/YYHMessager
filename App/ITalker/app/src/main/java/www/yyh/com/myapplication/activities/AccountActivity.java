package www.yyh.com.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;
import www.yyh.com.common.app.Activity;
import www.yyh.com.common.app.Fragment;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.account.AccountTrigger;
import www.yyh.com.myapplication.frags.account.LoginFragment;
import www.yyh.com.myapplication.frags.account.RegisterFragment;

public class AccountActivity extends Activity implements AccountTrigger{
    private Fragment mFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;
    @BindView(R.id.im_bg)
    ImageView imageView;
    /**
     * 账户Activity显示入口
     * @param context Context
     */
    public  static void show(Context context){
        context.startActivity(new Intent(context,AccountActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //初始化Fragment
        mLoginFragment= mFragment = new LoginFragment();
        getSupportFragmentManager().
                beginTransaction()
                .add(R.id.lay_container, mLoginFragment)
                .commit();
        //初始化背景
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView ,GlideDrawable>(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //拿到Glide的drawable
                        Drawable drawable = resource.getCurrent();
                        //使用适配类进行包装
                        drawable= DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(),R.color.colorAccent),
                                PorterDuff.Mode.SCREEN);//设置着色的效果和颜色，蒙板模式
                        //设置给ImageView
                        this.view.setImageDrawable(drawable);
                    }
                });
    }
    @Override
    protected int getContentLayout() {
        return R.layout.activity_account;
    }


    @Override
    public void triggerView() {
        Fragment fragment = null;
        if (mFragment==mLoginFragment){
            if (mRegisterFragment==null){
                //默认情况下为null
                //第一次之后就不为null
                mRegisterFragment=new RegisterFragment();
            }
            fragment=mRegisterFragment;
        }else {
            //因为默认情况下mLoginFragment
            fragment=mLoginFragment;
        }
        //重新复制当前正在显示的Fragment我们当前
        mFragment =fragment;
        //切换显示
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lay_container,mFragment)
                .commit();
    }
}
