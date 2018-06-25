package www.yyh.com.factory.net;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import www.yyh.com.common.Common;
import www.yyh.com.factory.Factory;
import www.yyh.com.factory.persistence.Account;

/**
 * Created by 56357 on 2018/6/5
 */
public class Network {

    private static Network instance;
    private Retrofit retrofit;

    static {
        instance=new Network();
    }

    private Network(){}
    //构建一个Retrofit
    public static Retrofit getRetrofit(){
        if (instance.retrofit!=null){
            return instance.retrofit;
        }

        //得到一个OK client
        OkHttpClient client =new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original =chain.request();
                        Request.Builder builder =original.newBuilder();
                        if (!TextUtils.isEmpty(Account.getToken()))
                        builder.addHeader("token",Account.getToken());
                        builder.addHeader("Content-Type","application/json");
                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit.Builder builder =new Retrofit.Builder();

        instance.retrofit= builder.baseUrl(Common.Constance.API_URL)
         //设置client
        .client(client)
          //设置一个gson解析器
         .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
        .build();
    return instance.retrofit;
    }


    /**
     * 返回一个请求代理
     * @return
     */
    public static RemoteService remote(){
        return Network.getRetrofit().create(RemoteService.class);
    }



















}
