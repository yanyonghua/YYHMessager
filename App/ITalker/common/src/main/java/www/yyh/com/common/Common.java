package www.yyh.com.common;

/**
 * Created by 56357 on 2018/5/24
 */
public class Common {
    /**
     * 一些不可变得永恒的参数
     * 通常用于一些配置
     */
    public interface Constance{
        //手机号的正则，11位手机号
        String REGEX_MOBILE="[1][3,4,5,7,8][0-9]{9}$";
        //基础的网络请求
        String API_URL ="http://192.168.56.1:8081/api/";
    }

}
