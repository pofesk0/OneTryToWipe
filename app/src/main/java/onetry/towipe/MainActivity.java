package onetry.towipe;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import java.util.*;
import android.widget.*;
import android.view.*;
import android.view.inputmethod.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;

public class MainActivity extends Activity {
	
	private void showOnboarding() {
    final android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);
    android.view.Window window = dialog.getWindow();
    if (window != null) {
		window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);  
		window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);        		
		window.getDecorView().setKeepScreenOn(true);     
	}

    android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
    float scaleFactor = (float) Math.sqrt(dm.widthPixels * dm.heightPixels);
    float textPx = scaleFactor * 0.025f;
    int pX = (int) (dm.widthPixels * 0.08f);

    android.widget.LinearLayout root = new android.widget.LinearLayout(this);
    root.setOrientation(android.widget.LinearLayout.VERTICAL);
    root.setBackgroundColor(0xFFFFFFFF);

    android.widget.LinearLayout headerContainer = new android.widget.LinearLayout(this);
    headerContainer.setOrientation(android.widget.LinearLayout.VERTICAL);
    headerContainer.setBackgroundColor(0xFFFF0000);
    android.widget.LinearLayout.LayoutParams hParams = new android.widget.LinearLayout.LayoutParams(-1, 0, 1.0f);
    
    android.view.View spacer = new android.view.View(this);
    headerContainer.addView(spacer, new android.widget.LinearLayout.LayoutParams(-1, 0, 1.0f));

    android.widget.TextView titleTv = new android.widget.TextView(this);

    boolean isRussian = Locale.getDefault().getLanguage().equals("ru");

    titleTv.setText(isRussian
            ? "Создать рабочий профиль"
            : "Create a work profile");

    titleTv.setTextColor(0xFFFFFFFF);
    titleTv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, scaleFactor * 0.035f);
    titleTv.setPadding(pX, 0, pX, (int)(pX * 0.5f));
    headerContainer.addView(titleTv);
    
    root.addView(headerContainer, hParams);

    android.widget.ScrollView scroll = new android.widget.ScrollView(this);
    android.widget.LinearLayout.LayoutParams sParams = new android.widget.LinearLayout.LayoutParams(-1, 0, 2.0f);
    
    android.widget.TextView tv = new android.widget.TextView(this);
    tv.setPadding(pX, (int)(pX * 0.8f), pX, pX);
    tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textPx);
    tv.setTextColor(0xFF333333);
    tv.setLineSpacing(0, 1.2f);
	tv.setTypeface(null, android.graphics.Typeface.BOLD); 

    tv.setText(isRussian
            ? "Привет!\n" +
              "Здесь вы можете создать рабочий профиль\n" +
              "Просто нажмите START >\n"
            : "Hello!\n" +
              "Here you can create a work profile\n" +
              "Simply press START >\n");  
	
	scroll.addView(tv);
    root.addView(scroll, sParams);

    android.view.View divider = new android.view.View(this);
    divider.setBackgroundColor(0xFFDCDCDC);
    root.addView(divider, new android.widget.LinearLayout.LayoutParams(-1, 3));
		
    android.widget.RelativeLayout bottomBar = new android.widget.RelativeLayout(this);
    bottomBar.setBackgroundColor(0xFFF5F5F5);
    bottomBar.setPadding(pX, (int)(pX * 0.2f), pX, (int)(pX * 0.2f));

    android.widget.Button btn = new android.widget.Button(this);
    btn.setText("START >");
    btn.setTextColor(0xFF333333);
    btn.setBackgroundColor(0);
    btn.setTypeface(null, android.graphics.Typeface.BOLD);
    btn.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textPx * 0.9f);

    android.widget.RelativeLayout.LayoutParams btnParams = new android.widget.RelativeLayout.LayoutParams(-2, -2);
    btnParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
    bottomBar.addView(btn, btnParams);

    root.addView(bottomBar, new android.widget.LinearLayout.LayoutParams(-1, -2));

    btn.setOnClickListener(v -> {
        
		Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
		intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, new ComponentName(this, MyDeviceAdminReceiver.class));
		intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_ALLOW_OFFLINE, true);
		startActivityForResult(intent, 100);
		
        dialog.dismiss();
    });

    dialog.setContentView(root);
    dialog.setCancelable(false);
    dialog.show();
	}

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);  		
		super.onCreate(savedInstanceState);		
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);        
		getWindow().getDecorView().setKeepScreenOn(true);
        
        final TextView tv = new TextView(this);
        tv.setBackgroundColor(0xFF000000);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(120);
        tv.setGravity(17);
        setContentView(tv);
        getWindow().getDecorView().setSystemUiVisibility(5894);                															            		
			showOnboarding();							        
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   if (requestCode == 100) finish();
	}
}
