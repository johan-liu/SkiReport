package com.app.SkiReport;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.*;
import com.app.Comm.CommMgr;
import com.app.HttpConn.JsonHttpResponseHandler;
import com.app.STInfo.STCommentInfo;
import com.app.STInfo.STHourlyInfo;
import com.app.STInfo.STParkInfo;
import com.app.Utils.AppData;
import com.app.Utils.Global;
import com.app.Utils.ResolutionSet;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MaHanYong on 14-2-20.
 */
public class CommentDialog extends Dialog {
	Context mContext;
    STCommentInfo commInfo;

    public CommentDialog(Context context, int layout, STCommentInfo commentInfo)
    {
        super(context, layout);
    	mContext = context;
        commInfo = commentInfo;
        SetView(R.layout.parkcommentdlg);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void SetView(int layoutName)
    {
        super.setContentView(layoutName);
        ResolutionSet._instance.iterateChild(findViewById(R.id.RLCommentDlgMain));

        RelativeLayout rlDlgMain = (RelativeLayout)findViewById(R.id.RLCommentDlgMain);
        rlDlgMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView txtName = (TextView)findViewById(R.id.txtName);
        //TextView txtCountry = (TextView)findViewById(R.id.txtCountry);
        TextView txtDate = (TextView)findViewById(R.id.txtDate);
        TextView txtComment = (TextView)findViewById(R.id.txtComment);

        txtName.setText(commInfo.userinfo);
        txtDate.setText(commInfo.datetime);
        txtComment.setText(commInfo.comment);
    }
}
