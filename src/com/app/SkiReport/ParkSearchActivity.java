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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.app.Comm.CommMgr;
import com.app.HttpConn.AsyncHttpResponseHandler;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.ResolutionSet;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParkSearchActivity extends Activity {
    private ParkSearchItemAdapter mAdapter = null;
    private STParkInfo[] parkInfo = null;

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
        setContentView(R.layout.parksearch);

        //ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkSearchMain));
        mainLayout = (RelativeLayout)findViewById(R.id.RLParkSearchMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkSearchMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        listPark = (ListView)findViewById(R.id.listViewParks);

        listPark.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listPark.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
        listPark.setDividerHeight(20);
        mAdapter = new ParkSearchItemAdapter(ParkSearchActivity.this, this.getApplicationContext());
        listPark.setAdapter(mAdapter);

        TextView txtMyParks = (TextView)findViewById(R.id.lblMyParks);
        txtMyParks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkSearchActivity.this, ParkListActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        TextView txtSetting = (TextView)findViewById(R.id.lblSettings);
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkSearchActivity.this, ParkSettingActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtEpic = (TextView)findViewById(R.id.lblEpicDirt);
        txtEpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkSearchActivity.this, EpicDirtActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                EditText ednKey = (EditText)findViewById(R.id.ednSearch);
                String parkkey = ednKey.getText().toString();

                CommMgr _comMgr = new CommMgr();
                _comMgr.GetParksWithName(parkkey, handlerRespInfo);

                progMainDialog = ProgressDialog.show(
                        ParkSearchActivity.this,
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
            }
        });

        RunBackGround();
    }


    public void hideSoftKeyboard() {
        View viewEdit = findViewById(R.id.ednSearch);

        if(viewEdit!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewEdit.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (getCurrentFocus() != null)
            {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideSoftKeyboard();
    }

    private void RunBackGround()
    {
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
    }

    public void showInfoActivity(int uid)
    {
        hideSoftKeyboard();
        Intent parkDetIntent = new Intent(ParkSearchActivity.this, ParkInfoActivity.class);
        parkDetIntent.putExtra("parkuid", uid);

        startActivity(parkDetIntent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(ParkSearchActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(ParkSearchActivity.this).activityStop(this); // Add this method.
    }
}
