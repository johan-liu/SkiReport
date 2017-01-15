package com.app.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.ViewGroup;
import com.app.Comm.CommMgr;
import com.app.HttpConn.AsyncHttpResponseHandler;
import com.app.STInfo.STCommentInfo;
import com.app.SkiReport.ParkListActivity;
import com.app.SkiReport.R;
import com.app.SkiReport.SplashActivity;
import com.app.Utils.AppData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: KHM
 * Date: 14-2-11
 * Time: 下午11:29
 * To change this template use File | Settings | File Templates.
 */
public class PushNotificationService extends IntentService {
	public static Context m_ctxMain = null;

	public PushNotificationService() {
		super("");
	}

	public PushNotificationService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				while (true)
				{
					try
					{
                        if (m_ctxMain == null)
                            m_ctxMain = getApplicationContext();

						getPushNotificationInfo(m_ctxMain);
						Thread.sleep(1000);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}, 4000);
		return START_STICKY;
	}

	private void getPushNotificationInfo(Context context)
	{
        if (AppData.GetPushNotificationState(m_ctxMain) == true)
        {
            CommMgr _commMgr = new CommMgr();
            int lastupdatedid = AppData.GetLastUpdatedForPush(m_ctxMain);
            int userid = AppData.GetUserId(m_ctxMain);

            if (userid != -1)
                _commMgr.GetPushNotifications(Integer.toString(userid), Integer.toString(lastupdatedid), new_notify_handler);
        }
	}

	private AsyncHttpResponseHandler new_notify_handler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(String content) {
			super.onSuccess(content);    //To change body of overridden methods use File | Settings | File Templates.

			try {
				JSONObject jsonObject = new JSONObject(content);
				int nRetCode = jsonObject.getInt("ret");
				if (nRetCode == 0)
				{
					String msg = jsonObject.getString("message");
                    int lastupdateid = jsonObject.getInt("lastuid");
					checkData(msg, lastupdateid);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);    //To change body of overridden methods use File | Settings | File Templates.
		}
	};

	private void checkData(String msg, int lastupdateid)
	{
		try {
			showNotification(msg, lastupdateid);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void showNotification(String msg, int lastupdateid)
	{
		if (m_ctxMain == null || msg.isEmpty())
			return;

        int savedupdatedid = AppData.GetLastUpdatedForPush(m_ctxMain);
        if (savedupdatedid >= lastupdateid)
            return;

		AppData.SetLastUpdatedForPush(m_ctxMain, lastupdateid);

		int nIcon = R.drawable.ic_launcher;
		long nWhen = System.currentTimeMillis();

		int nID = AppData.GetNotifID(m_ctxMain) + 1;
		Intent notif_intent = new Intent(m_ctxMain, SplashActivity.class);
		notif_intent.putExtra("notification", nID);
		// set intent so it does not start a new activity
		notif_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent intent = PendingIntent.getActivity(m_ctxMain, nID, notif_intent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationManager notificationManager = (NotificationManager)m_ctxMain.getSystemService(Context.NOTIFICATION_SERVICE);

		String szMsg = msg;
		String szTitle = getResources().getString(R.string.app_name);

		//if (Build.VERSION.SDK_INT >= 11)
		//{
			/*Notification.Builder notif_builder = new Notification.Builder(m_ctxMain);
			notif_builder.setSmallIcon(nIcon);
			notif_builder.setContentTitle(szTitle);
			notif_builder.setContentText(szMsg);
			notif_builder.setWhen(nWhen);
			notif_builder.setContentIntent(intent);
			notif_builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			notif_builder.setAutoCancel(true);

			notificationManager.notify(nID, notif_builder.build());*/
		//}
		//else
		{
			Notification notif = new Notification(nIcon, szMsg, nWhen);
			notif.setLatestEventInfo(m_ctxMain, szTitle, szMsg, intent);
			//notif.flags += Notification.FLAG_NO_CLEAR;
			notificationManager.notify(nID, notif);
		}

		AppData.SetNotifID(m_ctxMain, nID);
	}

	private String getContent(String szName)
	{/*
		String szBracketOpen = getResources().getString(R.string.STR_BRACKET_OPEN);
		String szBracketClose = getResources().getString(R.string.STR_BRACKET_CLOSE);
		String szNewExist = getResources().getString(R.string.STR_NOTIF_MSG);

		return szBracketOpen + szName + szBracketClose + szNewExist;*/
        return "";
	}
}
