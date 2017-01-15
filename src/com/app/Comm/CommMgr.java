package com.app.Comm;

import com.app.HttpConn.AsyncHttpClient;
import com.app.HttpConn.AsyncHttpResponseHandler;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STCommentInfo;
import com.app.STInfo.STHourlyInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.Global;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaHanYong on 4/16/14.
 */
public class CommMgr {
    ParkMgr _parkMgr;
    UserMgr _userMgr;
    CommentMgr _commentMgr;
    WeatherMgr _weatherMgr;

    public CommMgr()
    {
        _parkMgr = new ParkMgr();
        _userMgr = new UserMgr();
        _weatherMgr = new WeatherMgr();
        _commentMgr = new CommentMgr();
    }

    public void GetParksWithName(String parkkey, JsonHttpResponseHandler handler)
    {
        _parkMgr.GetParksWithName(parkkey, handler);
    }

    public void GetParksWithId(String userid, String parkid, JsonHttpResponseHandler handler)
    {
        _parkMgr.GetParksWithId(userid, parkid, handler);
    }

    public ArrayList<STParkInfo> ParseParkListFromJsonString(JSONObject jsonResult)
    {
        return _parkMgr.ParseParkListFromJson(jsonResult);
    }

    public STParkInfo ParseParkFromJsonString(JSONObject jsonResult)
    {
        return _parkMgr.ParseParkFromJsonString(jsonResult);
    }

    public void RegisterUser(String username, String countryname, String version, String phoneEmail, String userLocation, String devuid, JsonHttpResponseHandler handler)
    {
        _userMgr.RegisterUser(username, countryname, version, phoneEmail, userLocation, devuid, handler);
    }

    public void GetLoginId(String username, String countryname, String version, String phoneEmail, String userLocation, String devuid, JsonHttpResponseHandler handler)
    {
        _userMgr.GetLoginId(username, countryname, version, phoneEmail, userLocation, devuid, handler);
    }

    public int ParseUserIdFromJsonString(JSONObject jsonResult)
    {
        return _userMgr.ParseUserIdFromJsonString(jsonResult);
    }

    public void PostComment(String userid, String parkid, String comment, JsonHttpResponseHandler handler)
    {
        _commentMgr.PostComment(userid, parkid, comment, handler);
    }

    public void GetLatestComments(String parkid, JsonHttpResponseHandler handler)
    {
        _commentMgr.GetLatestComments(parkid, handler);
    }

    public ArrayList<STCommentInfo> ParseCommentsFromJsonString(JSONObject jsonResult)
    {
        return _commentMgr.ParseCommentsFromJsonString(jsonResult);
    }

    public boolean ParseCommentResultFromJsonString(JSONObject jsonResult)
    {
        return _commentMgr.ParseCommentResultFromJsonString(jsonResult);
    }

    public boolean ParseAddParkResultFromJSONResult(JSONObject jsonResult)
    {
        return _parkMgr.ParseAddParkResultFromJSONResult(jsonResult);
    }

    public void AddParktoMyParkList(String userid, String parkid, JsonHttpResponseHandler handler)
    {
        _parkMgr.AddParktoMyParkList(userid, parkid, handler);
    }

    public void RemoveParkFromMyParkList(String userid, String parkid, JsonHttpResponseHandler handler)
    {
        _parkMgr.RemoveParkFromMyParkList(userid, parkid, handler);
    }

    public void GetMyParksList(String userid, JsonHttpResponseHandler handler)
    {
        _parkMgr.GetMyParksList(userid, handler);
    }

    public void GetPushNotifications(String userid, String lastupdateid, AsyncHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_getpushnotification + "?userid=" + URLEncoder.encode(userid) + "&lastcheckid=" + URLEncoder.encode(lastupdateid), handler);
    }

    public void GetHourlyWeather(String parkid, JsonHttpResponseHandler handler)
    {
        _parkMgr.GetHourlyWeather(parkid, handler);
    }

    public ArrayList<STHourlyInfo> ParseHourlyListFromJsonString(JSONObject jsonResult)
    {
        return _parkMgr.ParseHourlyDataFromJson(jsonResult);
    }

    public void GetEpicData(JsonHttpResponseHandler handler)
    {
        _parkMgr.GetEpicData(handler);
    }

    public ArrayList<String> ParsePopularStringFromJson(JSONObject jsonResult)
    {
        return _parkMgr.ParsePopularStringFromJson(jsonResult);
    }
}
