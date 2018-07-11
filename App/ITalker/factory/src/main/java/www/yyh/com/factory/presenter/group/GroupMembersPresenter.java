package www.yyh.com.factory.presenter.group;

import java.util.List;

import www.yyh.com.factory.Factory;
import www.yyh.com.factory.data.helper.GroupHelper;
import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.presenter.BaseRecyclerPresenter;

/**
 * Created by 56357 on 2018/7/9
 */
public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel,GroupMembersContract.View>
implements GroupMembersContract.Presenter{

    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        //显示loading
        start();
        Factory.runOnAsync(loader);
    }

    private Runnable loader = new Runnable() {

        @Override
        public void run() {
            GroupMembersContract.View view =getView();
            if(view==null)
                return;
             String groupId =view.getmGroupId();
            //传递数量为-1 代表查询所有
            List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId,-1);
            refreshData(models);
        }
    };
}
