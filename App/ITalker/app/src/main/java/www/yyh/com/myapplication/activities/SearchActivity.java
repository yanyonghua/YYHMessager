package www.yyh.com.myapplication.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import www.yyh.com.common.app.Fragment;
import www.yyh.com.common.app.ToolbarActivity;
import www.yyh.com.myapplication.R;
import www.yyh.com.myapplication.frags.search.SearchGroupFragment;
import www.yyh.com.myapplication.frags.search.SearchUserFragment;

public class SearchActivity extends ToolbarActivity {

    public static final  String EXTRA_TYPE ="EXTRA_TYPE";//传递参数
    public static final  int TYPE_USER=1;//搜索人
    public static final  int TYPE_GROUP=2;//搜索群

    private int type;

    private SearchFragment mSearchFragment;

    public static void show(Context context,int type){
        Intent  intent=new Intent(context,SearchActivity.class);
        intent.putExtra(EXTRA_TYPE,type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type =bundle.getInt(EXTRA_TYPE);
        //是搜索人或者搜索群
        return type==TYPE_USER||type==TYPE_GROUP;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //显示对应的Fragment
        Fragment fragment;
        if (type==TYPE_USER){
            SearchUserFragment searchUserFragment = new SearchUserFragment();
            fragment=searchUserFragment;
            mSearchFragment=searchUserFragment;
        }else {
            SearchGroupFragment searchGroupFragment =new SearchGroupFragment();
            fragment=searchGroupFragment;
            mSearchFragment=searchGroupFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container,fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.search,menu);
        final MenuItem search =menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        if (searchView!=null){
            //拿到一個搜索管理器
            SearchManager searchManager =(SearchManager)getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //当点击了提交按钮的时候
                    search(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //当字体改变的时候，咱们不会及时搜索，只在为null的情况下进行搜索
                    if (TextUtils.isEmpty(newText)){
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);

    }



    /**
     * 搜索的发起点
     * @param query 搜索的文字
     */
    private void search(String query) {
        if (mSearchFragment!=null){
            mSearchFragment.search(query);
        }
    }

    public interface SearchFragment{
        void search(String content);
    }
}
