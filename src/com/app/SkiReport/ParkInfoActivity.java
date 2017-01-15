package com.app.SkiReport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.app.Comm.CommMgr;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STCommentInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.PDFTools;
import com.app.Utils.ResolutionSet;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ParkInfoActivity extends Activity {
    private static final String PDF_MIME_TYPE = "application/pdf";
    private ParkCommentItemAdapter mAdapter = null;

    private ProgressDialog progMainDialog;
    private JsonHttpResponseHandler handlerRespInfo = null;
    private JsonHttpResponseHandler handlerCommentRespInfo = null;
    private JsonHttpResponseHandler handlerPostCommentRespInfo = null;
    private JsonHttpResponseHandler handlerMyParkRespInfo = null;

    int uid = -1;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    ListView listComment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkinfo);

        uid = getIntent().getIntExtra("parkuid", -1);

        //ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkInfoMain));
        mainLayout = (RelativeLayout)findViewById(R.id.RLParkInfoMain);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkInfoMain));
                            bInitialized = true;
                        }
                    }
                }
        );

        listComment = (ListView)findViewById(R.id.listCommentsView);

        listComment.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listComment.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
        listComment.setDividerHeight(10);
        mAdapter = new ParkCommentItemAdapter(ParkInfoActivity.this, this.getApplicationContext());
        listComment.setAdapter(mAdapter);

        TextView txtSearch = (TextView)findViewById(R.id.lblSearch);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkInfoActivity.this, ParkSearchActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtSetting = (TextView)findViewById(R.id.lblSettings);
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkInfoActivity.this, ParkSettingActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        TextView txtEpic = (TextView)findViewById(R.id.lblEpicDirt);
        txtEpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkInfoActivity.this, EpicDirtActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        Button btnHourly = (Button)findViewById(R.id.btnHourlyFor);
        btnHourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HourlyForecastDialog dialog = new HourlyForecastDialog(ParkInfoActivity.this, R.style.NoTitleDialog, uid);
                dialog.show();
            }
        });

        Button btnPostComment = (Button)findViewById(R.id.btnPostComment);
        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                EditText ednComment = (EditText)findViewById(R.id.ednTxtComment);
                String strComment = ednComment.getText().toString();
                if (strComment.equals(""))
                {
                    Toast.makeText(ParkInfoActivity.this, "Please Input Comment", Toast.LENGTH_SHORT).show();
                    return;
                }

                CommMgr _comMgr = new CommMgr();
                _comMgr.PostComment(Integer.toString(AppData.GetUserId(ParkInfoActivity.this)), Integer.toString(uid), strComment, handlerPostCommentRespInfo);

                progMainDialog = ProgressDialog.show(
                        ParkInfoActivity.this,
                        "",
                        getString(R.string.msg_waiting),
                        true,
                        true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        }
                );
            }
        });

        final EditText edittext = (EditText) findViewById(R.id.ednTxtComment);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideSoftKeyboard();
                    EditText ednComment = (EditText)findViewById(R.id.ednTxtComment);
                    String strComment = ednComment.getText().toString();
                    if (strComment.equals(""))
                    {
                        Toast.makeText(ParkInfoActivity.this, "Please Input Comment", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    CommMgr _comMgr = new CommMgr();
                    _comMgr.PostComment(Integer.toString(AppData.GetUserId(ParkInfoActivity.this)), Integer.toString(uid), strComment, handlerPostCommentRespInfo);

                    progMainDialog = ProgressDialog.show(
                            ParkInfoActivity.this,
                            "",
                            getString(R.string.msg_waiting),
                            true,
                            true,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            }
                    );
                    return true;
                }
                return false;
            }
        });

        RunBackGround();
    }


    public void hideSoftKeyboard() {
        View viewComment = findViewById(R.id.ednTxtComment);

        if(viewComment!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewComment.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
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
        int userid;
        userid = AppData.GetUserId(ParkInfoActivity.this);
        if (userid == -1 || uid == -1)
            finish();

        handlerRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                STParkInfo parkInfo = _commMgr.ParseParkFromJsonString(result);

                InitUIObject(parkInfo);
                retval = 1;

                _commMgr.GetLatestComments(Integer.toString(uid), handlerCommentRespInfo);
            }

            @Override
            public void onFailure(Throwable ex, String exception){
            }

            @Override
            public void onFinish()
            {
                if (retval == 0)
                {
                    progMainDialog.dismiss();

                    Toast.makeText(getApplicationContext(), getString(R.string.msg_servererror), Toast.LENGTH_SHORT).show();
                }

                retval = 0;
            }
        };

        handlerCommentRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                ArrayList<STCommentInfo> commentInfo = _commMgr.ParseCommentsFromJsonString(result);

                InitComment(commentInfo);
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

        handlerPostCommentRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                boolean bRet = _commMgr.ParseCommentResultFromJsonString(result);
                if (bRet == true)
                {
                    retval = 1;
                    Toast.makeText(ParkInfoActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();
                    EditText ednComment = (EditText)findViewById(R.id.ednTxtComment);
                    ednComment.setText("");
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

        handlerMyParkRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                boolean bResult = _commMgr.ParseAddParkResultFromJSONResult(result);
                if (bResult)
                {
                    ChangeMyParkSetting();
                    retval = 1;
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
                ParkInfoActivity.this,
                "",
                getString(R.string.msg_waiting),
                true,
                true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }
        );

        CommMgr _comMgr = new CommMgr();
        _comMgr.GetParksWithId(Integer.toString(userid), Integer.toString(uid), handlerRespInfo);

    }

    private void InitUIObject(STParkInfo info)
    {
        if (info == null)
            return;

        try
        {
            TextView txtName = (TextView)findViewById(R.id.txtParkName);
            txtName.setText(info.name);

            String strTemp = "";
            boolean bFahren = AppData.GetTemperState(ParkInfoActivity.this);
            if (bFahren == false)
                strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.curTemp)) + getString(R.string.park_tempc);
            else
                strTemp = Integer.toString((int)info.curTemp) + getString(R.string.park_tempf);

            ((TextView)findViewById(R.id.txtParkWeatherStr)).setText(info.weatherStr + " " + strTemp);

            int resourceId = Global.GetResourceIdFromState(info.iconstate);
            if (resourceId != -1)
                ((ImageView)findViewById(R.id.imgParkWeather)).setImageResource(resourceId);

            int dirtresourceId = Global.GetDirtResourceIdFromState(info.icondirt);
            if (dirtresourceId != -1)
                ((ImageView)findViewById(R.id.imgParkState)).setImageResource(dirtresourceId);

            // Show past3, next3 days weather
            RelativeLayout rlBefore1 = (RelativeLayout)findViewById(R.id.RLPast1Weather);
            if (info.before1State == -1)
                rlBefore1.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.before1fHigh)) + getString(R.string.park_tempc) + "/" + Integer.toString((int) Global.ConvertFahrentoCelsius(info.before1fLow)) + getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.before1fHigh) + getString(R.string.park_tempf) + "/" + Integer.toString((int)info.before1fLow) + getString(R.string.park_tempf);

                ((TextView)findViewById(R.id.txtPast1Str)).setText(info.before1weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.before1State);
                if (resourceId != -1)
                    ((ImageView)findViewById(R.id.imgPast1Icon)).setImageResource(resourceId);
            }

            RelativeLayout rlBefore2 = (RelativeLayout)findViewById(R.id.RLPast2Weather);
            if (info.before2State == -1)
                rlBefore2.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.before2fHigh)) + getString(R.string.park_tempc) + "/" + Integer.toString((int) Global.ConvertFahrentoCelsius(info.before2fLow)) + getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.before2fHigh) + getString(R.string.park_tempf) + "/" + Integer.toString((int)info.before2fLow) + getString(R.string.park_tempf);

                ((TextView)findViewById(R.id.txtPast2Str)).setText(info.before2weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.before2State);
                if (resourceId != -1)
                    ((ImageView)findViewById(R.id.imgPast2Icon)).setImageResource(resourceId);
            }

            RelativeLayout rlBefore3 = (RelativeLayout)findViewById(R.id.RLPast3Weather);
            if (info.before3State == -1)
                rlBefore3.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.before3fHigh)) + getString(R.string.park_tempc) + "/" + Integer.toString((int) Global.ConvertFahrentoCelsius(info.before3fLow)) + getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.before3fHigh) + getString(R.string.park_tempf) + "/" + Integer.toString((int)info.before3fLow) + getString(R.string.park_tempf);

                ((TextView)findViewById(R.id.txtPast3Str)).setText(info.before3weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.before3State);
                if (resourceId != -1)
                    ((ImageView)findViewById(R.id.imgPast3Icon)).setImageResource(resourceId);
            }

            RelativeLayout rlNext1 = (RelativeLayout)findViewById(R.id.RLNext1Weather);
            if (info.after1State == -1)
                rlNext1.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.after1fHigh)) + getString(R.string.park_tempc) + "/" + Integer.toString((int) Global.ConvertFahrentoCelsius(info.after1fLow)) + getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.after1fHigh) + getString(R.string.park_tempf) + "/" + Integer.toString((int)info.after1fLow) + getString(R.string.park_tempf);

                ((TextView)findViewById(R.id.txtNext1Str)).setText(info.after1weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.after1State);
                if (resourceId != -1)
                    ((ImageView)findViewById(R.id.imgNext1Icon)).setImageResource(resourceId);
            }

            RelativeLayout rlNext2 = (RelativeLayout)findViewById(R.id.RLNext2Weather);
            if (info.after2State == -1)
                rlNext2.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.after2fHigh)) + getString(R.string.park_tempc) + "/" + Integer.toString((int) Global.ConvertFahrentoCelsius(info.after2fLow)) + getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.after2fHigh) + getString(R.string.park_tempf) + "/" + Integer.toString((int)info.after2fLow) + getString(R.string.park_tempf);

                ((TextView)findViewById(R.id.txtNext2Str)).setText(info.after2weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.after2State);
                if (resourceId != -1)
                    ((ImageView)findViewById(R.id.imgNext2Icon)).setImageResource(resourceId);
            }

            RelativeLayout rlNext3 = (RelativeLayout)findViewById(R.id.RLNext3Weather);
            if (info.after3State == -1)
                rlNext3.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.after3fHigh)) + getString(R.string.park_tempc) + "/" + Integer.toString((int) Global.ConvertFahrentoCelsius(info.after3fLow)) + getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.after3fHigh) + getString(R.string.park_tempf) + "/" + Integer.toString((int)info.after3fLow) + getString(R.string.park_tempf);

                ((TextView)findViewById(R.id.txtNext3Str)).setText(info.after3weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.after3State);
                if (resourceId != -1)
                    ((ImageView)findViewById(R.id.imgNext3Icon)).setImageResource(resourceId);
            }

            TextView txtVertical = (TextView)findViewById(R.id.txtVertical);
            txtVertical.setText(Integer.toString(info.vertical) + "(Feet)\n" + Integer.toString(info.meters) + "(Meters)");

            TextView txtAddress = (TextView)findViewById(R.id.txtAddress);
            String fulladdr = info.address + ", " + info.city + ", " + info.province + ", " + info.country;
            String geolocation = "geo:0,0?q=" + (Double.toString(info.latitude) + ", " + Double.toString(info.longitude));
            txtAddress.setText(fulladdr);
            //txtAddress.setTag(fulladdr);
            txtAddress.setTag(geolocation);

            txtAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse((String)view.getTag()));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txtPhoneNum= (TextView)findViewById(R.id.txtPhoneNum);
            txtPhoneNum.setText(info.phone);

            TextView txtOpen = (TextView)findViewById(R.id.txtOpen);
            txtOpen.setText(info.open_months);

            TextView txtCost = (TextView)findViewById(R.id.txtCost);
            txtCost.setText(info.dailiy_cost);

            TextView txtRental = (TextView)findViewById(R.id.txtRental);
            txtRental.setText(info.rental);

            TextView txtBikeShop = (TextView)findViewById(R.id.txtBikeShop);
            txtBikeShop.setText(info.shop);

            TextView txtAddMyPark = (TextView)findViewById(R.id.txtAddParks);
            ImageView imgAddMyPark = (ImageView)findViewById(R.id.imgAddPark);

            if (info.isMyPark)
            {
                txtAddMyPark.setText(getString(R.string.park_removeparks));
                imgAddMyPark.setImageResource(R.drawable.removepark);
            }
            else
            {
                txtAddMyPark.setText(getString(R.string.park_addparks));
                imgAddMyPark.setImageResource(R.drawable.addpark);
            }

            imgAddMyPark.setTag(info.isMyPark);
            imgAddMyPark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Boolean bFlag = (Boolean)view.getTag();
                    int userid;
                    userid = AppData.GetUserId(ParkInfoActivity.this);

                    if (bFlag == true)
                    {
                        CommMgr _comMgr = new CommMgr();
                        _comMgr.RemoveParkFromMyParkList(Integer.toString(userid), Integer.toString(uid), handlerMyParkRespInfo);
                    }
                    else
                    {
                        CommMgr _comMgr = new CommMgr();
                        _comMgr.AddParktoMyParkList(Integer.toString(userid), Integer.toString(uid), handlerMyParkRespInfo);
                    }

                    progMainDialog = ProgressDialog.show(
                            ParkInfoActivity.this,
                            "",
                            getString(R.string.msg_waiting),
                            true,
                            true,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            }
                    );
                }
            });

            txtAddMyPark.setTag(info.isMyPark);
            txtAddMyPark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Boolean bFlag = (Boolean)view.getTag();
                    int userid;
                    userid = AppData.GetUserId(ParkInfoActivity.this);

                    if (bFlag == true)
                    {
                        CommMgr _comMgr = new CommMgr();
                        _comMgr.RemoveParkFromMyParkList(Integer.toString(userid), Integer.toString(uid), handlerMyParkRespInfo);
                    }
                    else
                    {
                        CommMgr _comMgr = new CommMgr();
                        _comMgr.AddParktoMyParkList(Integer.toString(userid), Integer.toString(uid), handlerMyParkRespInfo);
                    }

                    progMainDialog = ProgressDialog.show(
                            ParkInfoActivity.this,
                            "",
                            getString(R.string.msg_waiting),
                            true,
                            true,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            }
                    );
                }
            });

            ImageView imgFacebook = (ImageView)findViewById(R.id.imgFaceBook);
            imgFacebook.setTag(info.facebook);
            imgFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)view.getTag()));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            ImageView imgTwitter = (ImageView)findViewById(R.id.imgTwitter);
            imgTwitter.setTag(info.twitter);
            imgTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)view.getTag()));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            ImageView imgInstagram = (ImageView)findViewById(R.id.imgInstagram);
            imgInstagram.setTag(info.instagram);
            imgInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)view.getTag()));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            ImageView imgPinkBike = (ImageView)findViewById(R.id.imgPinkBike);
            imgPinkBike.setTag(info.pinkbike);
            imgPinkBike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)view.getTag()));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            Button btnTrailMap = (Button)findViewById(R.id.btnTrailMap);
            btnTrailMap.setTag(info.name);
            btnTrailMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pdfname = (String) view.getTag();
                    pdfname = pdfname.replace(" ", "%20");
                    String pdfUrl = Global.web_trailmap_pdf_base_uri + pdfname + Global.web_trailmap_default_extension;
                    new AsyncCaller().execute(pdfUrl);
                    //PDFTools.showPDFUrl(ParkInfoActivity.this, pdfUrl);
                    //openPDF(Global.web_trailmap_pdf_base_uri + (String) view.getTag() + Global.web_trailmap_default_extension);
                }
            });
        }
        catch (Exception e)
        {

        }

        // Get day of week
        String strpast1Week = Global.GetDayofWeekString(-1);
        TextView txtPast1Week = (TextView)findViewById(R.id.txtPast1Week);
        txtPast1Week.setText(strpast1Week);

        String strpast2Week = Global.GetDayofWeekString(-2);
        TextView txtPast2Week = (TextView)findViewById(R.id.txtPast2Week);
        txtPast2Week.setText(strpast2Week);

        String strpast3Week = Global.GetDayofWeekString(-3);
        TextView txtPast3Week = (TextView)findViewById(R.id.txtPast3Week);
        txtPast3Week.setText(strpast3Week);

        String strnext1Week = Global.GetDayofWeekString(1);
        TextView txtNext1Week = (TextView)findViewById(R.id.txtNext1Week);
        txtNext1Week.setText(strnext1Week);

        String strnext2Week = Global.GetDayofWeekString(2);
        TextView txtNext2Week = (TextView)findViewById(R.id.txtNext2Week);
        txtNext2Week.setText(strnext2Week);

        String strnext3Week = Global.GetDayofWeekString(3);
        TextView txtNext3Week = (TextView)findViewById(R.id.txtNext3Week);
        txtNext3Week.setText(strnext3Week);
    }

    private void InitComment(ArrayList<STCommentInfo> commentinfo)
    {
        mAdapter.setData(commentinfo);
        listComment.setAdapter(mAdapter);

        RelativeLayout.LayoutParams rlLayoutParam = (RelativeLayout.LayoutParams)listComment.getLayoutParams();
        rlLayoutParam.height = (int)(60 * ResolutionSet.fYpro * commentinfo.size());

        listComment.setLayoutParams(rlLayoutParam);
    }

    private void ChangeMyParkSetting()
    {
        TextView txtAddPark = (TextView)findViewById(R.id.txtAddParks);
        ImageView imgAddPark = (ImageView)findViewById(R.id.imgAddPark);
        Boolean bAdd = (Boolean)imgAddPark.getTag();
        bAdd = !bAdd;

        imgAddPark.setTag(bAdd);
        if (bAdd)
        {
            txtAddPark.setText(getString(R.string.park_removeparks));
            imgAddPark.setImageResource(R.drawable.removepark);
        }
        else
        {
            txtAddPark.setText(getString(R.string.park_addparks));
            imgAddPark.setImageResource(R.drawable.addpark);
        }
    }

    public void openPDF(String url ) {
        final String googleDocsUrl = "http://docs.google.com/viewer?url=";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(googleDocsUrl + url),"text/html");
        startActivity(intent);
        /*Intent i = new Intent( Intent.ACTION_VIEW );
        i.setDataAndType( localUri, PDF_MIME_TYPE );
        startActivity(i);*/
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(ParkInfoActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(ParkInfoActivity.this).activityStop(this); // Add this method.
    }

    private class AsyncCaller extends AsyncTask<String, Integer, String>
    {
        boolean bsuccess = false;
        Context mContext;
        ProgressDialog pdLoading = new ProgressDialog(ParkInfoActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bsuccess = false;
            mContext = ParkInfoActivity.this;
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading PDF...");
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);

            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... pdfUrl) {

            String downloadUrl = pdfUrl[0];
            String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
            String PATH = Environment.getExternalStorageDirectory() + "/skipdf_download/";
            // The place where the downloaded PDF file will be put
            final File tempFile = new File( Environment.getExternalStorageDirectory() + "/skipdf_download/", filename );
            if ( tempFile.exists()  ) {
                // If we have downloaded the file before, just go ahead and show it.
                bsuccess = true;
             }
            else
            {
                try
                {
                    URL url = new URL(downloadUrl);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, filename);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }
                    fos.close();
                    is.close();

                    bsuccess = true;
                }
                catch (Exception e)
                {
                    File file = new File(PATH);
                    File outputFile = new File(file, filename);
                    outputFile.delete();

                    filename = "";
                }
            }

            return filename;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("") == false)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/skipdf_download/", result)), "application/pdf");

                startActivity(intent);
            }

            pdLoading.dismiss();

            if (bsuccess == false)
            {
                File outputFile = new File(Environment.getExternalStorageDirectory() + "/skipdf_download/", result);
                outputFile.delete();
            }
        }

    }

    public void showCommentDialog(STCommentInfo commentInfo)
    {
        CommentDialog dialog = new CommentDialog(ParkInfoActivity.this, R.style.NoTitleDialog, commentInfo);
        dialog.show();
    }
}
