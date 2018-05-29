package www.yyh.com.myapplication.frags.main;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import butterknife.BindView;
import www.yyh.com.common.app.Fragment;
import www.yyh.com.common.widget.GalleyView;
import www.yyh.com.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends Fragment {
    @BindView(R.id.galleyView)
    GalleyView mGalleyView;

    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayout() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        verifyStoragePermissions(getActivity());
    }

    @Override
    protected void initData() {
        super.initData();
        mGalleyView.setup(getLoaderManager(), new GalleyView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
    //    还要在JAVA代码中运行时实时请求权限：
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
