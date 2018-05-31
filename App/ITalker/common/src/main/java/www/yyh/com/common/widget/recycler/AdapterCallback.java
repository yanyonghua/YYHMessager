package www.yyh.com.common.widget.recycler;

/**
 * Adapter自己更新的接口
 * Created by 56357 on 2018/5/24
 */
public interface AdapterCallback<Data> {
    void update(Data date, RecyclerAdapter.ViewHolder<Data> holder);
}
