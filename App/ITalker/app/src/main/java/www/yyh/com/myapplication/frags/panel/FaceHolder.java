package www.yyh.com.myapplication.frags.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import butterknife.BindView;
import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.face.Face;
import www.yyh.com.myapplication.R;

/**
 * Created by 56357 on 2018/7/9
 */
public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean>{
    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if (bean!=null&&(
                //drawable 资源 id
                (bean.preview instanceof  Integer)
                        //faces zip 包资源路径
                        ||bean.preview instanceof String)){

            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)//设置解码的格式8888、保证清晰度
                    .placeholder(R.drawable.default_face)
                    .into(mFace);
        }
    }











}
