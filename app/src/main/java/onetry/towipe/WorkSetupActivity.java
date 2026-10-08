package onetry.towipe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class WorkSetupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);        
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);        
        
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
            description.setText("Привет, задайте отдельный пароль для рабочего профиля, если хотите.\n\n" +
                    "Если вы установите отдельный пароль, профиль будет перезагружен " +
                    "при выключении экрана. Это может повлиять на воспроизведение медиа " +
                    "и остановить процессы, но усилит безопасность. Рабочий профиль " +
                    "будет стёрт только если неверно ввести его пароль. Выбирайте этот " +
                    "вариант только если вы включили защиту для основного профиля " +
                    "(которая стирает и рабочий), чтобы у вас была возможность стереть " +
                    "данные прямо с экрана блокировки.");
        } else {
            description.setText("Hello, set a separate password for the work profile if you want.\n\n" +
                    "If you set a separate password, the profile will be rebooted " +
                    "when the screen is turned off. This may affect media playback " +
                    "and stop processes, but it will increase security. The work profile " +
                    "will only be erased if its password is entered incorrectly. Choose this " +
                    "option only if you have enabled protection for the main profile " +
                    "(which also erases the work profile), so that you have the ability to erase " +
                    "the data directly from the lock screen.");
        }

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        description.setLayoutParams(textParams);

        TextView description2 = new TextView(this);

        description2.setGravity(Gravity.CENTER);
        description2.setTextSize(16f);
        description2.setTextColor(Color.WHITE);

        if (language.equals("ru")) {
            description2.setText("Если вы не установите отдельный пароль, рабочий профиль будет " +
                    "стёрт при любой неверной попытке разблокировать экран основного " +
                    "профиля, вне зависимости от его защиты. Но он не будет перезагружен при выключении экрана.");
        } else {
            description2.setText("If you do not set a separate password, the work profile will be " +
                    "erased after any incorrect attempt to unlock the screen of the main " +
                    "profile, regardless of its protection. But it will not be rebooted when the screen is turned off.");
        }

        LinearLayout.LayoutParams textParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams2.setMargins(0, 48, 0, 0);
        
        description2.setLayoutParams(textParams2);

        LinearLayout buttonBox = new LinearLayout(this);

        buttonBox.setOrientation(LinearLayout.VERTICAL);
        buttonBox.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams buttonBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonBoxParams.setMargins(0, 64, 0, 0);
        buttonBox.setLayoutParams(buttonBoxParams);

        Button setPasswordButton = createStyledButton(
                language.equals("ru") ? "Установить отдельный пароль" : "Set a separate password"
        );

        setPasswordButton.setOnClickListener(v -> {

            Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            startActivity(intent);
        
        });

        Button noPasswordButton = createStyledButton(
                language.equals("ru") ? "Не ставить отдельный пароль" : "Do not set a separate password"
        );

        noPasswordButton.setOnClickListener(v -> finishAndRemoveTask());
        
        root.addView(description);
        root.addView(description2);

        buttonBox.addView(setPasswordButton);
        buttonBox.addView(noPasswordButton);

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
