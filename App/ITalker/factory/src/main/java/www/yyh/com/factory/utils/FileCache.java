package www.yyh.com.factory.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import www.yyh.com.common.app.Application;
import www.yyh.com.factory.net.Network;
import www.yyh.com.utils.HashUtil;
import www.yyh.com.utils.StreamUtil;

/**
 * 简单的一个文件缓存，实现文件的下载操作
 * 下载成功后回调响应方法
 * Created by 56357 on 2018/7/11
 */
public class FileCache<Holder> {
    private File baseDir;//地址
    private String ext;//后缀名
    private CacheListener<Holder> listener;
    //最后一次目标
    private SoftReference<Holder> holderSoftReference;

    public FileCache(String baseDir,String ext,CacheListener<Holder> listener) {
        this.baseDir = new File(Application.getCacheDirFile(),baseDir);
        this.ext = ext;
        this.listener = listener;
    }
    //构建一个缓存文件，同一个网络路径对应以本地文件
    private File buildCacheFile(String path){
        String key = HashUtil.getMD5String(path);
        return new File(baseDir,key+"."+ext);
    }

    public void download(Holder holder,String path){
        //如果路径就是本地缓存路径，那么不需要下载
        if (path.startsWith(Application.getCacheDirFile().getAbsolutePath())){
            listener.onDownloadSuccessed(holder,new File(path));
            return;
        }
        //构建缓存文件
        final File cacheFile =buildCacheFile(path);
        if (cacheFile.exists()&&cacheFile.length()>0){
            //如果文件存在，无需重新下载
            listener.onDownloadSuccessed(holder,cacheFile);
            return;
        }
        //把目标进行软引用
        holderSoftReference =new SoftReference<>(holder);
        OkHttpClient client= Network.getClient();
        Request request =new Request.Builder()
                .url(path)
                .get().build();

        Call call =client.newCall(request);
        call.enqueue(new NetCallback(holder,cacheFile));
    }

    //拿最后的目标，只能使用一次
    private Holder getLastHolderAndClear(){
        if (holderSoftReference==null)
            return null;
        else {
            //拿并清理
            Holder holder=holderSoftReference.get();
            holderSoftReference.clear();
            return holder;
        }
    }

    private class NetCallback implements Callback{
        private final SoftReference<Holder> holderSoftReference;
        private final File file;

        public NetCallback(Holder holder, File file) {
            this.holderSoftReference = new SoftReference<>(holder);
            this.file = file;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Holder holder=holderSoftReference.get();
            if (holder!=null&&holder==getLastHolderAndClear()){
                FileCache.this.listener.onDownloadFailed(holder);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream inputStream =response.body().byteStream();
            //文件保存就是下载操作
            if (inputStream!=null&& StreamUtil.copy(inputStream,file)){
                Holder holder=holderSoftReference.get();
                if (holder!=null&&holder==getLastHolderAndClear()){
                    FileCache.this.listener.onDownloadSuccessed(holder,file);
                }
            }else {
                onFailure(call,null);
            }

        }
    }

    //相关的监听
    public interface CacheListener<Holder>{
        //成功把目标一块儿丢回去
        void onDownloadSuccessed(Holder holder,File file);
        void onDownloadFailed(Holder holder);
    }











}
