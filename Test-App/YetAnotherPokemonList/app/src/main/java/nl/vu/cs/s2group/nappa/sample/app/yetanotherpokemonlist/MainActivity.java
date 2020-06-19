package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerriesActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerryActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location.LocationsActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonsActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.AbilitiesActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.AbilityActivity;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Class<AppCompatActivity>[] activitiesList = new Class[]{
            PokemonsActivity.class,
            AbilitiesActivity.class,
            BerriesActivity.class,
            LocationsActivity.class,
    };
    private Class<AppCompatActivity>[] activitiesItem = new Class[]{
            PokemonActivity.class,
            AbilityActivity.class,
            BerryActivity.class,
    };
    private int[] maxItems = new int[]{
            964,
            293,
            64,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void navigateToPage(View view) {
        int index = Integer.parseInt(view.getTag().toString());
        Class<AppCompatActivity> activity = activitiesList[index];
        Log.d(LOG_TAG, "Navigating to page " + activity.getCanonicalName());
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void navigateToRandomPage(View view) {
        int pageIndex = new Random().nextInt(activitiesItem.length - 1);
        int itemId = new Random().nextInt(maxItems[pageIndex]);
        Class<AppCompatActivity> activity = activitiesItem[pageIndex];
        Log.d(LOG_TAG, "Navigating to page " + activity.getCanonicalName());
        Intent intent = new Intent(this, activity);
        intent.putExtra("id", itemId);
        startActivity(intent);
    }

}