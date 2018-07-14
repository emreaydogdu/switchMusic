package com.emreay.music.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emreay.music.R;
import com.emreay.music.activites.activities.SettingsActivity;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*
        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        adapter = new MyRecyclerViewAdapter(this, animalNames);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
*/
        findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateIntent();
            }
        });
    }

    public void animateIntent() {

        // Ordinary Intent for launching a new activity
        Intent intent = new Intent(this, SettingsActivity.class);

        // Get the transition name from the string
        String transitionName = getString(R.string.transition_string);

        // Define the view that the animation will start from
        View viewStart = findViewById(R.id.card);

        ActivityOptionsCompat options =

                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        viewStart,   // Starting view
                        transitionName    // The String
                );
        //Start the Intent
        ActivityCompat.startActivity(this, intent, options.toBundle());

    }

    static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

            private List<String> mData;
            private LayoutInflater mInflater;

            // data is passed into the constructor
            MyRecyclerViewAdapter(Context context, List<String> data) {
                this.mInflater = LayoutInflater.from(context);
                this.mData = data;
            }

            // inflates the row layout from xml when needed
            @NonNull @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = mInflater.inflate(R.layout.test_itemview, parent, false);
                return new ViewHolder(view);
            }

            // binds the data to the TextView in each row
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                String animal = mData.get(position);
                holder.myTextView.setText(animal);
            }

            // total number of rows
            @Override
            public int getItemCount() {
                return mData.size();
            }


            // stores and recycles views as they are scrolled off screen
            public class ViewHolder extends RecyclerView.ViewHolder {
                TextView myTextView;

                ViewHolder(View itemView) {
                    super(itemView);
                    myTextView = itemView.findViewById(R.id.tvAnimalName);
                }

            }

    }

}
