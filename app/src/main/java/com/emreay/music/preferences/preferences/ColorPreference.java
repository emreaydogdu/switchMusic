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
import android.widget.ImageView;

import com.emreay.music.R;
import com.emreay.music.activites.activities.MainActivity;
import com.emreay.music.util.PreferenceUtil;
import com.kabouzeid.appthemehelper.ThemeStore;
public class ColorPreference extends Preference implements View.OnClickListener {

    public ColorPreference(Context context) {
        super(context);
    }
    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_color);
    }
    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_color);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.preference_color);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView button1 = view.findViewById(R.id.button1);
        ImageView button2 = view.findViewById(R.id.button2);
        ImageView button3 = view.findViewById(R.id.button3);
        ImageView button4 = view.findViewById(R.id.button4);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1 :
                PreferenceUtil.getInstance(getContext()).setColorInteger(1);
                ThemeStore.editTheme(getContext())
                        .accentColor(getContext().getResources().getColor(R.color.md_orange_500))
                        .commit();
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
            case R.id.button2 :
                PreferenceUtil.getInstance(getContext()).setColorInteger(2);
                ThemeStore.editTheme(getContext())
                        .accentColor(getContext().getResources().getColor(R.color.md_blue_300))
                        .commit();
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
            case R.id.button3 :
                PreferenceUtil.getInstance(getContext()).setColorInteger(3);
                ThemeStore.editTheme(getContext())
                        .accentColor(getContext().getResources().getColor(R.color.md_red_500))
                        .commit();
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
            case R.id.button4 :
                PreferenceUtil.getInstance(getContext()).setColorInteger(4);
                ThemeStore.editTheme(getContext())
                        .accentColor(getContext().getResources().getColor(R.color.md_green_500))
                        .commit();
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
        }
    }

}
