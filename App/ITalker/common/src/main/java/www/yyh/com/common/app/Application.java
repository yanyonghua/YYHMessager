package www.yyh.com.common.app;


import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

/**
 * Created by 56357 on 2018/5/30
 */
public class Application extends android.app.Application {

    private static Application instance;

    /**
     * 获取头像的临时存储文件地址
     * @return 临时文件
     */
    public static File getPortraitTmpFile(){
        //得到头像目录的缓存地址
        File dir =new File(getCacheDirFile(),"portrait");
        //创建所有的对应的文件夹
        dir.mkdirs();

        //删除旧的一些缓存文件
        File[] files=dir.listFiles();
        if (files!=null&&files.length>0){
            for (File file:files){
                file.delete();
            }
        }

        //返回一个当前时间戳的目录文件地址
        File path =new File(dir, SystemClock.uptimeMillis()+".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * 获取声音文件的本地地址，避免多个缓存文件
     * @param isTmp 是否是缓存文件 True ，每次返回的文件地址是一样的，否则就换成带时间戳的文件
     * @return
     */
    public static File getAudioTmpFile(boolean isTmp){
        File dir =new File(getCacheDirFile(),"audio");
        dir.mkdirs();
        //删除旧的一些缓存文件
        File[] files=dir.listFiles();
        if (files!=null&&files.length>0){
            for (File file:files){
                file.delete();
            }
        }
      File path=  new File(getCacheDirFile(),isTmp?"tmp.mp3":SystemClock.uptimeMillis()+".mp3");
        return path.getAbsoluteFile();
    }

    /**
     * 显示一个Toast
     * @param msg 字符串
     */
    public static void showToast(final  String msg){
        //Toast 只能在主线程中显示，需要进行线程转换
        //保证一定是主线程进行的show操作
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                Toast.makeText(instance,msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 显示一个Toast
     * @param msgId 字符串资源
     */
    public static void showToast(@StringRes int msgId){

        showToast(instance.getString(msgId));

    }

    /**
     * 获取缓存文件夹地址
     * @return 当前APP的缓存文件夹地址
     */
    public static File getCacheDirFile(){
        return instance .getCacheDir();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    /**
     * 外部获取单例
     * @return
     */
    public static Application getInstance(){
        return instance;
    }
}
