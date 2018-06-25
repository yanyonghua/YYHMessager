package www.yyh.com.myapplication;


import com.igexin.sdk.PushManager;
import com.squareup.leakcanary.LeakCanary;

import www.yyh.com.common.app.Application;
import www.yyh.com.factory.Factory;

/**
 * Created by 56357 on 2018/5/29
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //调用Factory进行初始化
        Factory.setup();
        //推动进行初始化
        PushManager.getInstance().initialize(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }
}
