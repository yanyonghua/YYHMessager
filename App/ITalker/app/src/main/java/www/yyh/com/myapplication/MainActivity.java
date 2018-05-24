package www.yyh.com.myapplication;

import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.Activity;

public class MainActivity extends Activity  {

    @BindView(R.id.txt_Result)
    TextView mResultText;
    @BindView(R.id.edit_query)
    EditText mEditQuery;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initWidget() {
        super.initWidget();
//        mResultText.setText("hello world.");
    }
    @OnClick(R.id.btn_submit)
    void onSubmit(){

    }


}
