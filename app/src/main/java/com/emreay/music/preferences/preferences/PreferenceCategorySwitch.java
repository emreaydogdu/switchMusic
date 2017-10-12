package com.emreay.music.preferences.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.emreay.music.R;
public class PreferenceCategorySwitch extends android.preference.PreferenceCategory {

    private Context context;

    public PreferenceCategorySwitch(Context context) {
        super(context);
        this.context = context;
    }

    public PreferenceCategorySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setLayoutResource(R.layout.preference_category);
    }

    public PreferenceCategorySwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setLayoutResource(R.layout.preference_category);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(getTitle());
        titleView.getResources().getColor(R.color.md_white_1000);
    }
}
