package www.yyh.com.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.EditText;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import www.yyh.com.common.app.Application;
import www.yyh.com.common.app.PresenterToolbarActivity;
import www.yyh.com.common.widget.PortraitView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.factory.presenter.group.GroupCreateContract;
import www.yyh.com.factory.presenter.group.GroupCreatePresenter;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.media.GalleryFragment;

public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContract.Presenter>
implements GroupCreateContract.View{

    @BindView(R.id.mrecycler)
    RecyclerView mRecycle;

    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private String mPortraitPath;
    private Adapter mAdapter;
    public static void show(Context context){
        context.startActivity(new Intent(context,GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new Adapter();
        mRecycle.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @OnClick(R.id.im_portrait)
    void onPortaitClick(){
        hideSoftKeyboard();
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
                        .start(GroupCreateActivity.this);

            }
        })//show的使用建议使用getChildFragmentManager，
                //tag 就是GalleryFragment class名字
                .show(getSupportFragmentManager(),GalleryFragment.class.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.group_create,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_create){
            //进行创建群
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCreateClick() {
        hideSoftKeyboard();
        String name =mName.getText().toString().trim();
        String desc =mDesc.getText().toString().trim();
        mPresenter.create(name,desc,mPortraitPath);
    }

    private void hideSoftKeyboard(){
        View view =getCurrentFocus();
        if (view==null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public void onCreateSuccessed() {
        //提示成功
        hideDialogLoading();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel>getRecyclerAdapter () {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }

    private class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel>{

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreataViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }
    }

     class ViewHolder  extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel>{
        @BindView(R.id.im_portrait)
         PortraitView mPortrait;
        @BindView(R.id.txt_name)
         TextView mName;
        @BindView(R.id.cb_select)
        android.widget.CheckBox mSelect;

         public ViewHolder(View itemView) {
             super(itemView);
         }
         @OnCheckedChanged(R.id.cb_select)
         void onCheckedChanged(boolean checked){
             //进行状态更改
            mPresenter.changeSelect(mData,checked);
         }

         @Override
         protected void onBind(GroupCreateContract.ViewModel viewModel) {
             mPortrait.setup(Glide.with(GroupCreateActivity.this),viewModel.author);
             mName.setText(viewModel.author.getName());
             mSelect.setChecked(viewModel.isSelect);
         }
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

























}
