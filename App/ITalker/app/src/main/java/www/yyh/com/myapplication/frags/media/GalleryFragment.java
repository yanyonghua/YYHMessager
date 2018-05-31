package www.yyh.com.myapplication.frags.media;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import www.yyh.com.common.tools.UITool;
import www.yyh.com.common.widget.GalleryView;
import www.yyh.com.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends BottomSheetDialogFragment
implements GalleryView.SelectedChangeListener{
    private OnSelectedListener mOnSelectedListener;
    private GalleryView mGallery;
    private String TAG=GalleryFragment.class.getName();

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * 设置监听，并返回自己
     * @param mOnSelectedListener
     * @return GalleryFragment
     */
    public GalleryFragment setmOnSelectedListener(OnSelectedListener mOnSelectedListener){
        this.mOnSelectedListener = mOnSelectedListener;
        return this;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //先使用默认的
        return new TransStatusBottomDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = root.findViewById(R.id.galleryView);
        Log.e(TAG, "onCreateView: " );
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(),this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        //如果选中一张图片
        if (count>0){
            //隐藏自己
            dismiss();
            if (mOnSelectedListener !=null){
                //得到所有选中的图片路径
                String[] paths =mGallery.getSelectedPath();
                //返回第一张
                mOnSelectedListener.onSelecredImage(paths[0]);
                //取消和唤起这之间的应用，加快内存回收
                mOnSelectedListener =null;
            }
        }
    }

    /**
     * x选中图片的监听器
     */
    public interface  OnSelectedListener{
        void onSelecredImage(String path);
    }

    /**
     * 为了解决顶部状态栏变黑而写的TransStatusBottomDialog
     */
    public static class TransStatusBottomDialog extends  BottomSheetDialog{

        public TransStatusBottomDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomDialog(@NonNull Context context, int theme) {
            super(context, theme);

        }

        protected TransStatusBottomDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window =getWindow();

            if (window==null){
                return;
            }

            //得到屏幕的高度
            int screenHeight = UITool.getScreenheight(getOwnerActivity());

            //得到状态栏的高度
            int statusHeight = UITool.getStatusBarHeight(getOwnerActivity());

            //极端dialog的高度比设置
            int dialogHeight =screenHeight-statusHeight;

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,dialogHeight<=0?ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight);

        }
    }

























}
