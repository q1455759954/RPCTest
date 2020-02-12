package server.entity;

import java.io.Serializable;

public class StatusContent implements Serializable {

    public StatusContent() {
    }

    /**
     * 创建时间
     */
    private String created_at;

    /**
     * 微博ID
     */
    private String row_key;

    /**
     * 微博信息内容
     */
    private String text;

    /**
     * 类别
     */
    private String type;


    private int userId;//暂时使用这个

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
