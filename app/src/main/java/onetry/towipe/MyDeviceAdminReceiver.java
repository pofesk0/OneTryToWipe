package onetry.towipe;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

	@Override
	public void onPasswordChanged(Context context, Intent intent, UserHandle user) {
    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    if (dpm.isProfileOwnerApp(context.getPackageName()) && user.equals(Process.myUserHandle())) {

        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();		
		setAlias(pm, packageName, "LauncherWorkAlias", false);
        setAlias(pm, packageName, "LauncherFinishAlias", false);
	
    } }
	
	@Override
	public void onEnabled(Context context, Intent intent) {
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName admin = new ComponentName(context, MyDeviceAdminReceiver.class);
		dpm.setMaximumFailedPasswordsForWipe(admin, 1);
	}

	private void setAlias(PackageManager pm, String packageName, String aliasName, boolean enabled) {
        ComponentName componentName = new ComponentName(packageName, packageName + "." + aliasName);
        pm.setComponentEnabledSetting(
                componentName,
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(context, MyDeviceAdminReceiver.class);      
		
		PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
		setAlias(pm, packageName, "LauncherAlias", false);
		setAlias(pm, packageName, "LauncherWorkAlias", true);

		dpm.addUserRestriction(admin, UserManager.DISALLOW_AUTOFILL);			
		dpm.addUserRestriction(admin, UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE);  			
		dpm.addUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH_SHARING); 
		
		dpm.clearUserRestriction(admin, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);	
		dpm.clearUserRestriction(admin, UserManager.DISALLOW_INSTALL_APPS);		
		dpm.clearUserRestriction(admin, UserManager.DISALLOW_UNINSTALL_APPS);					
		dpm.clearUserRestriction(admin, UserManager.DISALLOW_MODIFY_ACCOUNTS);					
		
		dpm.setScreenCaptureDisabled(admin, true);
		dpm.setBackupServiceEnabled(admin, false);						
				
		dpm.setPasswordQuality(admin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
		dpm.setPasswordMinimumLength(admin, 12);
		dpm.setKeyguardDisabledFeatures(admin, DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_ALL);																						    								
                
		dpm.setMaximumFailedPasswordsForWipe(admin, 1);
		dpm.setProfileEnabled(admin);

		int systemFlags = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
		for (PackageInfo pkg : pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)) {
			String pkgName = pkg.packageName;    
			if (pkgName.equals(context.getPackageName())) continue;             
			if (pkg.applicationInfo != null && (pkg.applicationInfo.flags & systemFlags) != 0) {        
				Intent bIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://")).addCategory(Intent.CATEGORY_BROWSABLE).setPackage(pkgName);                    
				if (!pm.queryIntentActivities(bIntent, PackageManager.MATCH_UNINSTALLED_PACKAGES).isEmpty()) {           
					try {              
						dpm.enableSystemApp(admin, pkgName);
            		} catch (Throwable ignored) {}       
				}    
			}
		}
		
	}
}
