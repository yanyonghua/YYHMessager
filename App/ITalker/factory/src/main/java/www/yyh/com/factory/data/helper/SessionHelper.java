package www.yyh.com.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import www.yyh.com.factory.model.db.Session;
import www.yyh.com.factory.model.db.Session_Table;

/**
 * 会话辅助工具类
 * Created by 56357 on 2018/6/22
 */
public class SessionHelper {
    //从本地查询Session
    public static Session findFromLocal(String id) {
        return  SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
