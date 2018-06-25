package www.yyh.com.myapplication.helper;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.support.v4.app.Fragment;
/**
 * 解决对Fragment的调度与重用问题
 * 达到最优的Fragment切换
 * Created by 56357 on 2018/5/26
 */
public class NavHelper<T> {
    //所有Tab的集合
    private final SparseArray<Tab<T>> tabs= new SparseArray<>();
    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    private OnTabChangedListener<T> onTabChangedListener;
    //当前的一个选中的Tab
    private Tab<T> currentTab;
    /**
     * 构造函数
     * @param context 上下文
     * @param containerId
     * @param fragmentManager
     * @param onTabChangedListener
     */
    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, OnTabChangedListener<T> onTabChangedListener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.onTabChangedListener = onTabChangedListener;
    }

    /**
     * 添加Tab
     * @param menuId Tab对应的菜单Id
     * @param tab Tab
     */
    public NavHelper<T> add(int menuId,Tab<T> tab){
        tabs.put(menuId,tab);
        return this;
    }

    public Tab<T> getCurrentTab(){
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     * @param menuId 菜单的id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId){
        //集合中寻找点击的菜单对应的Tab
        //如果有则进行处理
        Tab<T> tab =tabs.get(menuId);
        if (tab!=null){
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实的Tab选择操作
     * @param tab Tab
     */
    private void doSelect(Tab<T> tab){
       Tab<T> oldTab =null;
       if (currentTab!=null){
          oldTab= currentTab;
          if (oldTab==tab){
              //如果说当前的Tab就是点击的Tab
              //那么我们不作处理
              notifyTabReselect(tab);
              return;
          }
       }
       currentTab =tab;
        doTabChanged(currentTab,oldTab);
    }

    /**
     * 进行Fragment的正式调度操作
     * @param newTab
     * @param oldTab
     */
    private void doTabChanged(Tab<T> newTab ,Tab<T> oldTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (oldTab!=null){
            if (oldTab.fragment!=null){
                //从界面移除，但是还在Fragment的缓存空间中
                ft.detach(oldTab.fragment);
            }
        }
        if (newTab!=null){
            if (newTab.fragment==null){
                //首次新建
                Fragment fragment = Fragment.instantiate(context, newTab.clx.getName(), null);
                //缓存起来
                newTab.fragment=fragment;
                //提交到FtagmentManager
                ft.add(containerId,fragment,newTab.clx.getName());

            }else {
                //从FragmentManger的缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        //提交事务
        ft.commit();
        notifyTabselect(newTab,oldTab);
    }

    /**
     * 回调我们的监听器
     * @param newTab 新的Tab<T>
     * @param oldTab 旧的Tab<T>
     */
    private void notifyTabselect(Tab<T> newTab,Tab<T> oldTab){
        if (onTabChangedListener!=null){
            onTabChangedListener.onTabChanged(newTab,oldTab);
        }
    }
    private void notifyTabReselect(Tab<T> tab){
        // TODO 二次点击Tab所做的操作
    }

    /**
     * 我们的所有的Tab基础属性
     * @param <T> 泛型的额外参数
     */
    public static class Tab<T>{
        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }
        //Fragmet对应的Class信息
        public Class<?> clx;
        //额外的字段，用户自己设定需要使用
        public T extra;
        //内部缓存的对应的Fragment
        //Private 私有的 外部无法使用
        private Fragment fragment;
    }

    /**
     * 定义事件处理完成后的接口
     * @param <T>
     */
    public interface OnTabChangedListener<T>{
        void onTabChanged(Tab<T> newTab,Tab<T> oldTab);
    }
}
