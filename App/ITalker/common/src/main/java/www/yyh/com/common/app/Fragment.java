package www.yyh.com.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import www.yyh.com.common.widget.convention.PlaceHolderView;

/**
 * Created by 56357 on 2018/5/24
 */
public abstract class Fragment extends android.support.v4.app.Fragment {
    protected PlaceHolderView mPlaceHolderView;
    protected View mRoot;
    protected Unbinder mRootUnbinder;
    private static final String TAG ="Fragment";
    //是否是第一次初始化數據
    protected boolean mIsFirstInitData=true;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //初始参数
        initArgs(getArguments());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: " );
        if (mRoot == null) {
            int layId = getContentLayout();
            //初始化当前的根布局，但是不创建时就添加到container里边
            View root = inflater.inflate(layId, container, false);
            initWidget(root);
            mRoot=root;
        } else {
            if (mRoot.getParent() != null) {
                //把当前root从其父控件中移除
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }
        return mRoot;
    }



    /**
     * 初始化相关参数
     *
     * @param bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected void initArgs(Bundle bundle) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData){
            //第一次才会出发
            mIsFirstInitData=false;
            //触发
            onFirstInit();
        }
        //当View创建完成后初始化数据
        initData();
    }

    /**
     * 得到当前界面的资源文件id
     *
     * @return 资源文件ID
     */
    protected abstract int getContentLayout();

    /*
     * 初始化控件
     * */
    protected void initWidget(View view) {
        mRootUnbinder = ButterKnife.bind(this, view);
    }

    /*
     * 初始化控件
     * */
    protected void initData() {

    }

    /**
     * 首次初始化
     */
    protected void onFirstInit() {

    }

    /**
     * 返回按键触发调用
     *
     * @return 返回True 代表我已经处理返回逻辑，Activity不用Finish
     * 返回False代表我没有处理逻辑,Activity自己处理自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 设置占位布局
     * @param placeHolderView
     */
    public void  setPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView=placeHolderView;
    }

}
