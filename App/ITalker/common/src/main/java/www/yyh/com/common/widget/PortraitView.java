package www.yyh.com.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;

import de.hdodenhof.circleimageview.CircleImageView;
import www.yyh.com.common.R;
import www.yyh.com.factory.model.Author;

/**
 * Created by 56357 on 2018/5/26
 */
public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setup(RequestManager manager,Author author){
        if (author==null)
            return;
        //进行显示
        setup(manager,author.getPortrait());
    }
    public void setup(RequestManager manager,String url){
        setup(manager, R.drawable.default_portrait,url);
    }

    public void setup(RequestManager manager,int resourceId,String url){
        if (url==null){
            url="";
        }
        manager.load(url)
                .placeholder(resourceId)
                .centerCrop()
                .dontAnimate()
                .into(this);
    }
}
