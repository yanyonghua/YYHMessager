package www.yyh.com.myapplication.frags.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.Application;
import www.yyh.com.common.app.PresenterFragment;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.factory.presenter.user.UpdateInfoContract;
import www.yyh.com.factory.presenter.user.UpdateInfoPresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.MainActivity;
import www.yyh.com.myapplication.frags.media.GalleryFragment;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
        implements  UpdateInfoContract.View {
    private final String TAG = UpdateInfoFragment.class.getName();
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    //头像的本地路径
    private String mPortraitPath;
    private boolean isMan=true;
    public UpdateInfoFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayout() {
        return R.layout.fragment_update_info;
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);

    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        new GalleryFragment().setmOnSelectedListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelecredImage(String path) {
                UCrop.Options options =new UCrop.Options();
                //设置图片处理的格式JPEG
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                //设置压缩后的图片精度
                options.setCompressionQuality(96);

                //得到头像的缓存地址
                File dPath = Application.getPortraitTmpFile();

                //发起剪切
                UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(dPath))
                        .withAspectRatio(1,1)//一比一
                        .withMaxResultSize(520,520)//返回最大的尺寸
                        .withOptions(options)//相关参数
                        .start(getActivity());

            }
        })//show的使用建议使用getChildFragmentManager，
                //tag 就是GalleryFragment class名字
                .show(getChildFragmentManager(),GalleryFragment.class.getName());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       //收到从Activity 传过来的回调，然后取出其中的值进行图片加载
        //吐过是我能够处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //UCrop得到对应的uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri!=null){
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
           Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    /**
     * 上传头像
     * @param uri
     */
    private void loadPortrait(Uri uri){
        //得到头像地址
        mPortraitPath=uri.getPath();
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        //当需要显示错误的时候触发，一定是结束了
        mLoading.stop();
        //让控件可以输入
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        //提交按钮可以继续点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //正在进行时，正在进行注册，界面不可操作
        mLoading.start();
        //让控件可以输入
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        //提交按钮可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }

    @Override
    public void updateSucceed() {
        MainActivity.show(getContext());
        getActivity().finish();
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String desc =mDesc.getText().toString();
        mPresenter.update(mPortraitPath,desc,isMan);
    }
    @OnClick(R.id.im_sex)
    void onSexClick(){
       //性别图片点击的时候触发
        isMan=!isMan;
        Drawable drawable =getResources().getDrawable(isMan?
                R.drawable.ic_sex_man:R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        //设置背景的层级切换背景
        mSex.getBackground().setLevel(isMan?0:1);
    }
}
