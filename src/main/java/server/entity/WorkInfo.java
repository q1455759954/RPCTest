package server.entity;



import java.io.Serializable;

/**
 * Created by Administrator on 2019/10/18.
 */

public class WorkInfo implements Serializable{

    public WorkInfo() {
    }

    private String row_key;

    private String title;

    private String content;

    private String userId;

    private String create_at;

    /**
     * 是否下架
     */
    private boolean state = false;

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
