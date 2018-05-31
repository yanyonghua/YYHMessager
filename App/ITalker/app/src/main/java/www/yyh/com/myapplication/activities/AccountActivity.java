package www.yyh.com.myapplication.activities;

import android.content.Context;
import android.content.Intent;

import www.yyh.com.common.app.Activity;
import www.yyh.com.common.app.Fragment;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.account.UpdateInfoFragment;

public class AccountActivity extends Activity {

    private Fragment mFragment;
    /**
     * 账户Activity显示入口
     * @param context Context
     */
    public  static void show(Context context){
        context.startActivity(new Intent(context,AccountActivity.class));
    }
    @Override
    protected int getContentLayout() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mFragment = new UpdateInfoFragment();
        getSupportFragmentManager().
                beginTransaction()
                .add(R.id.lay_container, mFragment)
                .commit();
    }

    //Activity中收到剪切图片成功的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       mFragment.onActivityResult(requestCode,resultCode,data);
    }
}
