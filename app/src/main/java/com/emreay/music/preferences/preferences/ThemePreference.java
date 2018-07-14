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
import android.widget.TextView;

import com.emreay.music.R;
import com.emreay.music.activites.activities.MainActivity;
import com.emreay.music.util.PreferenceUtil;

public class ThemePreference extends Preference implements View.OnClickListener {

    public ThemePreference(Context context) {
        super(context);
    }
    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_theme);
    }
    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_theme);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.preference_theme);
    }

    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView button1 = view.findViewById(R.id.button1);
        ImageView button2 = view.findViewById(R.id.button2);
        ImageView button3 = view.findViewById(R.id.button3);

        TextView text1 = view.findViewById(R.id.textView1);
        TextView text2 = view.findViewById(R.id.textView2);
        TextView text3 = view.findViewById(R.id.textView3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        switch (PreferenceUtil.getThemeInt(getContext())) {
            case 1:
                button1.setBackgroundResource(PreferenceUtil.getGardientColor(getContext()));
                button1.setScaleY(-1);
                text1.setTextColor(getContext().getResources().getColor(R.color.md_white_1000));
                break;
            case 2:
                button2.setBackgroundResource(PreferenceUtil.getGardientColor(getContext()));
                button2.setScaleY(-1);
                text2.setTextColor(getContext().getResources().getColor(R.color.md_white_1000));
                break;
            case 3:
                button3.setImageResource(PreferenceUtil.getGardientColor(getContext()));
                button3.setScaleY(-1);
                text3.setTextColor(getContext().getResources().getColor(R.color.md_white_1000));
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1 :
                PreferenceUtil.getInstance(getContext()).setThemeInteger(1);
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
            case R.id.button2 :
                PreferenceUtil.getInstance(getContext()).setThemeInteger(2);
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
            case R.id.button3 :
                PreferenceUtil.getInstance(getContext()).setThemeInteger(3);
                TaskStackBuilder.create(getContext())
                        .addNextIntent(new Intent(getContext(), MainActivity.class))
                        .addNextIntent(((Activity)v.getContext()).getIntent())
                        .startActivities();
                break;
        }
    }

}
