package www.yyh.com.factory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import www.yyh.com.common.app.Application;

/**
 * Created by 56357 on 2018/5/30
 */
public class Factory {

    private static final Factory instance;
    private final Executor executor;
    static {
        instance=new Factory();
    }

    private Factory(){
        //新建一个4个线程的线程池
        executor= Executors.newFixedThreadPool(4);
    }

    /**
     * 返回全局的Application
     * @return Application
     */
    public static Application app(){
        return Application.getInstance();
    }

    public static void runOnAsync(Runnable runnable){
        //拿到單例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }


}
