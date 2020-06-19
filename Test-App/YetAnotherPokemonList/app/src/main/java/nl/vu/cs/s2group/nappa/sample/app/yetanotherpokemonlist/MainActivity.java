package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.AbilityActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerriesActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location.LocationsActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonsActivity;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Class<AppCompatActivity>[] activities = new Class[]{
            PokemonsActivity.class,
            AbilityActivity.class,
            BerriesActivity.class,
            LocationsActivity.class,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void navigateToPage(View view) {
        int index = Integer.parseInt(view.getTag().toString());
        Class<AppCompatActivity> activity = activities[index];
        Log.d(LOG_TAG, "Navigating to page " + activity.getCanonicalName());
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}