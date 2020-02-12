package server.es;

import net.sf.json.JSONObject;

public class QueryKey {

    private JSONObject equals;

    private JSONObject like;

    private JSONObject ge;

    private JSONObject le;

    public JSONObject getEquals() {
        return equals;
    }

    public void setEquals(JSONObject equals) {
        this.equals = equals;
    }

    public JSONObject getLike() {
        return like;
    }

    public void setLike(JSONObject like) {
        this.like = like;
    }

    public JSONObject getGe() {
        return ge;
    }

    public void setGe(JSONObject ge) {
        this.ge = ge;
    }

    public JSONObject getLe() {
        return le;
    }

    public void setLe(JSONObject le) {
        this.le = le;
    }
}
