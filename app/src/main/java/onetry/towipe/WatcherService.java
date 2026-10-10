package onetry.towipe;

import android.app.*;
import android.os.storage.*;
import java.util.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;

public class WatcherService extends DeviceAdminService {
    
	private BroadcastReceiver receiver;    
	
	private void startForegroundService() {
	Context context = this;
    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	Intent intent = new Intent(context, DestroyActivity.class); 
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

	DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);    
	if (dpm.getPermissionGrantState(new ComponentName(this, MyDeviceAdminReceiver.class), context.getPackageName(), android.Manifest.permission.POST_NOTIFICATIONS) != DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED) dpm.setPermissionGrantState(new ComponentName(this, MyDeviceAdminReceiver.class), getPackageName(), android.Manifest.permission.POST_NOTIFICATIONS, DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);	
		
    List<NotificationChannel> channels = nm.getNotificationChannels();
    String activeId = null;
    boolean needNew = false;

    for (NotificationChannel ch : channels) {
        if (ch.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            nm.deleteNotificationChannel(ch.getId());
            needNew = true;
        } else if (activeId == null) {
            activeId = ch.getId();
        }
    }
	
    if (needNew || activeId == null) {
        activeId = "work" + Long.toHexString(new java.security.SecureRandom().nextLong());
        NotificationChannel nch = new NotificationChannel(activeId, " ", NotificationManager.IMPORTANCE_DEFAULT);
        nch.setSound(null, null);
		nch.setLockscreenVisibility(Notification.VISIBILITY_SECRET);		
		nch.enableVibration(false);
		nm.createNotificationChannel(nch);
    }

	boolean isRu = Locale.getDefault().getLanguage().equals("ru");

    Notification notif = new Notification.Builder(context, activeId)
            .setContentTitle(isRu ? "Рабочий профиль запущен" : "Work Profile Started")
            .setContentText(isRu ? "Нажмите чтобы удалить" : "Tap to delete it")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
		    .setContentIntent(pendingIntent)
		    .setVisibility(Notification.VISIBILITY_SECRET)
	        .setAutoCancel(false)
            .build();

    if (android.os.Build.VERSION.SDK_INT >= 34) {
        startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED);
    } else {
        startForeground(1, notif);
    }
	}

	private final void forceBindAndStart() {
    Intent intent = new Intent(this, HelperService.class);
    bindService(intent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
    try {startService(intent);} 
    catch (Throwable t) {}
    }
    
    private final ServiceConnection connection = new ServiceConnection() {
        @Override public final void onServiceConnected(ComponentName name, IBinder service) {}
        @Override
        public final void onServiceDisconnected(ComponentName name) {
            forceBindAndStart();
        }
    };
    	    
      @Override
    public void onCreate() {
        super.onCreate();
		
		forceBindAndStart();		        
		startForegroundService();
        
       if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    					
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);                    
					ComponentName admin = new ComponentName(WatcherService.this, MyDeviceAdminReceiver.class);      
					if (!dpm.isUsingUnifiedPassword(admin)) dpm.lockNow(1);							                										    
                    
                }
            };

           IntentFilter filter = new IntentFilter();
           filter.addAction(Intent.ACTION_SCREEN_OFF);
           if (Build.VERSION.SDK_INT >= 33) {
                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(receiver, filter);
            }
        }

        
    }

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {    
	startForegroundService();
	return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            try { unregisterReceiver(receiver); } catch (Exception ignored) {}
            receiver = null;
        }		
        super.onDestroy();
    }
    
}
