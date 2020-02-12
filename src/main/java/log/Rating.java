package log;

/**
 * 用户对动态的评分，写入日志
 */
public class Rating {

    private int user;

    private String rowKey;

    private int score;


    public Rating() {
    }

    public Rating(int user, String rowKey, int score) {
        this.user = user;
        this.rowKey = rowKey;
        this.score = score;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "user=" + user +
                ", rowKey='" + rowKey + '\'' +
                ", score=" + score +
                '}';
    }
}
