package www.yyh.com.myapplication;

import android.widget.TextView;

import butterknife.BindView;
import www.yyh.com.common.app.Activity;

public class MainActivity extends Activity {

    @BindView(R.id.txt_test)
    TextView mTestText;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTestText.setText("hello world.");
    }
}
