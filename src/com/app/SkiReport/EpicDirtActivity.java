package com.app.SkiReport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.app.Comm.CommMgr;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STNewsInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.Global;
import com.app.Utils.ResolutionSet;
import com.app.Utils.XmlParser;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EpicDirtActivity extends Activity {
    private ParkNewsItemAdapter mNewsAdapter = null;
    private ParkPopularItemAdapter mPopularAdapter = null;

    private ProgressDialog progMainDialog;
    private JsonHttpResponseHandler handlerRespInfo = null;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    boolean bUpdatedNews = false;
    boolean bUpdatedPopular = false;

    ListView listNews, listPopular;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.parkepicdirt);
        setContentView(R.layout.parknews);

        //ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkEpicMain));
        mainLayout = (RelativeLayout)findViewById(R.id.RLParkEpicMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkEpicMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        listNews = (ListView)findViewById(R.id.listViewNews);

        listNews.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listNews.setCacheColorHint(Color.TRANSPARENT);//Color.parseColor("#FFF1F1F1"));
        listNews.setDividerHeight(20);
        mNewsAdapter = new ParkNewsItemAdapter(EpicDirtActivity.this, this.getApplicationContext());
        listNews.setAdapter(mNewsAdapter);

        listPopular = (ListView)findViewById(R.id.listViewPopular);

        listPopular.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listPopular.setCacheColorHint(Color.TRANSPARENT);
        listPopular.setDividerHeight(20);
        mPopularAdapter = new ParkPopularItemAdapter(EpicDirtActivity.this, this.getApplicationContext());
        listPopular.setAdapter(mPopularAdapter);

        TextView txtSearch = (TextView)findViewById(R.id.lblSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EpicDirtActivity.this, ParkSearchActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtSetting = (TextView)findViewById(R.id.lblSettings);
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EpicDirtActivity.this, ParkSettingActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtParkList = (TextView)findViewById(R.id.lblMyParks);
        txtParkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EpicDirtActivity.this, ParkListActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        RunBackGround();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(EpicDirtActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(EpicDirtActivity.this).activityStop(this); // Add this method.
    }

    private void RunBackGround()
    {
        handlerRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                //ArrayList<STParkInfo> parkList = _commMgr.ParseParkListFromJsonString(result);

                //mAdapter.setData(parkList);
                //listPark.setAdapter(mAdapter);

                ArrayList<String> popularList = _commMgr.ParsePopularStringFromJson(result);
                mPopularAdapter.setData(popularList);
                listPopular.setAdapter(mPopularAdapter);

                Button btnMap = (Button)findViewById(R.id.btnMap);
                try
                {
                    btnMap.setTag(result.getString("mapurl"));
                    btnMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent mapIntent = new Intent(EpicDirtActivity.this, MapViewActivity.class);
                            mapIntent.putExtra("mapurl", (String) view.getTag());
                            startActivity(mapIntent);
                        }
                    });
                }
                catch (Exception e)
                {

                }

                retval = 1;
            }

            @Override
            public void onFailure(Throwable ex, String exception){
            }

            @Override
            public void onFinish()
            {
                bUpdatedPopular = true;
                if (bUpdatedNews == true)
                    progMainDialog.dismiss();

                if (retval == 0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_servererror), Toast.LENGTH_SHORT).show();
                }

                retval = 0;
            }
        };

        progMainDialog = ProgressDialog.show(
                EpicDirtActivity.this,
                "",
                getString(R.string.msg_waiting),
                true,
                true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                }
        );

        CommMgr _comMgr = new CommMgr();
        _comMgr.GetEpicData(handlerRespInfo);

        new DownloadXmlTask().execute(Global.web_news_url);
    }

    public void showInfoActivity(int uid)
    {
        Intent parkDetIntent = new Intent(EpicDirtActivity.this, ParkInfoActivity.class);
        parkDetIntent.putExtra("parkuid", uid);

        startActivity(parkDetIntent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void gotoUrl(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<STNewsInfo>> {

        @Override
        protected List<STNewsInfo> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(List<STNewsInfo> result) {
            bUpdatedNews = true;
            if (bUpdatedPopular == true)
                progMainDialog.dismiss();

            mNewsAdapter.setData(result);
            listNews.setAdapter(mNewsAdapter);
        }
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private List<STNewsInfo> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        XmlParser stackOverflowXmlParser = new XmlParser();
        List<STNewsInfo> entries = null;
        String title = null;
        String imageurl = null;

        try {
            stream = downloadUrl(urlString);
            entries = stackOverflowXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
}
