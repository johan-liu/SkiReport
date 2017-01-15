package com.app.STInfo;

/**
 * Created by MaHanYong on 3/26/14.
 */
public class STNewsInfo {
    // Uid
    public int uid = -1;
    public String imageUrl = "";
    public String title = "";
    public String link = "";

    public STNewsInfo(String title, String link, String imageurl)
    {
        this.imageUrl = imageurl;
        this.link = link;
        this.title = title;
    }
}
