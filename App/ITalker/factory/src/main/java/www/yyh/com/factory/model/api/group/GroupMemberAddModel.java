package www.yyh.com.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 56357 on 2018/7/3
 */
public class GroupMemberAddModel {
    private Set<String> users =new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
    public static boolean check(GroupMemberAddModel model){
        return !(model.users==null
                ||model.users.size()==0);

    }
}
