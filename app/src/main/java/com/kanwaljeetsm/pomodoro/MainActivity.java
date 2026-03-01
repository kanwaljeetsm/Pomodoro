package com.kanwaljeetsm.pomodoro;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Context context = MainActivity.this;
    private ImageView appDrawLogo;
    private ConstraintLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            appDrawLogo = findViewById(R.id.appDrawLogo);
            main = findViewById(R.id.main);

            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDark = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

            if(isDark) {
                appDrawLogo.setImageDrawable(getDrawable(R.drawable.drawpomoblack));
                main.setBackgroundColor(getColor(R.color.black));
            } else {
                appDrawLogo.setImageDrawable(getDrawable(R.drawable.drawpomo));
                main.setBackgroundColor(getColor(R.color.white));
            }


            return insets;

        });
    }
}