package www.yyh.com.factory.data;

import java.util.List;

/**
 * 基础的数据库数据源接口定义
 * Created by 56357 on 2018/6/25
 */
public interface DbDataSource<Data> extends DataSource{
    /**
     * 有一个基本的数据源加载方法
     * @param callback 传递一个Callback回调，一般回调到Prensenter
     */
    void load(SucceedCallback<List<Data>> callback);
}
