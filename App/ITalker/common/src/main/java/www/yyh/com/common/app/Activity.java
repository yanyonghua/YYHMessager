package www.yyh.com.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by 56357 on 2018/5/24
 */
public abstract class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面未初始化之前调用的窗口
        if (initArgs(getIntent().getExtras())) {
            int contentLayout = getContentLayout();
            setContentView(contentLayout);
            initWidget();
            initData();
        } else {
            finish();
        }
    }

    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到当前界面的资源文件id
     *
     * @return 资源文件ID
     */
    protected abstract int getContentLayout();

    /*
     * 初始化控件
     * */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /*
     * 初始化控件
     * */
    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击界面导航返回时，Finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //得到当前activity下的所有Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //判断是否为空
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                //判断是否为我们能够处理的Fragment类型
                if (fragment instanceof www.yyh.com.common.app.Fragment) {
                    //判断是否拦截了返回按钮
                    if (((www.yyh.com.common.app.Fragment) fragment).onBackPressed()) {
                        //如果有直接
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }
}
