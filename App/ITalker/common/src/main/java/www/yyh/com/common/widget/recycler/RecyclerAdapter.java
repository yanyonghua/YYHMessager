package www.yyh.com.common.widget.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import www.yyh.com.common.R;

/**
 * Created by 56357 on 2018/5/24
 */
public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {
    private AdapterListener mListerer;
    private final List<Data> mDataList;

    /**
     * 构造函数模块
     */
    public RecyclerAdapter() {
        this(new ArrayList<Data>(), null);
    }

    public RecyclerAdapter(AdapterListener adapterListener) {
        this(new ArrayList<Data>(), adapterListener);
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener adapterListener) {
        this.mDataList = dataList;
        this.mListerer = adapterListener;
    }


    /**
     * 创建一个ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面类型
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //得到LayoutInflater用于XML初始化为View
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //把XML id为viewType的文件初始化为一个 root View
        View root = inflater.inflate(viewType, parent, false);
        //通过子类嘘嘘实现的方法，得到一个ViewHolder
        ViewHolder<Data> holder = onCreataViewHolder(root, viewType);
        //设置View的Tag为ViewHolder，进行双向绑定
        root.setTag(R.id.tag_recycler_holder,holder);
        //设置事件点击
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        //进行界面注解绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        holder.callback = this;
        return holder;
    }

    /**
     * 得到一个新的ViewHolder
     *
     * @param root     根布局
     * @param viewType 布局的类型，其实就XML的id
     * @return ViewHolder
     */
    protected abstract ViewHolder<Data> onCreataViewHolder(View root, int viewType);


    /**
     * 绑定数据到一个Holder上
     *
     * @param holder   ViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> holder, int position) {
        //得到需要绑定的数据
        Data data = mDataList.get(position);
        //触发Holder的绑定方法
        holder.onBind(data);
    }


    /**
     * 得到当前集合数据量
     */
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 插入一条数据并通知插入
     *
     * @param data Data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyItemChanged(mDataList.size() - 1);
    }

    /**
     * 插入一堆数据，并通知这段集合更新
     *
     * @param dataList Data
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    /**
     * 插入一堆数据，并通知这段集合更新
     *
     * @param dataList Data
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    /**
     * 清除集合
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换为一个新的集合，其中包括了清空
     *
     * @param dataList 一个新的集合
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0)
            return;
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }




    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListerer != null) {
            //得到ViewHolder对应适配器中的坐标
            int adapterPosition = holder.getAdapterPosition();
            Data data = mDataList.get(adapterPosition);
            //回调方法
            this.mListerer.onItemClick(holder, data);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListerer != null) {
            //得到ViewHolder对应适配器中的坐标
            int adapterPosition = holder.getAdapterPosition();
            //回调方法
            this.mListerer.onItemLongClick(holder, adapterPosition);
            return true;
        }
        return false;
    }

    /**
     * 设置适配器监听
     *
     * @param adapterListener AdapterListener
     */
    public void setListener(AdapterListener<Data> adapterListener) {
        this.mListerer = adapterListener;
    }

    @Override
    public void update(Data date, ViewHolder<Data> holder) {
        //得到当前ViewHolder的坐标
        int pos =holder.getAdapterPosition();
        if (pos>=0){
            //进行数据转移和更新
            mDataList.remove(pos);
            mDataList.add(pos,date);
            //通知这个坐标下的数据有更新
            notifyItemChanged(pos);
        }
    }

    /**
     * 我们的自定义监听器
     *
     * @param <Data> 泛型
     */
    public interface AdapterListener<Data> {
        //当cell点击时触发
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);

        //cell长按的时候触发
        void onItemLongClick(RecyclerAdapter.ViewHolder holder, Data data);
    }


    /**
     * 自定义的ViewHolder
     *
     * @param <Data> 泛型类型
     */
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
        Unbinder unbinder;
        protected Data mData;
        private AdapterCallback callback;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 用于绑定数据的触发
         *
         * @param data 绑定的数据
         */
        void bind(Data data) {
            this.mData = data;
        }

        /**
         * 当触发绑定数据的时候的回调，必须复写
         *
         * @param data 绑定的数据
         */
        protected abstract void onBind(Data data);

        /**
         * Holder自己对自己的Data进行更新操作
         *
         * @param data
         */
        public void updateData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }

    /**
     * 复写默认的布局类型返回
     *
     * @param position 坐标
     * @return 类型，其实复写后返回的都是XML文件的ID
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局的类型
     *
     * @param position 坐标
     * @param data     当前的数据
     * @return XML文件的ID，用于创建ViewHolder
     */
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 对回调接口做一次实现
     * @param <Data>
     */
    public static class AdapterListenerImpl<Data> implements AdapterListener<Data>{

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, Data data) {

        }
    }
}
