package net.qiujuer.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息的Model，对应数据库
 */
@Entity
@Table(name="TB_MESSAGE")
public class Message {
    public static final int TYPE_STR=1;//字符串类型
    public static final int TYPE_PIC=2;//图片类型
    public static final int TYPE_FILE=3;//文件类型
    public static final int TYPE_AUDIO=4;//语音类型

    //这个一个主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型为UUID
    //这里不自动生产Id由代码写入，有客户端负责生成
    //避免复杂服务器和客户端的映射关系
    //@GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2，uuid是常规的UUID toString
    @GenericGenerator(name ="uuid",strategy = "uuid2")
    //不予许更改，不予许为空
    @Column(updatable = false,nullable = false)
    private String id;

    //内容不允许为空，类型为text
    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    @Column
    private String attach;

    @Column(nullable = false)
    private int type;

    //发送者
    //多个消息对应一个发送者
    @JoinColumn(name = "senderId")
    //Many代表当前类，One代表用户
    @ManyToOne(optional = false)
    private User sender;
    //这个字段仅仅只是对应sender的数据库字段senderId
    //不允许手动的更新或者插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String senderId;

    //接收者可为空
    //多个消息对应一个接受者
    @ManyToOne
    @JoinColumn(name = "receiverId")
    private User receiver;
    @Column(updatable = false,insertable = false)
    private String receiverId;

    //一个群可以接受多个消息
    @ManyToOne
    @JoinColumn(name = "groupId")
    private User group;
    @Column(updatable = false,insertable = false)
    private String groupId;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public User getGroup() {
        return group;
    }

    public void setGroup(User group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}