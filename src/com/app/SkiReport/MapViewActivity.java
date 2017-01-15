package com.app.SkiReport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.app.Comm.CommMgr;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.ResolutionSet;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapViewActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkmapview);

        //ResolutionSet._instance.iterateChild(findViewById(R.id.RLMapMain));
        mainLayout = (RelativeLayout)findViewById(R.id.RLMapMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLMapMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        WebView web = (WebView) findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        // web.getSettings().setPluginState(PluginState.ON);

        web.setWebViewClient(new WebViewClient());

        String mapurl = getIntent().getStringExtra("mapurl");
        web.loadUrl(mapurl);
    }
}
