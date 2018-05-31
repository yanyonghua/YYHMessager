package www.yyh.com.myapplication;

import www.yyh.com.common.app.Activity;
import www.yyh.com.myapplication.activities.MainActivity;
import www.yyh.com.myapplication.assist.PermissionsFragment;

public class LaunchActivity extends Activity {


    @Override
    protected int getContentLayout() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionsFragment.haveAll(this,getSupportFragmentManager())){
            MainActivity.show(this);
            finish();
        }
    }
}
