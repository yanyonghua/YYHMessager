package www.yyh.com.common.app;


import android.app.ProgressDialog;
import android.content.DialogInterface;

import www.yyh.com.common.R;
import www.yyh.com.factory.presenter.BaseContract;

/**
 * Created by 56357 on 2018/6/11
 */
public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter>{
    protected Presenter mPresenter;

    protected ProgressDialog mLoadingDialog;


    /**
     * 初始化Presenter
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    protected void initBefore() {
        super.initBefore();
        //初始化Presenter
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity关闭的时候，进行销毁
        if (mPresenter!=null){
            mPresenter.destory();
        }
    }

    @Override
    public void showError(int str) {
        //不管你怎么样，我先隐藏我
        hideDialogLoading();
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerError(str);
        }else {
            //显示错误
            Application.showToast(str);
        }
    }

    protected void hideDialogLoading(){
        ProgressDialog dialog= mLoadingDialog;
        if (dialog!=null){
            mLoadingDialog=null;
            dialog.dismiss();
        }
    }
    @Override
    public void showLoading() {
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }else {
          ProgressDialog dialog=  mLoadingDialog;
          if (dialog==null){

              dialog=new ProgressDialog(this,R.style.APPTheme_Dialog_Alert_Light);
              // 不可触摸取消
              dialog.setCanceledOnTouchOutside(false);
              //强制取消关闭界面
              dialog.setCancelable(true);
              dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                  @Override
                  public void onCancel(DialogInterface dialog) {
                      finish();
                  }
              });
              mLoadingDialog=  dialog;
          }
            dialog.setMessage(getText(R.string.prompt_loading));
          dialog.show();
        }
    }

    public void hideLoading() {
        //不管你怎么样，我先隐藏我
        hideDialogLoading();
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerOk();
        }
    }



    @Override
    public void setPresenter(Presenter presenter) {
        //View中赋值Presenter
        this.mPresenter = presenter;
    }
}
