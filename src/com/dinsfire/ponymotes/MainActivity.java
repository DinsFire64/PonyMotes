package com.dinsfire.ponymotes;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NotificationCompat;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	
	private DatabaseController dbController;
	
	private PowerManager.WakeLock mWakeLock;
	
	public Notifications notifications;
	
	public static Prefrences prefs;
	
	public DatabaseController getDBController() {
		return dbController;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new SearchFragment()).commit();
		}
		
		Intent intent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		
		notifications = new Notifications(this, (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE),
											new NotificationCompat.Builder(this), pendingIntent);
   	 	
		PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		
		FileIO.doesDirectoryExistIfNotMakeIt();
 	
   	 	dbController = new DatabaseController(this);
   	 	
   	 	prefs = new Prefrences(PreferenceManager.getDefaultSharedPreferences(this));
   	 	PreferenceManager.setDefaultValues(this, R.xml.settings, false);
   	 	
   	 	onboardingSession();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		
		switch (item.getItemId()) {
	        case R.id.action_updateDatabase:
	        	updateDatabase();
	            return true;
	        case R.id.action_settings:
	            openSettings();
	            return true;
	        case R.id.action_help:
	        	openHelp();
	            return true;
	        case R.id.action_about:
	        	openAbout();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	public void openHelp() {
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra("url", "http://dinsfire.com/projects/reddit-emotes/docs/help.htm");
    	intent.putExtra("title", "Help");
    	startActivity(intent);
	}
	
	public void openAbout() {
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra("url", "http://dinsfire.com/projects/reddit-emotes/docs/about.htm");
    	intent.putExtra("title", "About");
    	startActivity(intent);
	}
	
	public void openSettings() {
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			startActivity(new Intent(this, FragmentPreferences.class));
		} else{
			Intent i = new Intent(getApplicationContext(), RegularPreferences.class);
            startActivityForResult(i, 1);
		}
		
	}

	public void updateDatabase() {
		
		if(!Download.isConnectedToMobile(this.getApplicationContext()) || !prefs.disableOnMobile()) {
			new DownloadCompareDatabases(MainActivity.this).execute(dbController);
		} else {
			notifications.disabledOnMobile();
		}
	}
	
	
	
	private class DownloadCompareDatabases extends AsyncTask<Object, Void, ArrayList<String>> {
		
		private Context context;
	    private DatabaseController serverDBController;
	    
	    ArrayList<String> emotesToDownload;
	    
	    public DownloadCompareDatabases(Context context) {
	        this.context = context;
	    }
	    
	    @Override
        protected void onPreExecute() {
	    	
	    	Download.setUserAgent(getPackageManager());
	    	
	    	mWakeLock.acquire();
            
            notifications.downloadingDatabase();
        }
		
	    @Override
	    protected ArrayList<String> doInBackground(Object... params) {
        	
        	dbController = (DatabaseController)params[0];
        	
        	
        	if(!Download.from(EmoteDownloadService.SERVER_DB_URL, context.getCacheDir().toString() + "/" + DatabaseOpenHelper.SERVER_DATABASE_NAME)) {
		        
		        serverDBController = new DatabaseController(context, context.getCacheDir().toString() + "/" + DatabaseOpenHelper.SERVER_DATABASE_NAME);
		        //System.out.println(serverDBController.getNumberOfRows());
		        
		        //Get prefs for syncing before sycning
		        notifications.comparingDatabases();
		        
		        dbController.open();
		        
		        emotesToDownload = serverDBController.getDifferences(prefs.wantToDownloadEmotes(),
		        													 prefs.wantToDownloadMissingEmotes(),
		        													 prefs.includeNSFW(), 
		        													 dbController);
		       
		        
		        
		        dbController.close();
		        
		        serverDBController.close();
        	}
	        
	        return emotesToDownload;
	    }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
        	mWakeLock.release();
        	downloadNewEmotes(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
        
    }
	
	public void downloadNewEmotes(ArrayList<String> emotesToDownload) {
		
        
		if(emotesToDownload == null)  {

			notifications.connectionError();
        	
        } else if(emotesToDownload.size() == 0) {
        	
        	//Toast.makeText(this, "There are no new emotes to download.", Toast.LENGTH_LONG).show();
        	notifications.upToDate();
        	
        	
        } else {

			mWakeLock.acquire();
			
			Intent intent = new Intent(this, EmoteDownloadService.class);
			//intent.putExtra("listOfEmotes", new EmoteWrapper(emotesToDownload));
			intent.putExtra("listOfEmotes", emotesToDownload);
			intent.putExtra("receiver", new EmoteDownloadReciever(new Handler()));
			this.startService(intent);
			
			//Toast.makeText(this, "Starting download", Toast.LENGTH_SHORT).show();
        }
	}
	
	public void onboardingSession() {
		if(prefs.onboardingSession()) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setMessage(R.string.onboardingMessage)
				.setTitle(R.string.onboardingTitle);
			
			builder.setPositiveButton(R.string.onboardingOk, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   updateDatabase();
		        	   openHelp();
		           }
		       });
			
			builder.setNegativeButton(R.string.onboardingNo, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               //updateDatabase();
		           }
		       });
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
			//new AlertDialog.Builder(this.getActivity()).setTitle("First Run").setMessage("This only pops up once").setNeutralButton("OK", null).show();
			prefs.turnOnboardingSessionOff();
		}
	}
	
	private class EmoteDownloadReciever extends ResultReceiver{
	    public EmoteDownloadReciever(Handler handler) {
	        super(handler);
	    }

	    @Override
	    protected void onReceiveResult(int resultCode, Bundle resultData) {
	        super.onReceiveResult(resultCode, resultData);

	        if (resultCode == EmoteDownloadService.ERROR) {
	        	
	        	notifications.connectionError();
	        	
	        	if(mWakeLock.isHeld())
	        		mWakeLock.release();
	        	
	        } else if (resultCode == EmoteDownloadService.UPDATE_PROGRESS) {
	            
	        	//Pull the data of what just got downloaded
	        	int progress = resultData.getInt("progress");
	            int size = resultData.getInt("size");
	            String emoteName = resultData.getString("emoteName");

	            //Update the progress bar
	            if (progress == size) {
	            	
	            	notifications.upToDate();
	            	mWakeLock.release();
	            
	            } else {
	            	
	            	notifications.downloadingUpdate(size, progress, emoteName);
	            	
	            }
	            
	            
	        }
	    }
}
	
	

	

	

}
