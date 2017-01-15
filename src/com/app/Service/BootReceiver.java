package com.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: KHM
 * Date: 14-2-11
 * Time: 下午11:27
 * To change this template use File | Settings | File Templates.
 */
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		PushNotificationService.m_ctxMain = context;
		Intent service = new Intent(context, PushNotificationService.class);
		context.startService(service);
	}
}
