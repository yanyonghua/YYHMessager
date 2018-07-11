package www.yyh.com.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.yyh.com.factory.Factory;
import www.yyh.com.factory.R;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.model.api.RspModel;
import www.yyh.com.factory.model.api.group.GroupCreateModel;
import www.yyh.com.factory.model.card.GroupCard;
import www.yyh.com.factory.model.card.GroupMemberCard;
import www.yyh.com.factory.model.db.Group;
import www.yyh.com.factory.model.db.GroupMember;
import www.yyh.com.factory.model.db.GroupMember_Table;
import www.yyh.com.factory.model.db.Group_Table;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.model.db.User_Table;
import www.yyh.com.factory.model.db.view.MemberUserModel;
import www.yyh.com.factory.net.Network;
import www.yyh.com.factory.net.RemoteService;

/**
 * 对群的一个简单的辅助工具类
 * Created by 56357 on 2018/6/22
 */
public class GroupHelper {


    public static Group find(String groupId) {
        Group group=    findFromLocal(groupId);
        if (group==null){
            group=findForNet(groupId);
        }
        return group;
    }

    //从本地找群
    public static Group findFromLocal(String groupId) {
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    //从网络找群
    public static Group findForNet(String id){
        RemoteService remote = Network.remote();
        try {
            Response<RspModel<GroupCard>> response= remote.groupFind(id).execute();
            GroupCard groupCard = response.body().getResult();
            if (groupCard!=null){
                User user =UserHelper.searchFirstOfLocal(groupCard.getOwnerId());
                if (user!=null){
                    return groupCard.build(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //群的创建
    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        service .createGroup(model)
        .enqueue(new Callback<RspModel<GroupCard>>() {
            @Override
            public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                RspModel<GroupCard> rspModel = response.body();
                if (rspModel.success()){
                    GroupCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getGroupCenter().dispatch(userCard);

                    //操作成功
                    callback.onDataLoaded(userCard);
                }else {
                    //错误情况下进行错误分配
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                if (callback!=null){
                    callback.onDataNotAvailable(R.string.data_network_error);
                }
            }
        });
    }

    //搜索的方法
    public static Call search(String content,  final DataSource.Callback<List<GroupCard>> cardCallback) {
        RemoteService service = Network.remote();
        //得到一个call
        Call<RspModel<List<GroupCard>>> call = service.groupSearch(content);

        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> body = response.body();
                if (body.success()){
                    //返回数据
                    cardCallback.onDataLoaded(body.getResult());
                }else {
                    Factory.decodeRspCode(body,cardCallback);
                }
            }
            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                cardCallback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //把当前的调度者返回
        return call;
    }

    //刷新我的群组列表
    public static void refreshGroups() {
        RemoteService service =Network.remote();
        service.groups("").enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> body = response.body();
                if (body.success()){
                    //返回数据
                    List<GroupCard> groupCards =body.getResult();
                    if (groupCards!=null&&groupCards.size()>0){
                        //进行调度显示
                        Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupCard[0]));
                    }
                }else {
                    Factory.decodeRspCode(body,null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
//               不做任何事情
            }
        });
    }


    //获取群成员的数量
    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()//只查询数量
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    //从网络去刷新一个群的成员信息
    public static void refreshGroupMembers(Group group) {
        RemoteService service =Network.remote();
        service.groupMember(group.getId()).enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> body = response.body();
                if (body.success()){
                    //返回数据
                    List<GroupMemberCard> groupCards =body.getResult();
                    if (groupCards!=null&&groupCards.size()>0){
                        //进行调度显示
                        Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupMemberCard[0]));
                    }
                }else {
                    Factory.decodeRspCode(body,null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
//               不做任何事情
            }
        });
    }

    // 重点  关联查询一个用户和群成员的表，返回一个MemberUserModel表的集合
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {

        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),//需要查询的列
                User_Table.id.withTable().as("userId"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)//从GroupMember
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(groupId))
                .orderBy(GroupMember_Table.user_id,true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);

    }




}
