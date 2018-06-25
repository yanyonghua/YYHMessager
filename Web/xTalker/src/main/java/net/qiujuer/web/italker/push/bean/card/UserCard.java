package net.qiujuer.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import net.qiujuer.web.italker.push.bean.db.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDateTime;

/**
 * 用户信息卡片
 */
public class UserCard {


    @Expose
    private String id;

    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String portrait;
    @Expose
    private String description;
    @Expose
    private int sex =0;



    @Expose
    //用户关注的人的数量，
    private int follows;
    @Expose
    //用户关注的人的数量，
    private int following;
    @Expose
    //我与当前User的关系状态，是否关系了这个人
    private boolean isFollow;
    @Expose
    //用户信息最后的更新时间
    private LocalDateTime modifyAt ;

    public UserCard(final User user){
        this(user,false);
    }
    public UserCard(final User user ,boolean isFollow){
        this.isFollow =isFollow;
        this.id=user.getId();
        this.name=user.getName();
        this.phone=user.getPhone();
        this.portrait=user.getPortrait();
        this.description=user.getDescription();
        this.sex=user.getSex();
        this.modifyAt=user.getUpdateAt();

        // TODO 得到关注人和粉丝的数量
        //user.getFollows().size()因为懒加载会报错
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }
}
