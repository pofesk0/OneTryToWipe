package onetry.towipe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;

public class HelloActivity extends Activity {

    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);     
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(64, 64, 64, 64);

        TextView description = new TextView(this);
        description.setGravity(Gravity.CENTER);
        description.setTextSize(16f);
        description.setTextColor(Color.WHITE);

        String language = Locale.getDefault().getLanguage();

        if (language.equals("ru")) {
            description.setText(
                    "Привет! Это простое приложение, которое сбросит данные " +
                    "при 1-й неверной попытке ввода пароля на устройстве.\n\n" +
                    "Внимание: Как правило, пароль засчитывается как неверный только если ввести более 4х символов. Если у вас телефон с предустановленными Google сервисами, " +
                    "то после сброса могут остаться следы от Google аккаунтов из-за " +
                    "механизма FRP. Чтобы этого не произошло, лучше перенести их " +
                    "в рабочий профиль и не оставить ни одного аккаунта в основном профиле."
            );
        } else {
            description.setText(
                    "Hello! This is a simple application that will reset data " +
                    "after the 1st incorrect password attempt on the device.\n\n" +
                    "Attention: As a rule, the password is counted as incorrect only if you enter more than 4 characters. If you have a phone with pre-installed Google services, " +
                    "then after the reset, traces of Google accounts may remain due to " +
                    "the FRP mechanism. To prevent this, it is better to move them " +
                    "to the work profile and leave no accounts in the main profile."
            );
        }

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        description.setLayoutParams(textParams);
        LinearLayout buttonBox = new LinearLayout(this);
        buttonBox.setOrientation(LinearLayout.VERTICAL);
        buttonBox.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams buttonBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonBoxParams.setMargins(0, 64, 0, 0);
        buttonBox.setLayoutParams(buttonBoxParams);

        Button protectButton = createStyledButton(
                language.equals("ru") ? "Защитить этот профиль" : "Protect this profile"
        );

        protectButton.setOnClickListener(v -> {

            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.setMaximumFailedPasswordsForWipe(adminComponent, 1);
                Toast.makeText(
                        HelloActivity.this,
                        language.equals("ru") ? "Защита уже активна" : "Protection is already active",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                intent.putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        language.equals("ru")
                                ? "Как только вы предоставите права администратора " +
                                  "приложение использует их, чтобы установить лимит " +
                                  "попыток разблокировки до сброса данных в 1 попытку."
                                : "Once you grant administrator privileges, " +
                                  "the application will use them to set the limit " +
                                  "of unlock attempts before data reset to 1 attempt."
                );
                startActivity(intent);
            }
            
        });

        Button workProfileButton = createStyledButton(
                language.equals("ru") ? "Создать рабочий профиль" : "Create a work profile"
        );

        workProfileButton.setOnClickListener(v -> {

            UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
            LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
            boolean managedProfileExists = false;

            if (userManager != null && launcherApps != null) {

                UserHandle currentUser = android.os.Process.myUserHandle();
                List<UserHandle> profiles = userManager.getUserProfiles();

                for (UserHandle user : profiles) {
                    
                    if (user.equals(currentUser)) {
                        continue;
                    }

                    List<LauncherActivityInfo> activities = launcherApps.getActivityList(null, user);

                    if (activities != null && !activities.isEmpty()) {
                        managedProfileExists = true;
                        break;
                    }
                    
                }
                
            }

            if (managedProfileExists) {
                Toast.makeText(
                        HelloActivity.this,
                        language.equals("ru")
                                ? "На этом устройстве уже есть рабочий профиль"
                                : "You already have a work profile on this device",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Intent intent = new Intent(HelloActivity.this, MainActivity.class);
                startActivity(intent);
            }
            
        });

        root.addView(description);
        buttonBox.addView(protectButton);
        buttonBox.addView(workProfileButton);
        root.addView(buttonBox);

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private Button createStyledButton(String text) {

        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16f);
        button.setGravity(Gravity.CENTER);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(Color.parseColor("#34495e"));
        shape.setCornerRadius(6f);

        button.setBackground(shape);
        button.setPadding(32, 32, 32, 32);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 16);

        button.setLayoutParams(params);
        return button;
    }
}
