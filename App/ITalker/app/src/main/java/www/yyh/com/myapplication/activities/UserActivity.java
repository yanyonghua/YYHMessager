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
import www.yyh.com.myapplication.frags.user.UpdateInfoFragment;

/**
 * 用户信息界面
 * 可以提供用户信息修改
 */
public class UserActivity extends Activity {
    private Fragment mFragment;


    @BindView(R.id.im_bg)
    ImageView imageView;

    public static void show(Context context){
        context.startActivity(new Intent(context,UserActivity.class));
    }
    @Override
    protected int getContentLayout() {
        return R.layout.activity_user;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //初始化Fragment
        mFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mFragment)
                .commit();


        //初始化背景
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView,GlideDrawable>(imageView) {
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

    //Activity中收到剪切图片成功的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode,resultCode,data);
    }


}
