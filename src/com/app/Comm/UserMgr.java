package com.app.Comm;

import com.app.HttpConn.AsyncHttpClient;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STParkInfo;
import com.app.Utils.Global;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by MaHanYong on 4/16/14.
 */
public class UserMgr {
    public UserMgr(){

    }

    public void RegisterUser(String username, String country, String version, String phoneEmail, String userLocation, String devuid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_registeruser + "?username=" + URLEncoder.encode(username) + "&country=" + URLEncoder.encode(country) + "&phonever=" + URLEncoder.encode(version) + "&useremail=" + URLEncoder.encode(phoneEmail) + "&phonetype=1&location=" + URLEncoder.encode(userLocation) + "&devuid=" + URLEncoder.encode(devuid), handler);
    }

    public void GetLoginId(String username, String country, String version, String phoneEmail, String userLocation, String devuid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_loginuser + "?username=" + URLEncoder.encode(username) + "&country=" + URLEncoder.encode(country) + "&phonever=" + URLEncoder.encode(version) + "&useremail=" + URLEncoder.encode(phoneEmail) + "&phonetype=1&location=" + URLEncoder.encode(userLocation) + "&devuid=" + URLEncoder.encode(devuid), handler);
    }

    public int ParseUserIdFromJsonString(JSONObject jsonResult)
    {
        int userid = -1;
        try {
            userid = jsonResult.getInt("uid");
        }
        catch (Exception e)
        {

        }

        return userid;
    }
}
