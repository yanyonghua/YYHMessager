package www.yyh.com.factory.data.user;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import www.yyh.com.factory.data.BaseDbRepository;
import www.yyh.com.factory.data.DataSource;
import www.yyh.com.factory.model.db.User;
import www.yyh.com.factory.model.db.User_Table;
import www.yyh.com.factory.persistence.Account;

/**
 * 联系人仓库
 * Created by 56357 on 2018/6/23
 */
public class ContactRepository extends BaseDbRepository<User>
        implements ContactDataSource{

    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);

        //加载本地数据库数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))//是否是订阅用户
                .and(User_Table.id.notEq(Account.getUserId()))//通过非自己的id
                .orderBy(User_Table.name,true)//通过名字排序
                .limit(100)//限制100条数据
                .async()//异步操作
                .queryListResultCallback(this)
                .execute();
    }


    /**
     * 检查一个User是否是我需要关注的数据
     * @param user User
     * @return true 是我关注的数据
     */
    @Override
    protected boolean isRequired(User user){
        return user.isFollow()&&!user.getId().equals(Account.getUserId());
    }

}
