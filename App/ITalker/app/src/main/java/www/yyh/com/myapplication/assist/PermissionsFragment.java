package www.yyh.com.myapplication.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import www.yyh.com.common.app.Application;
import www.yyh.com.factory.persistence.Account;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.AccountActivity;
import www.yyh.com.myapplication.activities.MainActivity;
import www.yyh.com.myapplication.frags.media.GalleryFragment;

/**
 * 权限申请弹出框
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks{

    //权限回调的表识
    private static final int RC =0x0100;

    public PermissionsFragment() {
        // Required empty public constructor
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //返回一个我们复写的
        return new GalleryFragment.TransStatusBottomDialog(getContext());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_permissions, container, false);

        root.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //找到按钮点击时进行申请权限
                requestPerm();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //界面显示的时候进行刷新
        refreshState(getView());
    }

    /**
     * 刷新我们的布局中的图片的状态
     * @param root
     */
    private void refreshState(View root){
        if (root==null){
            return;
        }
        Context context =getContext();
        root.findViewById(R.id.im_state_permission_network).
                setVisibility(haveNetwork(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_read).
                setVisibility(haveReadPerm(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_write).
                setVisibility(haveWritePerm(context)?View.VISIBLE:View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio).
                setVisibility(haveRecordAudioPerm(context)?View.VISIBLE:View.GONE);
    }

    /**
     * 获取是否有网络权限
     * @param context 上下文
     * @return True则有
     */
    private static boolean haveNetwork(Context context){
        //准备需要检查的权限
        String[] perms= new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
        };
        return EasyPermissions.hasPermissions(context,perms);
    }
    /**
     * 获取是否有外部存储读取权限
     * @param context 上下文
     * @return True则有
     */
    private static boolean haveReadPerm(Context context){
        //准备需要检查的读取权限
        String[] perms= new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        return EasyPermissions.hasPermissions(context,perms);
    }
    /**
     * 获取是否有外部存储写入权限
     * @param context 上下文
     * @return True则有
     */
    private static boolean haveWritePerm(Context context){
        //准备需要检查的写入权限
        String[] perms= new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context,perms);
    }
    /**
     * 获取是否有录音权限
     * @param context 上下文
     * @return True则有
     */
    private static boolean haveRecordAudioPerm(Context context){
        //准备需要检查的权限
        String[] perms= new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context,perms);
    }

    //私有的show方法
    private static void show(FragmentManager manager){
        //调用BottomSheetDialogFragment已经准备好的显示方法
        new PermissionsFragment()
                .show(manager,PermissionsFragment.class.getName());

    }

    /**
     * 检查是否具有所有的权限
     * @param context
     * @param fragmentManager
     * @return 是否有权限
     */
    public static boolean haveAll(Context context,FragmentManager fragmentManager){
        //检查是否具有所有的权限
        boolean haveAll =haveNetwork(context)
                &&haveReadPerm(context)
                &&haveWritePerm(context)
                &&haveRecordAudioPerm(context);
        //如果有则显示当前申请权限的界面
        if (!haveAll){
            show(fragmentManager);
        }
        return haveAll;
    }
    /**
     * 申请权限的方法
     */
    @AfterPermissionGranted(RC)
    public void requestPerm(){
        //准备需要检查的权限
        String[] perms= new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        if(EasyPermissions.hasPermissions(getContext(),perms)){
            Application.showToast(R.string.label_permission_ok);
            //Fragement中调用getView得到根布局，前提是在onCreateview调用之后
            refreshState(getView());
            if (Account.isLogin()){
                MainActivity.show(getContext());
            }  else  AccountActivity.show(getContext());
            getActivity(). finish();
        }else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.title_assist_permissions),RC,perms);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
       //如果权限没有申请成功的权限存在，则弹出弹出框，用户点击后渠道设置界面自己打开权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * 权限申请的时候回调的方法，在这个方法中把对应的权限申请状态交给EasyPermissions框架
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //传递对应的参数，并且告知接受权限的处理者是我自己
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}
