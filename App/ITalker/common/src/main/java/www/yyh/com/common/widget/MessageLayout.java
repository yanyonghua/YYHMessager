package www.yyh.com.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import net.qiujuer.widget.airpanel.AirPanelLinearLayout;

/**
 * Created by 56357 on 2018/6/27
 */
public class MessageLayout extends AirPanelLinearLayout {
    public MessageLayout(Context context) {
        super(context);
    }

    public MessageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            insets.left=0;
            insets.top=0;
            insets.right=0;
        }
        return super.fitSystemWindows(insets);


    }
}
