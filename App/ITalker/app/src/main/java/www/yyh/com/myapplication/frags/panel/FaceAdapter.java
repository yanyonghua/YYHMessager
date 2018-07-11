package www.yyh.com.myapplication.frags.panel;

import android.view.View;

import java.util.List;

import www.yyh.com.common.widget.recycler.RecyclerAdapter;
import www.yyh.com.face.Face;
import www.yyh.com.myapplication.R;

/**
 * Created by 56357 on 2018/7/9
 */
public class FaceAdapter extends RecyclerAdapter<Face.Bean>{
    public FaceAdapter(List<Face.Bean> beans, AdapterListener adapterListener) {
        super(beans, adapterListener);
    }

    @Override
    protected ViewHolder<Face.Bean> onCreataViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }
}
