package com.emreay.music.util;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ThemeUtil {

    public static void setDrawUnderStatusbar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.getDecorView().setSystemUiVisibility(
                    window.getDecorView().getSystemUiVisibility()
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static int getDrawUnderStatusbar(){
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setLightStatusbar(boolean enabled,Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            if (enabled) {
                decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(0);
            }
        }
    }

}
