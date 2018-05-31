package www.yyh.com.myapplication;


import com.squareup.leakcanary.LeakCanary;

import www.yyh.com.common.app.Application;

/**
 * Created by 56357 on 2018/5/29
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }
}
