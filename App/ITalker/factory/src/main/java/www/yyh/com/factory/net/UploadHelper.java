package www.yyh.com.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.Date;

import www.yyh.com.factory.Factory;
import www.yyh.com.utils.HashUtil;

/**
 * 上传工具类，用于上传任意文件到阿里OOS存储
 * Created by 56357 on 2018/5/30
 */
public class UploadHelper {
    //与你们的存储区域有关系
    public static final String ENDPOIT= "http://oss-cn-shenzhen.aliyuncs.com";


    //
    private static  final String BUCKET_NAME ="xun-liao";
    private static String TAG=UploadHelper.class.getName();

    private static OSS getClient(){
        // 在移动端建议使用STS的方式初始化OSSClient，更多信息参考：[访问控制]
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("",
                "");

       return new OSSClient(Factory.app(), ENDPOIT, credentialProvider);
    }

    /**
     * 上传的最终方法，成功返回这一个路径
     * @param objKey 上传上去后，在服务器上的独立的KEY，加斜杠就是加文件夹的动作
     * @param path 需要上传的文件的路径
     * @return 存储的地址
     */
    private static String upload(String objKey,String path){
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);
        try{
            //初始化上传的Client
            OSS client =getClient();
            //开始同步上传
            PutObjectResult result = client.putObject(request);
            //得到一个外网可以访问的地址
            String url =client.presignPublicObjectURL(BUCKET_NAME,objKey);
            //格式打印输出
            Log.d(TAG, String.format("PublicObjectURL: %S ",url) );
            return url;
        }catch (Exception e){
            e.printStackTrace();
            //如果有异常则返回空
            return null;
        }
    }

    /**
     * 上传图片
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path){
        String key =getImageObjectKey(path);
        return upload(key,path);
    }

    /**
     * 上传头像
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path){
        String key =getPortraitObjectKey(path);
        return upload(key,path);
    }

    /**
     * 上传音频
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path){
        String key =getAudioObjectKey(path);
        return upload(key,path);
    }

    /**
     * 分月存储
     * @return yyyyMM
     */
    private static String getDateString(){
        return DateFormat.format("yyyyMM",new Date()).toString();
    }
    //image/201805/asdfasdfaskl.jpg
    private static String getImageObjectKey(String path){
            String fileMd5 = HashUtil.getMD5String(new File(path));
            String dateString =getDateString();
            return String.format("image/%s/%s.jpg",dateString,fileMd5);
    }
    //portrait/201805/asdfasdfaskl.jpg
    private static String getPortraitObjectKey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString =getDateString();
        return String.format("portrait/%s/%s.jpg",dateString,fileMd5);
    }
    //audio/201805/asdfasdfaskl.mp3
    private static String getAudioObjectKey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString =getDateString();
        return String.format("audio/%s/%s.mp3",dateString,fileMd5);
    }

}
