package net.qiujuer.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 *用户关系的Model
 * 用于用户直接进行号有关系的实现
 *
 */
@Entity
@Table(name="TB_USER_FOLLOW")
public class UserFollow {

    //这个一个主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2，uuid是常规的UUID toString
    @GenericGenerator(name ="uuid",strategy = "uuid2")
    //不予许更改，不予许为空
    @Column(updatable = false,nullable = false)
    private String id;

    //定义一个发起人，你关注某人，这里就是你
    //多对一 ->你可以关注很多人，你的每一次关注
    //你可以创建很多歌关注的信息，所以是多对一，
    //这里的多对一是：User对应多个UserFollow
    //optional 不可选，必须存储，一条关注记录一定要有一个“你”
    @ManyToOne(optional =  false)
    //定义关联得标字段名为originId，对应的是User.id
    //定义的是数据库中的存储字段
    @JoinColumn(name = "originId")
    private  User origin;
    //把这个列提取到我们的Model中，不允许为空不允许更新，插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String originId;


    //定义关注的目标，你关注的人
    //也是多对一，你可以被很多人关注，每次一关注就是一条记录
    //所有就是 多个UserFollow 对应一个 User的情况
    @ManyToOne(optional =  false)
    //定义关联得标字段名为targetId，对应的是User.id
    @JoinColumn(name = "targetId")
    private User target;
    //把这个列提取到我们的Model中，不允许为空不允许更新，插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String targetId;

    //别名也就是对target的备注，可以为null
    private String alias ;

    //定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt =LocalDateTime.now();

    //定义为更新时间戳，在创建的时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt =LocalDateTime.now();





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }


}
