package server.entity;

import java.io.Serializable;

public class PicUrls implements Serializable {

    /**
     * 图片的地址
     */
    private String thumbnail_pic;

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }
}
