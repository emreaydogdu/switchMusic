package com.emreay.music.preferences.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emreay.music.R;
import com.emreay.music.util.PreferenceUtil;
import com.emreay.music.util.Util;

public class SimplePreference extends Preference implements View.OnClickListener {

    private TextView text1;

    public SimplePreference(Context context) {
        super(context);
    }

    public SimplePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_simple);
    }

    public SimplePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_simple);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimplePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.preference_simple);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView text1 = view.findViewById(R.id.textView);
        text1.setText(getTitle());

        ImageView button1 = view.findViewById(R.id.imageView);
        button1.setOnClickListener(this);

        View root = view.findViewById(R.id.root);
        root.setOnClickListener(this);

        switch (PreferenceUtil.getThemeInt(getContext())) {
            case 1:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root :
                switch (getKey()) {
                    case "start_selection":
                        //getContext().startActivity(NavigationUtils.getNavigateToStyleSelectorIntent(getContext(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));
                        Util.toast(getContext(), "Test1", Toast.LENGTH_SHORT);
                        break;
                    case "bottom_playing_selector":
                        //getContext().startActivity(NavigationUtils.getNavigateToBottomStyleSelectorIntent(getContext(), Constants.SETTINGS_STYLE_SELECTOR_BOTTOMPLAYING));
                        break;
                }
                break;
            case R.id.imageView :
                switch (getKey()) {
                    case "now_playing_selector":
                        //getContext().startActivity(NavigationUtils.getNavigateToStyleSelectorIntent(getContext(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));
                        break;
                    case "bottom_playing_selector":
                        //getContext().startActivity(NavigationUtils.getNavigateToBottomStyleSelectorIntent(getContext(), Constants.SETTINGS_STYLE_SELECTOR_BOTTOMPLAYING));
                        break;
                }
                break;
        }
    }

}
