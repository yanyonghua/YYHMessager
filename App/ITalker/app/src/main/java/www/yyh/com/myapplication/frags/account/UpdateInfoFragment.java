package www.yyh.com.myapplication.frags.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import www.yyh.com.common.app.Application;
import www.yyh.com.common.app.Fragment;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.factory.Factory;
import www.yyh.com.factory.net.UploadHelper;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.media.GalleryFragment;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息界面
 */
public class UpdateInfoFragment extends Fragment {
    private final String TAG = UpdateInfoFragment.class.getName();
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

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
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void loadPortrait(Uri uri){
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);

        //拿到本地文件的地址
        final String localPath = uri.getPath();
        Log.e(TAG, "loadPortrait: "+localPath );

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
              String url=  UploadHelper.uploadPortrait(localPath);
                Log.e(TAG, "run: "+url );
            }
        });
    }
}
