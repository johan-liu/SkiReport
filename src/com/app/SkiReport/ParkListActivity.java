package com.app.SkiReport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParkListActivity extends Activity {
    private STParkInfo[] parkInfo = null;
    private ParkListItemAdapter mAdapter = null;

    private ProgressDialog progMainDialog;
    private JsonHttpResponseHandler handlerRespInfo = null;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    ListView listPark;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parklist);

        //ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkListMain));
        mainLayout = (RelativeLayout)findViewById(R.id.RLParkListMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkListMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        listPark = (ListView)findViewById(R.id.listViewParks);

        listPark.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listPark.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
        listPark.setDividerHeight(10);
        mAdapter = new ParkListItemAdapter(ParkListActivity.this, this.getApplicationContext());
        listPark.setAdapter(mAdapter);

        TextView txtSearch = (TextView)findViewById(R.id.lblSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkListActivity.this, ParkSearchActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtSetting = (TextView)findViewById(R.id.lblSettings);
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkListActivity.this, ParkSettingActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtEpic = (TextView)findViewById(R.id.lblEpicDirt);
        txtEpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkListActivity.this, EpicDirtActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        RunBackGround();
    }

    public void showInfoActivity(int uid)
    {
        Intent parkDetIntent = new Intent(ParkListActivity.this, ParkInfoActivity.class);
        parkDetIntent.putExtra("parkuid", uid);

        startActivity(parkDetIntent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void RunBackGround()
    {
        int userid;
        userid = AppData.GetUserId(ParkListActivity.this);
        if (userid == -1)
            finish();

        handlerRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                ArrayList<STParkInfo> parkList = _commMgr.ParseParkListFromJsonString(result);

                mAdapter.setData(parkList);
                listPark.setAdapter(mAdapter);

                retval = 1;
            }

            @Override
            public void onFailure(Throwable ex, String exception){
            }

            @Override
            public void onFinish()
            {
                progMainDialog.dismiss();

                if (retval == 0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_servererror), Toast.LENGTH_SHORT).show();
                }

                retval = 0;
            }
        };

        progMainDialog = ProgressDialog.show(
                ParkListActivity.this,
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
        _comMgr.GetMyParksList(Integer.toString(userid), handlerRespInfo);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(ParkListActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(ParkListActivity.this).activityStop(this); // Add this method.
    }
}
