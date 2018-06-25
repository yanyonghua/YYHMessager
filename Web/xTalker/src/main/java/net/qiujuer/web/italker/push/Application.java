package net.qiujuer.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.qiujuer.web.italker.push.provider.AuthRequestFilter;
import net.qiujuer.web.italker.push.provider.GsonProvider;
import net.qiujuer.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;
//app入口
public class Application extends ResourceConfig {
    public Application(){
        //注册逻辑处理的包名
        packages(AccountService.class.getPackage().getName());

        // 注册我们的全局请求拦截器
        register(AuthRequestFilter.class);
//        register(JacksonJsonProvider.class);
        //替换解析器为Gson
        register(GsonProvider.class);
        register(Logger.class);

    }
}
