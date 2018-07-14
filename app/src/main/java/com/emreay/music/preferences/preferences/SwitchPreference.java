package com.emreay.music.preferences.preferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.Preference;
import android.support.v4.app.TaskStackBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.emreay.music.R;
import com.emreay.music.activites.activities.MainActivity;
import com.emreay.music.util.PreferenceUtil;
import com.kabouzeid.appthemehelper.ThemeStore;

public class SwitchPreference extends Preference implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private View sharedView;
    private Switch switcher;

    public SwitchPreference(Context context) {
        super(context);
    }
    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_switch);
    }
    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_switch);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.preference_switch);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        sharedView = view;

        TextView text = view.findViewById(R.id.textView);
        text.setText(getSummary());

        View root = view.findViewById(R.id.root);
        root.setOnClickListener(this);

        switcher = view.findViewById(R.id.switchButton);
        switch (getKey()){
            case "colored_notification":
                if (PreferenceUtil.getInt(getContext(), "color_not_int") == 1) { switcher.setChecked(false);} else { switcher.setChecked(true); }
                break;
            case "should_color_navigation_bar":
                if (PreferenceUtil.getInt(getContext(), "navbar_color_int") == 1) { switcher.setChecked(false);} else { switcher.setChecked(true); }
                break;
        }

        switcher.setOnCheckedChangeListener(this);


    }

    @Override
    public void onClick(View v) {
            switcher.setOnCheckedChangeListener(this);
    }

    public void recreate(){
        TaskStackBuilder.create(getContext())
                .addNextIntent(new Intent(getContext(), MainActivity.class))
                .addNextIntent(((Activity)sharedView.getContext()).getIntent())
                .startActivities();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b){
            switch (getKey()){
                case "colored_notification":
                    PreferenceUtil.getInstance(getContext()).setInt(PreferenceUtil.COLOR_NOT_INT,2);
                    PreferenceUtil.getInstance(getContext()).setColoredNotification(b);
                    break;
                case "should_color_navigation_bar":
                    PreferenceUtil.getInstance(getContext()).setInt(PreferenceUtil.NAVBAR_COLOR_INT,2);
                    ThemeStore.editTheme(getContext())
                            .coloredNavigationBar(b)
                            .commit();
                    recreate();
                    break;
                case "fullscreen_preference":
                    PreferenceUtil.getInstance(getContext()).setFullscreenInteger(2);
                    ((Activity)sharedView.getContext()).recreate();
                    recreate();
                    break;
            }
        } else {
            switch (getKey()){
                case "colored_notification":
                    PreferenceUtil.getInstance(getContext()).setInt(PreferenceUtil.COLOR_NOT_INT,1);
                    PreferenceUtil.getInstance(getContext()).setColoredNotification(false);
                    break;
                case "should_color_navigation_bar":
                    PreferenceUtil.getInstance(getContext()).setInt(PreferenceUtil.NAVBAR_COLOR_INT,1);
                    ThemeStore.editTheme(getContext())
                            .coloredNavigationBar(false)
                            .commit();
                    recreate();
                    break;
                case "fullscreen_preference":
                    compoundButton.setChecked(false);
                    PreferenceUtil.getInstance(getContext()).setFullscreenInteger(1);
                    ((Activity)sharedView.getContext()).recreate();
                    recreate();
                    break;
            }
        }
    }

}
