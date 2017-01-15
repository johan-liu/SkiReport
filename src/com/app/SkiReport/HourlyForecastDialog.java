package com.app.SkiReport;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.*;
import com.app.Comm.CommMgr;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STHourlyInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.ResolutionSet;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MaHanYong on 14-2-20.
 */
public class HourlyForecastDialog extends Dialog {
	Context mContext;
    private int park_id = -1;
    private ProgressDialog progMainDialog;
    private JsonHttpResponseHandler handlerRespInfo = null;

    public HourlyForecastDialog(Context context, int layout, int parkid)
    {
        super(context, layout);
    	mContext = context;
        park_id = parkid;
        SetView(R.layout.parkforecast);

    }

    @Override
    public void onStart() {
        super.onStart();

        RunBackGround();
    }

    public void SetView(int layoutName)
    {
        super.setContentView(layoutName);
        ResolutionSet._instance.iterateChild(findViewById(R.id.RLParkHourlyMain));
        
        Button btnClose = (Button)findViewById(R.id.btnOK);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }

        });
    }

    private void RunBackGround()
    {
        int userid;
        userid = AppData.GetUserId(mContext);
        if (userid == -1)
            dismiss();

        handlerRespInfo = new JsonHttpResponseHandler()
        {
            int retval = 0;
            @Override
            public void onSuccess(JSONObject result) {
                CommMgr _commMgr = new CommMgr();
                ArrayList<STHourlyInfo> hrList = _commMgr.ParseHourlyListFromJsonString(result);

                ShowHourlyInfo(hrList);

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
                    Toast.makeText(mContext, mContext.getString(R.string.msg_servererror), Toast.LENGTH_SHORT).show();
                }

                retval = 0;
            }
        };

        progMainDialog = ProgressDialog.show(
                mContext,
                "",
                mContext.getString(R.string.msg_waiting),
                true,
                true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                }
        );

        CommMgr _comMgr = new CommMgr();
        _comMgr.GetHourlyWeather(Integer.toString(park_id), handlerRespInfo);
    }

    public void ShowHourlyInfo(ArrayList<STHourlyInfo> hrList)
    {
        try
        {
            RelativeLayout rlNext1 = (RelativeLayout)findViewById(R.id.RLNext1);
            SetHourlyDetail(rlNext1, hrList.get(0), (TextView)findViewById(R.id.txtNext1Str), (ImageView)findViewById(R.id.imgNext1Icon));

            RelativeLayout rlNext2 = (RelativeLayout)findViewById(R.id.RLNext2);
            SetHourlyDetail(rlNext2, hrList.get(1), (TextView)findViewById(R.id.txtNext2Str), (ImageView)findViewById(R.id.imgNext2Icon));

            RelativeLayout rlNext3 = (RelativeLayout)findViewById(R.id.RLNext3);
            SetHourlyDetail(rlNext3, hrList.get(2), (TextView)findViewById(R.id.txtNext3Str), (ImageView)findViewById(R.id.imgNext3Icon));

            RelativeLayout rlNext4 = (RelativeLayout)findViewById(R.id.RLNext4);
            SetHourlyDetail(rlNext4, hrList.get(3), (TextView)findViewById(R.id.txtNext4Str), (ImageView)findViewById(R.id.imgNext4Icon));

            RelativeLayout rlNext5 = (RelativeLayout)findViewById(R.id.RLNext5);
            SetHourlyDetail(rlNext5, hrList.get(4), (TextView)findViewById(R.id.txtNext5Str), (ImageView)findViewById(R.id.imgNext5Icon));

            RelativeLayout rlNext6 = (RelativeLayout)findViewById(R.id.RLNext6);
            SetHourlyDetail(rlNext6, hrList.get(5), (TextView)findViewById(R.id.txtNext6Str), (ImageView)findViewById(R.id.imgNext6Icon));

            RelativeLayout rlNext7 = (RelativeLayout)findViewById(R.id.RLNext7);
            SetHourlyDetail(rlNext7, hrList.get(6), (TextView)findViewById(R.id.txtNext7Str), (ImageView)findViewById(R.id.imgNext7Icon));

            RelativeLayout rlNext8 = (RelativeLayout)findViewById(R.id.RLNext8);
            SetHourlyDetail(rlNext8, hrList.get(7), (TextView)findViewById(R.id.txtNext8Str), (ImageView)findViewById(R.id.imgNext8Icon));

            RelativeLayout rlNext9 = (RelativeLayout)findViewById(R.id.RLNext9);
            SetHourlyDetail(rlNext9, hrList.get(8), (TextView)findViewById(R.id.txtNext9Str), (ImageView)findViewById(R.id.imgNext9Icon));

            RelativeLayout rlNext10 = (RelativeLayout)findViewById(R.id.RLNext10);
            SetHourlyDetail(rlNext10, hrList.get(9), (TextView)findViewById(R.id.txtNext10Str), (ImageView)findViewById(R.id.imgNext10Icon));

            RelativeLayout rlNext11 = (RelativeLayout)findViewById(R.id.RLNext11);
            SetHourlyDetail(rlNext11, hrList.get(10), (TextView)findViewById(R.id.txtNext11Str), (ImageView)findViewById(R.id.imgNext11Icon));

            RelativeLayout rlNext12 = (RelativeLayout)findViewById(R.id.RLNext12);
            SetHourlyDetail(rlNext12, hrList.get(11), (TextView)findViewById(R.id.txtNext12Str), (ImageView)findViewById(R.id.imgNext12Icon));
        }
        catch (Exception e)
        {

        }
    }

    public void SetHourlyDetail(RelativeLayout rlLayout, STHourlyInfo info, TextView txtStr, ImageView imgIcon)
    {
        try
        {
            boolean bFahren = AppData.GetTemperState(mContext);
            String strTemp = "";
            int resourceId = -1;

            if (info.iconstate == -1)
                rlLayout.setVisibility(View.INVISIBLE);
            else
            {
                if (bFahren == false)
                    strTemp = Integer.toString((int) Global.ConvertFahrentoCelsius(info.temp)) + mContext.getString(R.string.park_tempc);
                else
                    strTemp = Integer.toString((int)info.temp) + mContext.getString(R.string.park_tempf);

                txtStr.setText(info.weatherStr + " " + strTemp);

                resourceId = Global.GetResourceIdFromState(info.iconstate);
                if (resourceId != -1)
                    imgIcon.setImageResource(resourceId);
            }
        }
        catch (Exception e)
        {

        }
    }
}
