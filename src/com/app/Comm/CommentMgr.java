package com.app.Comm;

import com.app.HttpConn.AsyncHttpClient;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STCommentInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.Global;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by MaHanYong on 4/16/14.
 */
public class CommentMgr {
    public CommentMgr()
    {

    }

    public void PostComment(String userid, String parkid, String comment, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_postcomment + "?userid=" + URLEncoder.encode(userid) + "&parkid=" + URLEncoder.encode(parkid) + "&comment=" + URLEncoder.encode(comment), handler);
    }

    public void GetLatestComments(String parkid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_getcomments + "?parkid=" + URLEncoder.encode(parkid), handler);
    }

    public ArrayList<STCommentInfo> ParseCommentsFromJsonString(JSONObject jsonResult)
    {
        ArrayList<STCommentInfo> result = new ArrayList<STCommentInfo>();

        try
        {
            JSONArray jsonObjArray = jsonResult.getJSONArray("data");
            for (int i = 0; i < jsonObjArray.length(); i++)
            {
                try {
                    STCommentInfo commentInfo = new STCommentInfo();
                    JSONObject jsonObj = jsonObjArray.getJSONObject(i);

                    commentInfo.uid = jsonObj.getInt("uid");

                    if (jsonObj.has("comment"))
                        commentInfo.comment = jsonObj.getString("comment");
                    if (jsonObj.has("datetime"))
                        commentInfo.datetime = jsonObj.getString("datetime");
                    if (jsonObj.has("userinfo"))
                        commentInfo.userinfo = jsonObj.getString("userinfo");

                    result.add(commentInfo);
                }
                catch (Exception e)
                {

                }
            }
        }
        catch (Exception e)
        {

        }

        return result;
    }

    public boolean ParseCommentResultFromJsonString(JSONObject jsonResult)
    {
        boolean bRet = false;
        try
        {
            bRet = (jsonResult.getInt("ret") == 0);
        }
        catch (Exception e)
        {

        }

        return bRet;
    }
}
