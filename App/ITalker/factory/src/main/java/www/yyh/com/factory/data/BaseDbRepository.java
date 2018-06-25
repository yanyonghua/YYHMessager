package www.yyh.com.factory.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import www.yyh.com.factory.data.helper.DbHelper;
import www.yyh.com.factory.model.db.BaseDbModel;
import www.yyh.com.utils.CollectionUtil;

/**
 * 基础的数据库仓库
 * 实现对数据库的基本监听操作
 * Created by 56357 on 2018/6/25
 */
public abstract class BaseDbRepository<Data extends BaseDbModel<Data>>
        implements DbDataSource<Data>,
        DbHelper.ChangedListener<Data>,
        QueryTransaction.QueryResultListCallback<Data>{
    //和Presenter交互的回调
    private SucceedCallback<List<Data>> callback;
    private final List<Data> dataList =new LinkedList<>();
    private Class<Data> dataClass;

    public BaseDbRepository(){
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class,this.getClass());
        dataClass= (Class<Data>) types[0];
    }
    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback=callback;
        //进行数据库监听
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        //取消监听，销毁数据
        this.callback=null;
        DbHelper.removeChangedListener(dataClass,this);
        dataList.clear();
    }

    //数据库统一通知的地方 增加/更改
    @Override
    public void onDataSave(Data... list) {
        boolean isChanged=false;
        // 当数据库数据变更的操作
        for (Data data : list) {
            //是关注的人，同时不是我自己
            if (isRequired(data)){
                insertOrUpdate(data);
                isChanged=true;
            }
        }
        //有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();
    }
    //数据库统一通知的地方 删除
    @Override
    public void onDataDelete(Data... list) {
        //再删除情况下不用进行过滤判断
        // 当数据库数据删除的操作
        boolean isChanged=false;
        for (Data data : list) {
            boolean remove = dataList.remove(data);
            if (remove){
                isChanged=true;
            }
        }
        //有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();
    }

    // DbFlow 框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        if (tResult.size()==0){
            dataList.clear();
            notifyDataChange();
            return;
        }
        //转变为数组
        Data[] users = CollectionUtil.toArray(tResult,dataClass);

        // 回到数据集更新的操作中
        onDataSave(users);
    }

    //通知界面刷新的方法
    private void  notifyDataChange(){
        SucceedCallback<List<Data>> callback=this.callback;
        if (callback!=null)
            callback.onDataLoaded(dataList);
    }
    private void insertOrUpdate(Data data){
        int index =indexOf(data);
        if (index>=0){
            replace(index,data);
        }else {
            insert(data);
        }
    }
    //替换方法，更新某个坐标下的数据
    private void replace(int index,Data data){
        dataList.remove(index);
        dataList.add(index, data);
    }
    //添加方法 插入或者更新
    private void insert(Data data){
        dataList.add(data);
    }

    /**
     * 判断这个user有没有在list里面
     * @param newData User
     * @return 有则返回非-1的数字，没有返回-1
     */
    private int indexOf(Data newData){
        int index =-1;
        for (Data data1 : dataList) {
            index++;
            if (data1.isSame(newData)){
                return index;
            }
        }
        return -1;
    }

    /**
     * 检查一个User是否是我需要关注的数据
     * @param data User
     * @return true 是我关注的数据
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener(){
        // 对数据辅助工具类添加一个数据更新的监听
        DbHelper.addChangedListener(dataClass,this);
    }
}
