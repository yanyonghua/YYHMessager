package www.yyh.com.common.tools;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;



/**
 * Created by 56357 on 2018/5/30
 */
public class UITool {
    private static int STATUS_BAR_HEIGHT =-1;

    /**
     * 得到我们的状态栏的高度
     * @param activity Activity
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT&&STATUS_BAR_HEIGHT==-1){
            try {

                final Resources res =activity.getResources();
                //尝试获取status_bar_height这个属性的Id对应的资源int值
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId<=0){
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object o = clazz.newInstance();
                    resourceId =Integer.parseInt(clazz.getField("status_bar_height")
                            .get(o).toString());
                }
                //如果拿到了就直接调用获取值
                if (resourceId>0){
                    STATUS_BAR_HEIGHT =res.getDimensionPixelSize(resourceId);
                }
                //如果还是未拿到
                if (STATUS_BAR_HEIGHT<=0){
                    Rect rect = new Rect();
                    Window window =activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rect);
                   STATUS_BAR_HEIGHT= rect.top;
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    public static int getScreenheight(Activity activity){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }















}
