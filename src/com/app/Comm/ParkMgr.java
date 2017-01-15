package com.app.Comm;

import com.app.HttpConn.AsyncHttpClient;
import com.app.HttpConn.JsonHttpResponseHandler;
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
public class ParkMgr {
    public ParkMgr()
    {

    }

    public void GetParksWithName(String parkkey, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_getparks + "?parkkey=" + URLEncoder.encode(parkkey), handler);
    }

    public void GetParksWithId(String userid, String parkid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_getparkswithid + "?userid=" + URLEncoder.encode(userid) + "&parkid=" + URLEncoder.encode(parkid), handler);
    }

    public ArrayList<STParkInfo> ParseParkListFromJson(JSONObject jsonResult)
    {
        ArrayList<STParkInfo> retList = new ArrayList<STParkInfo>();

        try
        {
            JSONArray jsonObjArray = jsonResult.getJSONArray("data");
            for (int i = 0; i < jsonObjArray.length(); i++)
            {
                try {
                    STParkInfo parkInfo = new STParkInfo();
                    JSONObject jsonObj = jsonObjArray.getJSONObject(i);

                    parkInfo.uid = jsonObj.getInt("uid");
                    parkInfo.name = jsonObj.getString("name");

                    if (jsonObj.has("ismypark"))
                        parkInfo.isMyPark = (jsonObj.getInt("ismypark") != 0);
                    if (jsonObj.has("trailmap"))
                        parkInfo.trailmap = jsonObj.getString("trailmap").toLowerCase().equals("yes");
                    if (jsonObj.has("country"))
                        parkInfo.country = jsonObj.getString("country");
                    if (jsonObj.has("province"))
                        parkInfo.province = jsonObj.getString("province");
                    if (jsonObj.has("city"))
                        parkInfo.city = jsonObj.getString("city");
                    if (jsonObj.has("address"))
                        parkInfo.address = jsonObj.getString("address");
                    if (jsonObj.has("zipcode"))
                        parkInfo.zipcode = jsonObj.getString("zipcode");
                    if (jsonObj.has("vertical"))
                        parkInfo.vertical = jsonObj.getInt("vertical");
                    if (jsonObj.has("meters"))
                        parkInfo.meters = jsonObj.getInt("meters");
                    if (jsonObj.has("open_months"))
                        parkInfo.open_months = jsonObj.getString("open_months");
                    if (jsonObj.has("daily_cost"))
                        parkInfo.dailiy_cost = jsonObj.getString("daily_cost");
                    if (jsonObj.has("rental"))
                        parkInfo.rental = jsonObj.getString("rental");
                    if (jsonObj.has("shop"))
                        parkInfo.shop = jsonObj.getString("shop");
                    if (jsonObj.has("phone"))
                        parkInfo.phone = jsonObj.getString("phone");
                    if (jsonObj.has("url"))
                        parkInfo.url = jsonObj.getString("url");
                    if (jsonObj.has("facebook"))
                        parkInfo.facebook = jsonObj.getString("facebook");
                    if (jsonObj.has("trailmap"))
                        parkInfo.twitter = jsonObj.getString("twitter");
                    if (jsonObj.has("instagram"))
                        parkInfo.instagram = jsonObj.getString("instagram");
                    if (jsonObj.has("pinkbike"))
                        parkInfo.pinkbike = jsonObj.getString("pinkbike");
                    if (jsonObj.has("dirt"))
                        parkInfo.icondirt = jsonObj.getInt("dirt");

                    if (jsonObj.has("weather"))
                    {
                        JSONObject jsonWeather = (JSONObject)jsonObj.getJSONObject("weather");
                        if (jsonWeather.has("iconstate"))
                            parkInfo.iconstate = jsonWeather.getInt("iconstate");
                        if (jsonWeather.has("weatherstr"))
                            parkInfo.weatherStr = jsonWeather.getString("weatherstr");
                        if (jsonWeather.has("temp"))
                            parkInfo.curTemp= jsonWeather.getInt("temp");
                    }

                    retList.add(parkInfo);
                }
                catch (Exception e)
                {
                }
            }
        }
        catch (Exception e)
        {
        }

        return retList;
    }

    public STParkInfo ParseParkFromJsonString(JSONObject jsonResult)
    {
        STParkInfo parkInfo = new STParkInfo();

        try {
            JSONObject jsonObj = jsonResult.getJSONObject("data");
            parkInfo.uid = jsonObj.getInt("uid");
            parkInfo.name = jsonObj.getString("name");

            if (jsonObj.has("ismypark"))
                parkInfo.isMyPark = (jsonObj.getInt("ismypark") != 0);
            if (jsonObj.has("trailmap"))
                parkInfo.trailmap = jsonObj.getString("trailmap").toLowerCase().equals("yes");
            if (jsonObj.has("country"))
                parkInfo.country = jsonObj.getString("country");
            if (jsonObj.has("province"))
                parkInfo.province = jsonObj.getString("province");
            if (jsonObj.has("city"))
                parkInfo.city = jsonObj.getString("city");
            if (jsonObj.has("address"))
                parkInfo.address = jsonObj.getString("address");
            if (jsonObj.has("zipcode"))
                parkInfo.zipcode = jsonObj.getString("zipcode");
            if (jsonObj.has("vertical"))
                parkInfo.vertical = jsonObj.getInt("vertical");
            if (jsonObj.has("meters"))
                parkInfo.meters = jsonObj.getInt("meters");
            if (jsonObj.has("open_months"))
                parkInfo.open_months = jsonObj.getString("open_months");
            if (jsonObj.has("daily_cost"))
                parkInfo.dailiy_cost = jsonObj.getString("daily_cost");
            if (jsonObj.has("rental"))
                parkInfo.rental = jsonObj.getString("rental");
            if (jsonObj.has("shop"))
                parkInfo.shop = jsonObj.getString("shop");
            if (jsonObj.has("phone"))
                parkInfo.phone = jsonObj.getString("phone");
            if (jsonObj.has("url"))
                parkInfo.url = jsonObj.getString("url");
            if (jsonObj.has("facebook"))
                parkInfo.facebook = jsonObj.getString("facebook");
            if (jsonObj.has("trailmap"))
                parkInfo.twitter = jsonObj.getString("twitter");
            if (jsonObj.has("instagram"))
                parkInfo.instagram = jsonObj.getString("instagram");
            if (jsonObj.has("pinkbike"))
                parkInfo.pinkbike = jsonObj.getString("pinkbike");
            if (jsonObj.has("dirt"))
                parkInfo.icondirt = jsonObj.getInt("dirt");
            if (jsonObj.has("latitude"))
                parkInfo.latitude = (float)jsonObj.getDouble("latitude");
            if (jsonObj.has("longitude"))
                parkInfo.longitude = (float)jsonObj.getDouble("longitude");

            if (jsonObj.has("weather"))
            {
                JSONObject jsonWeather = (JSONObject)jsonObj.getJSONObject("weather");
                if (jsonWeather.has("iconstate"))
                    parkInfo.iconstate = jsonWeather.getInt("iconstate");
                if (jsonWeather.has("weatherstr"))
                    parkInfo.weatherStr = jsonWeather.getString("weatherstr");
                if (jsonWeather.has("temp"))
                    parkInfo.curTemp= jsonWeather.getInt("temp");
            }

            if (jsonObj.has("weatherprev1"))
            {
                JSONObject jsonWeatherPrev1 = (JSONObject)jsonObj.getJSONObject("weatherprev1");
                if (jsonWeatherPrev1.has("iconstate"))
                    parkInfo.before1State = jsonWeatherPrev1.getInt("iconstate");
                if (jsonWeatherPrev1.has("weatherstr"))
                    parkInfo.before1weatherStr = jsonWeatherPrev1.getString("weatherstr");
                if (jsonWeatherPrev1.has("fhigh"))
                    parkInfo.before1fHigh = jsonWeatherPrev1.getInt("fhigh");
                if (jsonWeatherPrev1.has("flow"))
                    parkInfo.before1fLow = jsonWeatherPrev1.getInt("flow");
            }

            if (jsonObj.has("weatherprev2"))
            {
                JSONObject jsonWeatherPrev2 = (JSONObject)jsonObj.getJSONObject("weatherprev2");
                if (jsonWeatherPrev2.has("iconstate"))
                    parkInfo.before2State = jsonWeatherPrev2.getInt("iconstate");
                if (jsonWeatherPrev2.has("weatherstr"))
                    parkInfo.before2weatherStr = jsonWeatherPrev2.getString("weatherstr");
                if (jsonWeatherPrev2.has("fhigh"))
                    parkInfo.before2fHigh = jsonWeatherPrev2.getInt("fhigh");
                if (jsonWeatherPrev2.has("flow"))
                    parkInfo.before2fLow = jsonWeatherPrev2.getInt("flow");
            }

            if (jsonObj.has("weatherprev3"))
            {
                JSONObject jsonWeatherPrev3 = (JSONObject)jsonObj.getJSONObject("weatherprev3");
                if (jsonWeatherPrev3.has("iconstate"))
                    parkInfo.before3State = jsonWeatherPrev3.getInt("iconstate");
                if (jsonWeatherPrev3.has("weatherstr"))
                    parkInfo.before3weatherStr = jsonWeatherPrev3.getString("weatherstr");
                if (jsonWeatherPrev3.has("fhigh"))
                    parkInfo.before3fHigh = jsonWeatherPrev3.getInt("fhigh");
                if (jsonWeatherPrev3.has("flow"))
                    parkInfo.before3fLow = jsonWeatherPrev3.getInt("flow");
            }

            if (jsonObj.has("weathernext1"))
            {
                JSONObject jsonWeatherafter1 = (JSONObject)jsonObj.getJSONObject("weathernext1");
                if (jsonWeatherafter1.has("iconstate"))
                    parkInfo.after1State = jsonWeatherafter1.getInt("iconstate");
                if (jsonWeatherafter1.has("weatherstr"))
                    parkInfo.after1weatherStr = jsonWeatherafter1.getString("weatherstr");
                if (jsonWeatherafter1.has("fhigh"))
                    parkInfo.after1fHigh = jsonWeatherafter1.getInt("fhigh");
                if (jsonWeatherafter1.has("flow"))
                    parkInfo.after1fLow = jsonWeatherafter1.getInt("flow");
            }

            if (jsonObj.has("weathernext2"))
            {
                JSONObject jsonWeatherafter2 = (JSONObject)jsonObj.getJSONObject("weathernext2");
                if (jsonWeatherafter2.has("iconstate"))
                    parkInfo.after2State = jsonWeatherafter2.getInt("iconstate");
                if (jsonWeatherafter2.has("weatherstr"))
                    parkInfo.after2weatherStr = jsonWeatherafter2.getString("weatherstr");
                if (jsonWeatherafter2.has("fhigh"))
                    parkInfo.after2fHigh = jsonWeatherafter2.getInt("fhigh");
                if (jsonWeatherafter2.has("flow"))
                    parkInfo.after2fLow = jsonWeatherafter2.getInt("flow");
            }

            if (jsonObj.has("weathernext3"))
            {
                JSONObject jsonWeatherafter3 = (JSONObject)jsonObj.getJSONObject("weathernext3");
                if (jsonWeatherafter3.has("iconstate"))
                    parkInfo.after3State = jsonWeatherafter3.getInt("iconstate");
                if (jsonWeatherafter3.has("weatherstr"))
                    parkInfo.after3weatherStr = jsonWeatherafter3.getString("weatherstr");
                if (jsonWeatherafter3.has("fhigh"))
                    parkInfo.after3fHigh = jsonWeatherafter3.getInt("fhigh");
                if (jsonWeatherafter3.has("flow"))
                    parkInfo.after3fLow = jsonWeatherafter3.getInt("flow");
            }
        }
        catch (Exception e)
        {

        }

        return parkInfo;
    }

    public boolean ParseAddParkResultFromJSONResult(JSONObject jsonResult)
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

    public void AddParktoMyParkList(String userid, String parkid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_addtomypark + "?userid=" + URLEncoder.encode(userid) + "&parkid=" + URLEncoder.encode(parkid), handler);
    }

    public void RemoveParkFromMyParkList(String userid, String parkid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_removefrommypark + "?userid=" + URLEncoder.encode(userid) + "&parkid=" + URLEncoder.encode(parkid), handler);
    }

    public void GetMyParksList(String userid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_getmyparks + "?userid=" + URLEncoder.encode(userid), handler);
    }

    public void GetHourlyWeather(String parkid, JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_gethourlyweather + "?parkid=" + URLEncoder.encode(parkid), handler);
    }

    public ArrayList<STHourlyInfo> ParseHourlyDataFromJson(JSONObject jsonResult)
    {
        ArrayList<STHourlyInfo> retList = new ArrayList<STHourlyInfo>();

        try
        {
            for (int i = 1; i <= 12; i++)
            {
                STHourlyInfo hrInfo = new STHourlyInfo();
                try
                {
                    if (jsonResult.has("weather" + i))
                    {
                        JSONObject jsonWeather = jsonResult.getJSONObject("weather" + i);
                        try {

                            if (jsonWeather.has("iconstate"))
                                hrInfo.iconstate = jsonWeather.getInt("iconstate");
                            if (jsonWeather.has("weatherstr"))
                                hrInfo.weatherStr = jsonWeather.getString("weatherstr");
                            if (jsonWeather.has("temp"))
                                hrInfo.temp= jsonWeather.getInt("temp");
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
                catch (Exception e)
                {

                }

                retList.add(hrInfo);
            }
        }
        catch (Exception e)
        {

        }

        return retList;
    }

    public void GetEpicData(JsonHttpResponseHandler handler)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Global.webservice_timeout);
        client.get(Global.webservice_base_url + Global.webservice_getepicdata, handler);
    }

    public ArrayList<String> ParsePopularStringFromJson(JSONObject jsonResult)
    {
        ArrayList<String> retList = new ArrayList<String>();

        try
        {
            JSONArray jsonpopular = jsonResult.getJSONArray("populardata");
            try {
                for (int i = 0; i < jsonpopular.length(); i++)
                {
                    JSONObject jsonobj = jsonpopular.getJSONObject(i);
                    String parkname = jsonobj.getString("name");
                    retList.add(parkname);
                }
            }
            catch (Exception e)
            {

            }
        }
        catch (Exception e)
        {

        }

        return retList;
    }
}
