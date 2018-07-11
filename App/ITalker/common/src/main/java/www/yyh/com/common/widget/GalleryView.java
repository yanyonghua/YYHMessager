package www.yyh.com.common.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import www.yyh.com.common.R;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;


public class GalleryView extends RecyclerView {

    private static final int LOADER_ID =0x0100;

    private static final int MAX_IMAGE_COUNT=3;//最大选中图片size
    private static final int MIN_IMAGE_FILE_SIZE=10*1024;//最小的图片大小

    private List<Image> mSelectedImages= new LinkedList<>();
    private LoaderCallback mLoaderCallback =new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private SelectedChangeListener mListener;

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //Cell点击操作，如果我们的点击数允许的，那么更新对应Cell的状态
                //然后更新节目，同理；Toast不能允许点击（已经达到最大选中数量）那么就不刷新界面
                if (onItemSelectClick(image)){
                    holder.updateData(image);
                }
            }

        });
    }

    /**
     * 初始化方法
     * @param loaderManager Loader管理器
     * @return 热河一个Loader_ID,可用于销毁Loader

     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener){
        this.mListener=listener;
        loaderManager.initLoader(LOADER_ID,null,mLoaderCallback);
        return LOADER_ID ;
    }

    /**
     * 通知选中状态改变
     */
    private void  notifySelectChange(){
        //得到监听者，并判断是否邮件听着，然后进行回调数量变化
        SelectedChangeListener listener =mListener;
        if (listener!=null){
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter数据更新的方法
     * @param images 新的数据
     */
    private  void updataSource(List<Image> images){
        mAdapter.replace(images);
    }
    /**
     * 用于实际的数据加载Loader Callback
     */
    private class LoaderCallback implements LoaderCallbacks<Cursor>{
        private final  String[] IMAGE_PROJECTION =new String[]{
                MediaStore.Images.Media._ID,//Id
                MediaStore.Images.Media.DATA,//图片路径
                MediaStore.Images.Media.DATE_ADDED//创建时间
        };
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // 创建一个loader
            if (id==LOADER_ID){
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2]+" DESC");//倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            //当Loader加载完成时
            List<Image> images =new ArrayList<>();
            //判断是否有数据
            if (data!=null){
                int count =data.getCount();
                if (count>0){
                    data.moveToFirst();//移动游标到顶部
                    int indexId=data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexpath =data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate =data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        //循环读取，知道没有下一条数据
                        int id =data.getInt(indexId);
                        String path =data.getString(indexpath);
                        long date =data.getLong(indexDate);
                        File file = new File(path);
                        if (!file.exists()||file.length()<MIN_IMAGE_FILE_SIZE){
                            //如果没有图片或者图片太小直接跳过
                            continue;
                        }
                        Image image =new Image();
                        image.id=id;
                        image.path=path;
                        image.date=date;
                        images.add(image);
                    }while (data.moveToNext());
                }
            }
            updataSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
            // 当loader销毁或重置,进行界面清空操作
            updataSource(null);
        }
    }
    /**
     * Cell点击的具体逻辑
     * @param image Image
     * @return True ，代表我进行数据更改，你需要刷新；反之不刷新
     */
    @SuppressLint("StringFormatMatches")
    private boolean onItemSelectClick(Image image){

        boolean notifyRefresh;
        if (mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
            image.isSelect=false;
            //状态已经改变则需要更新
            notifyRefresh=true;
        }else
        {
            if (mSelectedImages.size()>=MAX_IMAGE_COUNT){
                //得到提示
                String str =getResources().getString(R.string.label_gallery_select_max_size);
                str=String.format(str,MAX_IMAGE_COUNT);
                //格式化填充
               Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();

                notifyRefresh=false;
            }else {
                mSelectedImages.add(image);
                image.isSelect=true;
                notifyRefresh=true;
            };
        }
        //如果数据有更改，
        // 那么我们需要通知外面的监听者我们的数据选中改变了
        if (notifyRefresh){
            notifySelectChange();
        }
        return true;
    }

    /**
     * 得到选中的图片的全部地址
     * @return 返回一个数组
     */
    public String[] getSelectedPath(){
        String[] paths= new String[mSelectedImages.size()];
        int index =0;
        for (Image image :mSelectedImages){
            paths[index++]=image.path;
        }
        return paths;
    }

    /**
     * 可以进行清空选中的图片
     */
    public void  clear(){
        for (Image image :mSelectedImages){
            // 一定要先重置状态
            image.isSelect=false;
        }
        mSelectedImages.clear();
        // 通知更新
        mAdapter.notifyDataSetChanged();
        //通知选中数量的改变
        notifySelectChange();
    }


    /**
     * 内部的数据结构
     */
    private static class Image{
        //数据的id
        int id;
        String path;//图片的路径
        long date;//图片的创建日期
        boolean isSelect;//是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Image image = (Image) o;
            return Objects.equals(path, image.path);
        }

        @Override
        public int hashCode() {

            return Objects.hash(path);
        }


    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected ViewHolder<Image> onCreataViewHolder(android.view.View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

        @Override
        protected int getItemViewType(int position, Image image) {

            return R.layout.cell_galley;
        }
    }

    /**
     * Cell 对应的Holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;
        public ViewHolder(View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_selet);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext()).load(image.path)//加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不适用，换成直接从原图加载
                    .centerCrop()//居中剪切
                    .placeholder(R.color.grey_200)//默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect?VISIBLE:INVISIBLE);
            mSelected.setVisibility(VISIBLE);
            mSelected.setChecked(image.isSelect);
        }
    }

    /**
     * 对外的监听器
     */
    public interface SelectedChangeListener{
        void  onSelectedCountChanged(int count);
    }
}
