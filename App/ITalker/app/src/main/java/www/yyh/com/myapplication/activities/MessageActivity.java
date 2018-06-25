package www.yyh.com.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import www.yyh.com.factory.model.Author;
import www.yyh.com.myapplication.R;

public class MessageActivity extends AppCompatActivity {

    /**
     * 显示人的聊天界面
     * @param context 上下文
     * @param author 人的信息
     */
    public static void show(Context context, Author author){
        context.startActivity(new Intent(context,MessageActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }
}
