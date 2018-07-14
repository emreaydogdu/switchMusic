package com.emreay.music.activites.activities.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.emreay.music.LocalHelper;
import com.emreay.music.R;
import com.emreay.music.util.PreferenceUtil;
import com.emreay.music.views.VolumeProgress;
import com.kabouzeid.appthemehelper.ThemeStore;

public abstract class BaseActivity extends ThemeActivity {

    public static final int PERMISSION_REQUEST = 100;

    private boolean hadPermissions;
    private String[] permissions;
    private String permissionDeniedMessage;

    Drawable d;
    ProgressBar mProgressBar;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        permissions = getPermissionsToRequest();
        hadPermissions = hasPermissions();

        setPermissionDeniedMessage(null);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!hasPermissions()) {
            requestPermissions();
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalHelper.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final boolean hasPermissions = hasPermissions();
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHasPermissionsChanged(hasPermissions);
            }
        }
    }

    protected void onHasPermissionsChanged(boolean hasPermissions) {
        // implemented by sub classes
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
            showOverflowMenu();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    protected void showOverflowMenu() {

    }

    @Nullable
    protected String[] getPermissionsToRequest() {
        return null;
    }

    protected View getSnackBarContainer() {
        return getWindow().getDecorView();
    }

    protected void setPermissionDeniedMessage(String message) {
        permissionDeniedMessage = message;
    }

    private String getPermissionDeniedMessage() {
        return permissionDeniedMessage == null ? getString(R.string.permissions_denied) : permissionDeniedMessage;
    }

    protected void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions, PERMISSION_REQUEST);
        }
    }

    protected boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //User has deny from permission dialog
                        Snackbar.make(getSnackBarContainer(), getPermissionDeniedMessage(),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.action_grant, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        requestPermissions();
                                    }
                                })
                                .setActionTextColor(ThemeStore.accentColor(this))
                                .show();
                    } else {
                        // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                        Snackbar.make(getSnackBarContainer(), getPermissionDeniedMessage(),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.action_settings, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", BaseActivity.this.getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                })
                                .setActionTextColor(ThemeStore.accentColor(this))
                                .show();
                    }
                    return;
                }
            }
            hadPermissions = true;
            onHasPermissionsChanged(true);
        }
    }


    public void setVolume(){
        ConstraintLayout container = findViewById(R.id.root);
        mProgressBar = new ProgressBar(this,null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.WRAP_CONTENT));
        switch (PreferenceUtil.getThemeInt(this)) {
            case 1:
                d = new VolumeProgress(R.color.volumeForegroundLight,R.color.volumeBackgroundLight);
                break;
            case 2:
            case 3:
                d = new VolumeProgress(R.color.volumeForegroundDark,R.color.volumeBackgroundDark);
                break;
        }
        mProgressBar.setProgressDrawable(d);
        mProgressBar.setVisibility(View.GONE);
        container.addView(mProgressBar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        final View decorView = this.getWindow().getDecorView();
        runnable = new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            }
        };

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:

                handler.removeCallbacksAndMessages(null);
                //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mProgressBar.setVisibility(View.VISIBLE);
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                mProgressBar.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                handler.postDelayed(runnable, 3000);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                handler.removeCallbacksAndMessages(null);
                //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mProgressBar.setVisibility(View.VISIBLE);
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                mProgressBar.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                handler.postDelayed(runnable, 3000);

                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

}
