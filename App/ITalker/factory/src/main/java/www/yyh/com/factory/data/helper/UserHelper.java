package www.yyh.com.factory.data.helper;

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
import www.yyh.com.factory.model.api.user.UserUpdateModel;
import www.yyh.com.factory.model.card.UserCard;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.model.db.User_Table;
import www.yyh.com.factory.net.Network;
import www.yyh.com.factory.net.RemoteService;

/**
 *
 * Created by 56357 on 2018/6/8
 */
public class UserHelper {
    //更新用户信息操作，异步的
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> cardCallback){
        RemoteService service = Network.remote();
        //得到一个call
        Call<RspModel<UserCard>> rspModelCall = service.userUpdate(model);

        rspModelCall.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()){
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);

                    //操作成功
                    cardCallback.onDataLoaded(userCard);
                }else {
                    //错误情况下进行错误分配
                    Factory.decodeRspCode(rspModel,cardCallback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                if (cardCallback!=null){
                    cardCallback.onDataNotAvailable(R.string.data_network_error);
                }
            }
        });
    }

    //更新用户信息操作，异步的
    public static Call search(String name, final DataSource.Callback<List<UserCard>> cardCallback){
        RemoteService service = Network.remote();
        //得到一个call
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);

        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> body = response.body();
                if (body.success()){
                    //返回数据
                    cardCallback.onDataLoaded(body.getResult());
                }else {
                    Factory.decodeRspCode(body,cardCallback);
                }
            }
            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                cardCallback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //把当前的调度者返回
        return call;
    }

    /**
     * 关注的网络请求
     * @param id
     * @param callback
     */
    public static void follow(String id, final DataSource.Callback callback) {
        RemoteService service = Network.remote();
        //得到一个call
        final Call<RspModel<UserCard>> call = service.userFollow(id);

        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> body = response.body();
                if (body.success()){
                    UserCard userCard = body.getResult();
                    Factory.getUserCenter().dispatch(userCard);
                    //返回数据
                    callback.onDataLoaded(body.getResult());
                }else {
                    Factory.decodeRspCode(body,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    //更新用户信息操作，异步的，不需要Callback，直接存储到数据库，
    // 并通过数据库观察者通知界面
    // 界面更新的时候，然后差异更新
    public static void refreshContacts( ){
        RemoteService service = Network.remote();
        service.userContacts().enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> body = response.body();
                if (body.success()){
                    List<UserCard> cards =body.getResult();
                    if (cards==null||cards.size()==0){
                        return;
                    }
                    //两种方法
                    UserCard[] userCards = cards.toArray(new UserCard[0]);
//                    UserCard[] userCards = CollectionUtil.toArray(cards,UserCard.class);
                    Factory.getUserCenter().dispatch(userCards);
                }else {
                    Factory.decodeRspCode(body,null);
                }
            }
            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
               //nothing
            }
        });
    }

    public static User findFromLocal(String id){
      return   SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 从网络查询某用户的信息
     * @param id
     * @return
     */
    public static User findFromNet(String id){
        RemoteService remote = Network.remote();
        try {
          Response<RspModel<UserCard>> response= remote.userFind(id).execute();
            UserCard userCard = response.body().getResult();
            if (userCard!=null){
                User user = userCard.build();
                //数据库的存储并通知
                Factory.getUserCenter().dispatch(userCard);
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return   SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }
    /**
     * 搜索一个用户，优先本地缓存，
     * 没有用户然后再从网络拉取
     * @param id
     * @return
     */
    public static User search(String id){
        User user=findFromLocal(id);
        if (user==null){
            return findFromNet(id);
        }
        return user;
    }
    /**
     * 搜索一个用户，优先网络查询，
     * 没有用户然后再从本地缓存拉取
     * @param id
     * @return
     */
    public static User searchFirstOfNet(String id){
        User user=findFromNet(id);
        if (user==null){
            return findFromLocal(id);
        }
        return user;
    }
}
