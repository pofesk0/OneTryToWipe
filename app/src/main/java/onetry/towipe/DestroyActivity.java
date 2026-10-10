package onetry.towipe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class DestroyActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();	
		((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE)).wipeData(DevicePolicyManager.WIPE_SILENTLY);            
		finish();
    }
				
}
