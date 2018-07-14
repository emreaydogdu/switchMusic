package com.emreay.music.activites.activities.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.emreay.music.R;
import com.emreay.music.util.PreferenceUtil;
import com.emreay.music.util.ThemeUtil;

//import com.kabouzeid.appthemehelper.ATH;
//import com.kabouzeid.appthemehelper.ThemeStore;
//import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
//import com.kabouzeid.appthemehelper.util.ColorUtil;
//import com.kabouzeid.appthemehelper.util.MaterialDialogsUtil;

public abstract class ThemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme
        switch (PreferenceUtil.getThemeInt(this)) {
            case 1:
                setTheme(R.style.Theme_Phonograph_Light);
                setLightStatusbar(true);
                break;
            case 2:
                setTheme(R.style.Theme_Phonograph);
                setLightStatusbar(false);
                break;
            case 3:
                setTheme(R.style.Theme_Phonograph_Black);
                setLightStatusbar(false);
                break;
        }

        ThemeUtil.setDrawUnderStatusbar(getWindow());

        super.onCreate(savedInstanceState);
    }

    public void setLightStatusbar(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            if (enabled) {
                decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(ThemeUtil.getDrawUnderStatusbar());
            }
        }
    }

    public void setStatusbarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View statusBar = getWindow().getDecorView().getRootView().findViewById(R.id.status_bar);
            if (statusBar != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //statusBar.setBackgroundColor(ColorUtil.darkenColor(color));
                    setLightStatusbarAuto(color);
                } else {
                    statusBar.setBackgroundColor(color);
                }
            } else if (Build.VERSION.SDK_INT >= 21) {
                //getWindow().setStatusBarColor(ColorUtil.darkenColor(color));
                setLightStatusbarAuto(color);
            }
        }
    }

    public void setStatusbarColorAuto() {
        // we don't want to use statusbar color because we are doing the color darkening on our own to support KitKat
        //setStatusbarColor(ThemeStore.primaryColor(this));
    }

    public void setTaskDescriptionColor(@ColorInt int color) {
       // ATH.setTaskDescriptionColor(this, color);
    }

    public void setTaskDescriptionColorAuto() {
        //setTaskDescriptionColor(ThemeStore.primaryColor(this));
    }

    public void setNavigationbarColor(int color) {
       /* if (ThemeStore.coloredNavigationBar(this)) {
            ATH.setNavigationbarColor(this, color);
        } else {
            ATH.setNavigationbarColor(this, Color.BLACK);
        }*/
    }

    public void setNavigationbarColorAuto() {
        //setNavigationbarColor(ThemeStore.navigationBarColor(this));
    }

    public void setLightStatusbarAuto(int bgColor) {
        //setLightStatusbar(ColorUtil.isColorLight(bgColor));
    }

}