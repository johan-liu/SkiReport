package com.app.SkiReport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.app.Comm.CommMgr;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STParkInfo;
import com.app.Service.PushNotificationService;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.MyLocation;
import com.app.Utils.ResolutionSet;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity{
    /**
     * Called when the activity is first created.
     */
    RelativeLayout mainLayout;
    boolean bInitialized = false;
    boolean bLoginChecked = false;
    public static SplashActivity _instance = null;
    private Handler handler, handler1;

    final int LOADINGVIEW_TIMEOUT = 30000;
    long 	tmBegin , tmNow;
    Timer timer_1 = new Timer();

    private JsonHttpResponseHandler handlerRespInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        _instance = this;

        startPushNotificationService();

        mainLayout = (RelativeLayout)findViewById(R.id.RLSplashMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLSplashMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0)
                {
                    startActivity(new Intent(SplashActivity.this, ParkListActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            }
        };

        handler1 = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0)
                {
                    startActivity(new Intent(SplashActivity.this, ParkSettingActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            }
        };

        RunBackGround();
        //handler.sendEmptyMessageDelayed(0, 3000);
        tmBegin = System.currentTimeMillis();
        createTimer();
    }

    public class LoadingRunnable implements Runnable {
        public int nImageNo;
        public LoadingRunnable(int nImageNo) {
            this.nImageNo = nImageNo;
        }
        //@Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                ImageView loadingImage1 = (ImageView)findViewById(R.id.imgProg1);
                ImageView loadingImage2 = (ImageView)findViewById(R.id.imgProg2);
                ImageView loadingImage3 = (ImageView)findViewById(R.id.imgProg3);
                ImageView loadingImage4 = (ImageView)findViewById(R.id.imgProg4);
                ImageView loadingImage5 = (ImageView)findViewById(R.id.imgProg5);
                ImageView loadingImage6 = (ImageView)findViewById(R.id.imgProg6);

                loadingImage1.setImageResource(R.drawable.progress_white);
                loadingImage2.setImageResource(R.drawable.progress_white);
                loadingImage3.setImageResource(R.drawable.progress_white);
                loadingImage4.setImageResource(R.drawable.progress_white);
                loadingImage5.setImageResource(R.drawable.progress_white);
                loadingImage6.setImageResource(R.drawable.progress_white);

                if (nImageNo >= 0)
                    loadingImage1.setImageResource(R.drawable.progress_blue);
                if (nImageNo >= 1)
                    loadingImage2.setImageResource(R.drawable.progress_blue);
                if (nImageNo >= 2)
                    loadingImage3.setImageResource(R.drawable.progress_blue);
                if (nImageNo >= 3)
                    loadingImage4.setImageResource(R.drawable.progress_blue);
                if (nImageNo >= 4)
                    loadingImage5.setImageResource(R.drawable.progress_blue);
                if (nImageNo >= 5)
                    loadingImage6.setImageResource(R.drawable.progress_blue);
            } catch (Exception ex) {

            }
        }

    }
    private void createTimer() {

        timer_1.schedule(new TimerTask() {
            int nImageNo = 0;
            LoadingRunnable imgLoader = new LoadingRunnable(0);
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    tmNow = System.currentTimeMillis();
                    if ( tmBegin > 0 && tmNow - tmBegin > LOADINGVIEW_TIMEOUT )
                    {
                        Toast.makeText(SplashActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                        timer_1.cancel();
                        SplashActivity.this.finish();
                        return;
                    }

                    if (_instance != null) {
                        imgLoader.nImageNo = nImageNo;
                        _instance.runOnUiThread(imgLoader);
                    }
                    nImageNo = (nImageNo + 1) % 6;

                    if (nImageNo == 0 && bLoginChecked == false)
                    {
                        bLoginChecked = true;
                        String strUserName = AppData.GetUserName(SplashActivity.this);
                        String strCountryName = AppData.GetUserCountry(SplashActivity.this);

                        if (strUserName.equals("") == true || strCountryName.equals("") == true)
                        {
                            timer_1.cancel();
                            handler1.sendEmptyMessageDelayed(0, 100);
                        }
                        else
                        {
                            CommMgr _comMgr = new CommMgr();
                            String AndroidVersion = android.os.Build.VERSION.RELEASE;
                            String devuid = AppData.GetDeviceUdid(SplashActivity.this);
                            _comMgr.GetLoginId(strUserName, strCountryName, AndroidVersion, AppData.GetUserPhoneEmail(SplashActivity.this), AppData.GetUserLocation(SplashActivity.this), devuid, handlerRespInfo);
                        }
                    }
                } catch (Exception e) {
                    this.cancel();
                }
            }
        }, 0, 300);
    }

    private void RunBackGround()
    {
        handlerRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                retval = 1;

                CommMgr _commMgr = new CommMgr();
                int userId = _commMgr.ParseUserIdFromJsonString(result);
                AppData.SetUserId(SplashActivity.this, userId);
                if (userId == -1)
                {
                    handler1.sendEmptyMessageDelayed(0, 100);
                }
                else
                {
                    handler.sendEmptyMessageDelayed(0, 100);
                }
            }

            @Override
            public void onFailure(Throwable ex, String exception){
            }

            @Override
            public void onFinish()
            {
                if (retval == 0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_servererror), Toast.LENGTH_SHORT).show();
                    timer_1.cancel();
                    finish();
                }

                retval = 0;
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(SplashActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(SplashActivity.this).activityStop(this); // Add this method.
    }

    private void startPushNotificationService()
    {
        Context context = SplashActivity.this;

        PushNotificationService.m_ctxMain = context;
        Intent service = new Intent(context, PushNotificationService.class);
        context.startService(service);
    }
}
