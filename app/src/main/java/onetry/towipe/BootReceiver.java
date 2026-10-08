package onetry.towipe;

import android.app.*;
import android.app.admin.*;
import android.content.*;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {		     
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		if (!dpm.isProfileOwnerApp(context.getPackageName())) return;
		intent = new Intent(context, WatcherService.class);							           
		context.startForegroundService(intent);						        
    }
	
}
