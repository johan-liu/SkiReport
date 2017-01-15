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
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.ResolutionSet;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONObject;

public class ParkSettingActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private ProgressDialog progMainDialog;
    private JsonHttpResponseHandler handlerRespInfo = null;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parksetting);

        //ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkSettingMain));
        mainLayout = (RelativeLayout)findViewById(R.id.RLParkSettingMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkSettingMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        String username = AppData.GetUserName(ParkSettingActivity.this);
        String country = AppData.GetUserCountry(ParkSettingActivity.this);
        Boolean bPushNotification = AppData.GetPushNotificationState(ParkSettingActivity.this);
        Boolean bTemperature = AppData.GetTemperState(ParkSettingActivity.this);

        EditText ednUserName = (EditText)findViewById(R.id.ednUsername);
        ednUserName.setText(username);
        EditText ednCountry = (EditText)findViewById(R.id.ednCountry);
        ednCountry.setText(country);

        TextView txtNotifyOn = (TextView)findViewById(R.id.txtNotifyOn);
        txtNotifyOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlNotify = (RelativeLayout)findViewById(R.id.RLNotifyBack);
                rlNotify.setBackgroundResource(R.drawable.setting_option);
                AppData.SetPushNotificationState(ParkSettingActivity.this, true);
            }
        });

        TextView txtNotifyOff = (TextView)findViewById(R.id.txtNotifyOff);
        txtNotifyOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlNotify = (RelativeLayout)findViewById(R.id.RLNotifyBack);
                rlNotify.setBackgroundResource(R.drawable.setting_option_nonactive);
                AppData.SetPushNotificationState(ParkSettingActivity.this, false);
            }
        });

        TextView txtUnitsOn = (TextView)findViewById(R.id.txtUnitsUsa);
        txtUnitsOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlUnits = (RelativeLayout)findViewById(R.id.RLUnitsBack);
                rlUnits.setBackgroundResource(R.drawable.setting_option);
                AppData.SetTemperState(ParkSettingActivity.this, true);
            }
        });

        TextView txtUnitsOff = (TextView)findViewById(R.id.txtUnitsOther);
        txtUnitsOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlUnits = (RelativeLayout)findViewById(R.id.RLUnitsBack);
                rlUnits.setBackgroundResource(R.drawable.setting_option_nonactive);
                AppData.SetTemperState(ParkSettingActivity.this, false);
            }
        });

        RelativeLayout rlNotify = (RelativeLayout)findViewById(R.id.RLNotifyBack);
        RelativeLayout rlUnits = (RelativeLayout)findViewById(R.id.RLUnitsBack);
        if (bPushNotification)
        {
            rlNotify.setBackgroundResource(R.drawable.setting_option);
        }
        else
        {
            rlNotify.setBackgroundResource(R.drawable.setting_option_nonactive);
        }

        if (bTemperature)
        {
            rlUnits.setBackgroundResource(R.drawable.setting_option);
        }
        else
        {
            rlUnits.setBackgroundResource(R.drawable.setting_option_nonactive);
        }

        Button btnSaveChanges = (Button)findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ednUserName = (EditText)findViewById(R.id.ednUsername);
                EditText ednCountry = (EditText)findViewById(R.id.ednCountry);

                String userName = ednUserName.getText().toString();
                String country = ednCountry.getText().toString();
                if (userName.equals(""))
                {
                    Toast.makeText(ParkSettingActivity.this, "Please input username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (country.equals(""))
                {
                    Toast.makeText(ParkSettingActivity.this, "Please input country", Toast.LENGTH_LONG).show();
                    return;
                }

                AppData.SetUserName(ParkSettingActivity.this, userName);
                AppData.SetUserCountry(ParkSettingActivity.this, country);
                RunBackGround();
            }
        });

        TextView txtMyParks = (TextView)findViewById(R.id.lblMyParks);
        txtMyParks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userid = AppData.GetUserId(ParkSettingActivity.this);
                if (userid != -1)
                {
                    startActivity(new Intent(ParkSettingActivity.this, ParkListActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
                else
                {
                    Toast.makeText(ParkSettingActivity.this, "You have to login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView txtSearch = (TextView)findViewById(R.id.lblSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userid = AppData.GetUserId(ParkSettingActivity.this);
                if (userid != -1)
                {
                    startActivity(new Intent(ParkSettingActivity.this, ParkSearchActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
                else
                {
                    Toast.makeText(ParkSettingActivity.this, "You have to login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView txtEpic = (TextView)findViewById(R.id.lblEpicDirt);
        txtEpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userid = AppData.GetUserId(ParkSettingActivity.this);
                if (userid != -1)
                {
                    startActivity(new Intent(ParkSettingActivity.this, EpicDirtActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
                else
                {
                    Toast.makeText(ParkSettingActivity.this, "You have to login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnContactUs = (Button)findViewById(R.id.btnContactUs);
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPostEmailActivity();
            }
        });
    }

    public void hideSoftKeyboard() {
        View viewUser = findViewById(R.id.ednUsername);
        View viewCountry = findViewById(R.id.ednCountry);

        if(viewUser!=null && viewCountry!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewUser.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            inputMethodManager.hideSoftInputFromWindow(viewCountry.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                int userId = _commMgr.ParseUserIdFromJsonString(result);

                if (userId != -1)
                {
                    retval = 1;

                    AppData.SetUserId(ParkSettingActivity.this, userId);

                    startActivity(new Intent(ParkSettingActivity.this, ParkListActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
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
                ParkSettingActivity.this,
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
        String AndroidVersion = android.os.Build.VERSION.RELEASE;
        String devuid = AppData.GetDeviceUdid(ParkSettingActivity.this);
        _comMgr.RegisterUser(AppData.GetUserName(ParkSettingActivity.this), AppData.GetUserCountry(ParkSettingActivity.this), AndroidVersion, AppData.GetUserPhoneEmail(ParkSettingActivity.this), AppData.GetUserLocation(ParkSettingActivity.this), devuid, handlerRespInfo);
    }

    private void ShowPostEmailActivity()
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{Global.companyEmailAddr});
        i.putExtra(Intent.EXTRA_SUBJECT, "Muddy Tyre - Contact Us");

        // Get the information to be sent
        String body = "";

        i.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No emails accounts set on the device. Please configure your email account and try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(ParkSettingActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(ParkSettingActivity.this).activityStop(this); // Add this method.
    }
}
