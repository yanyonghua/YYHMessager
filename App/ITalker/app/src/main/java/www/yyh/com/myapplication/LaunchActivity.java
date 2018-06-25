package www.yyh.com.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

import www.yyh.com.common.app.Activity;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.myapplication.activities.AccountActivity;
import www.yyh.com.myapplication.activities.MainActivity;
import www.yyh.com.myapplication.assist.PermissionsFragment;

public class LaunchActivity extends Activity {
    // Drawable
    private ColorDrawable mBgDrawable;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //拿到根布局
        View root = findViewById(R.id.activity_launch);
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        //创建一个Drawable
        ColorDrawable colorDrawable = new ColorDrawable(color);
        //设置给背景
        root.setBackground(colorDrawable);
        mBgDrawable = colorDrawable;

    }

    @Override
    protected void initData() {
        super.initData();
        //动画进入到50%等待PushId获取到
        starAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                //检查等待id
                waitPushReceiverId();
            }
        });
    }

    /**
     * 等待个个推框架对我们的PushId设置好值
     */
    private void waitPushReceiverId(){
        if (Account.isLogin()){
            //已经登录情况下，判断是否绑定
            //如果没有绑定则登录广播接收器进行绑定
            if (Account.isBind()){
                skip();
                return;
            }
        }else {
            //没有登录
            //如果拿到了pushId，没有登录不能绑定id
            if (!TextUtils.isEmpty(Account.getPushId())){
                //跳转
                skip();
                return;
            }
        }
        //循环跳转
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                },500);

    }

    /**
     * 在跳转之前需要把剩下的50进行完成
     */
    private void skip() {
        starAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallyskip();
            }
        });

    }

    /**
     * 真实的跳转
     */
    private void reallyskip() {
        //权限检测，跳转
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
             if (Account.isLogin()){
                 MainActivity.show(this);
             }  else  AccountActivity.show(this);

            finish();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    /***
     * 给背景设置一个动画，动画的结束进度
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void starAnim(float endProgress, final Runnable endCallback) {
        //获取一个最终的颜色
        int finalColor = Resource.Color.WHITE;
        //运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.
                evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        //构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(1500);//设置时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor);//开始结束值
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
    };


}
