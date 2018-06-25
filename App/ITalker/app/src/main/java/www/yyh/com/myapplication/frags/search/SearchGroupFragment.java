package www.yyh.com.myapplication.frags.search;


import www.yyh.com.common.app.Fragment;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.activities.SearchActivity;

/**
 * 搜索群的实现
 */
public class SearchGroupFragment extends Fragment implements SearchActivity.SearchFragment{


    public SearchGroupFragment() {
        // Required empty public constructor
    }




    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }
}
