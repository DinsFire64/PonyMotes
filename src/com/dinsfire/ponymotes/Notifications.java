package com.dinsfire.ponymotes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class Notifications {

    public final int DOWNLOAD_NOTIFICAITON_ID = 123;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Context context;

    @SuppressWarnings("unused")
    private PendingIntent pendingIntent;

    public Notifications(Context context, NotificationManager mNotifyManager, NotificationCompat.Builder mBuilder,
	    PendingIntent pendingIntent) {
	this.context = context;
	this.mNotifyManager = mNotifyManager;
	this.mBuilder = mBuilder;
	this.pendingIntent = pendingIntent;

	mBuilder.setContentIntent(pendingIntent);
    }

    public void disabledOnMobile() {
	mBuilder.setContentTitle(context.getString(R.string.notificationTitle))
		.setContentText(context.getString(R.string.syncingDisabled)).setSmallIcon(R.drawable.error)
		.setProgress(0, 0, false).setOngoing(false);
	publish();
    }

    public void downloadingDatabase() {
	mBuilder.setContentTitle(context.getString(R.string.notificationTitle))
		.setContentText(context.getString(R.string.downloadingNewDatabase))
		.setSmallIcon(android.R.drawable.ic_popup_sync).setProgress(0, 0, true).setOngoing(true);
	publish();
    }

    public void comparingDatabases() {
	mBuilder.setContentTitle(context.getString(R.string.notificationTitle))
		.setContentText(context.getString(R.string.comparingDatabases))
		.setSmallIcon(android.R.drawable.ic_popup_sync).setProgress(0, 0, true).setOngoing(true);
	publish();
    }

    public void connectionError() {
	mBuilder.setContentTitle(context.getString(R.string.notificationTitle)).setProgress(0, 0, false)
		.setContentText(context.getString(R.string.connectionError)).setSmallIcon(R.drawable.error)
		.setOngoing(false);
	publish();
    }

    public void upToDate() {
	mBuilder.setContentTitle(context.getString(R.string.notificationTitle)).setProgress(0, 0, false)
		.setContentText(context.getString(R.string.upToDate)).setSmallIcon(R.drawable.checkmark)
		.setOngoing(false);
	publish();
    }

    public void downloadingUpdate(int size, int progress, String emoteName) {

	mBuilder.setProgress(size - 1, progress, false);
	mBuilder.setContentText("(" + progress + "/" + (size) + ") " + emoteName)
		.setContentInfo("" + progress + "/" + (size)).setOngoing(true);
	publish();
    }

    private void publish() {
	mNotifyManager.notify(DOWNLOAD_NOTIFICAITON_ID, mBuilder.build());
    }
}
