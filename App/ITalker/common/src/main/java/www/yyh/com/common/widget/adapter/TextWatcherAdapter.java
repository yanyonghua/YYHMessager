package www.yyh.com.common.widget.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by 56357 on 2018/6/27
 */
public abstract class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
