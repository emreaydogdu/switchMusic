package com.emreay.music.preferences.preferences;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emreay.music.LocalHelper;
import com.emreay.music.R;
import com.emreay.music.activites.activities.MainActivity;

import java.util.ArrayList;

public class LanguagePreference extends Preference {

    //String language[] = {"Mango","Apple", "Grapes","Papaya","WaterMelon"};
    private String locale[] = {"ar","bg", "zh-rCN","zh-rTW","hr","cs","nl","en-rGB","fi","fr","de","el","hu","id","it","ja","ko","nn","pl","pt-rBR","pt-rPT","ro","ru","es-rES","sv","tr","uk","vi"};
    private int    images[] = {R.drawable.land_arab,R.drawable.land_bulgaria,  R.drawable.land_china, R.drawable.land_taiwan,
                     R.drawable.land_croatia,R.drawable.land_czech,R.drawable.land_nederlands,  R.drawable.land_england,
                     R.drawable.land_finish,R.drawable.land_france,R.drawable.land_germany,R.drawable.land_gereece,
                     R.drawable.land_hungary,R.drawable.land_indonesia,R.drawable.land_italy,R.drawable.land_japan,
                     R.drawable.land_korea,R.drawable.land_noreway,R.drawable.land_poland,R.drawable.land_brazil,
                     R.drawable.land_portugal,R.drawable.land_romania,R.drawable.land_russia,R.drawable.land_spain ,
                     R.drawable.land_sweden,R.drawable.land_turkey,R.drawable.land_ukraine,R.drawable.land_vietnam };

    public LanguagePreference(Context context) {
        super(context);
    }
    public LanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_language);
    }
    public LanguagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_language);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LanguagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.preference_language);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ArrayList<Language> listitems = new ArrayList<>();
        listitems.clear();
        for(int i =0;i<images.length;i++){
            listitems.add(new Language(images[i],locale[i]));
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getContext());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (listitems.size() > 0) {
            recyclerView.setAdapter(new MyAdapter(listitems));
        }
        recyclerView.setLayoutManager(MyLayoutManager);

    }

    private void changeLang(String lang){
        /*setLanguage(lang);
        Locale locale = new Locale(lang);
        Resources res = getContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config,dm);*/
        Context context = LocalHelper.setLocale(getContext(), lang);
        Resources resources = context.getResources();
    }
    public SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }
    public void setLanguage(String language) {
        getPrefs().edit().putString("language", language).apply();
    } @NonNull
    public String getLanguage() {
        return getPrefs().getString("language", "system");
    }
    private static void recreate(Context context){
        TaskStackBuilder.create(context)
                .addNextIntent(new Intent(context, MainActivity.class))
                .addNextIntent(((Activity)context).getIntent())
                .startActivities();
    }

    public class Language {

        int images;
        String locale;

        Language(Integer images, String locale) {
            this.images = images;
            this.locale = locale;
        }

        public int getImages() {
            return images;
        }

        public String getLocale() {
            return locale;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Language> list;

        private MyAdapter(ArrayList<Language> Data) {
            list = Data;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView language;
            private ImageView likeImageView;

            MyViewHolder(View v) {
                super(v);
                //language = v.findViewById(R.id.titleTextView);
                likeImageView = v.findViewById(R.id.imageView);

            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pref_language, parent, false);
            return new MyViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            //holder.titleTextView.setText(list.get(position).getCardName());
            holder.likeImageView.setImageResource(list.get(position).getImages());
            holder.likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLanguage().equals(list.get(position).getLocale())) return;
                    changeLang(list.get(position).getLocale());
                    recreate(getContext());
                }
            });

        }
        @Override
        public int getItemCount() { return list.size(); }
    }

}
