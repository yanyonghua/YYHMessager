package www.yyh.com.factory.model.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import www.yyh.com.factory.utils.DiffUiDataCallback;

/**
 *  我们app中基础的一个BaseDbModel
 *  基础数据库框架DbFlow中的基础类
 *  同时定义了我们需要的方法
 * Created by 56357 on 2018/6/25
 */
public abstract   class BaseDbModel<Model>  extends BaseModel
        implements DiffUiDataCallback.UiDataDiffer<Model>{
}
