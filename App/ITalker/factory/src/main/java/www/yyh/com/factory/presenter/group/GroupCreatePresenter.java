package www.yyh.com.factory.presenter.group;

import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import www.yyh.com.factory.Factory;
import www.yyh.com.factory.R;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.data.helper.GroupHelper;
import www.yyh.com.factory.data.helper.UserHelper;
import www.yyh.com.factory.model.api.group.GroupCreateModel;
import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.model.db.view.UserSampleModel;
import www.yyh.com.factory.net.UploadHelper;
import www.yyh.com.factory.presenter.BaseRecyclerPresenter;

/**
 * 群创建界面的Presenter
 * Created by 56357 on 2018/7/1
 */
public class GroupCreatePresenter extends
        BaseRecyclerPresenter<GroupCreateContract.ViewModel,GroupCreateContract.View>
        implements GroupCreateContract.Presenter ,DataSource.Callback<GroupCard>{

    Set<String> users =new HashSet<>();
    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        //加载
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(final String name, final String desc, final String picture) {
            GroupCreateContract.View view =getView();
            view.showLoading();
            //判断参数
            if (TextUtils.isEmpty(name)||TextUtils.isEmpty(desc)||
                    TextUtils.isEmpty(picture)||users.size()==0){
                view.showError(R.string.label_group_create_invalid);
                return;
            }

            //上传图片
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                  String  url= uploadPicture(picture);
                  if (TextUtils.isEmpty(url))
                      return;
                  //进行网络请求
                    GroupCreateModel model=new GroupCreateModel(name,desc,url,users);
                    GroupHelper.create(model,GroupCreatePresenter.this);
                }
            });

    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelect) {
        if (isSelect){
            users.add(model.author.getId());
        }else
            users.remove(model.author.getId());
    }

    //同步上传操作
    private String uploadPicture(String path){
        String url = UploadHelper.uploadPortrait(path);
        if (TextUtils.isEmpty(url)){
            //切换到UI线程，提示信息
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                  GroupCreateContract.View view=  getView();
                  if (view!=null){
                      view.showError(R.string.data_upload_error);
                  }
                }
            });
        }
        return url;
    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> contact = UserHelper.getSampleContact();
            List<GroupCreateContract.ViewModel> models = new ArrayList<>();
            for (UserSampleModel model : contact) {
                GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
                viewModel.author = model;
                models.add(viewModel);
            }
            refreshData(models);
        }
    };


    @Override
    public void onDataLoaded(GroupCard groupCard) {
        // 成功
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view=  getView();
                if (view!=null){
                    view.onCreateSuccessed();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        //失败的情况下
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view=  getView();
                if (view!=null){
                    view.showError(strRes);
                }
            }
        });
    }
}