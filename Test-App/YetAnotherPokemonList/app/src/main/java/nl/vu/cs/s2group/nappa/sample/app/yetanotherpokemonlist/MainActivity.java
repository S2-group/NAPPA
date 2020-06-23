package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import nl.vu.cs.s2group.nappa.NAPPALifecycleObserver;
import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerriesActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerryActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location.LocationActivity;
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
            LocationActivity.class
    };
    private int[] maxItems = new int[]{
            964,
            293,
            64,
            781,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefetchingLib.init(this, PrefetchingStrategy.STRATEGY_GREEDY);
        getLifecycle().addObserver(new NAPPALifecycleObserver(this));
        setContentView(R.layout.activity_main);
    }

    public void navigateToPage(View view) {
        int index = Integer.parseInt(view.getTag().toString());
        Class<AppCompatActivity> activity = activitiesList[index];
        Log.d(LOG_TAG, "Navigating to page " + activity.getCanonicalName());
        Intent intent = new Intent(this, activity);
        PrefetchingLib.notifyExtras(intent.getExtras());
        startActivity(intent);
    }

    public void navigateToRandomPage(View view) {
        int pageIndex = new Random().nextInt(activitiesItem.length - 1);
        int itemId = new Random().nextInt(maxItems[pageIndex]);
        Class<AppCompatActivity> activity = activitiesItem[pageIndex];
        // There are gaps in the API IDs
        if (pageIndex == 0 && itemId > 807) itemId += 10001 - 807;
        if (pageIndex == 1 && itemId > 233) itemId += 10001 - 233;
        Log.d(LOG_TAG, "Navigating to page " + activity.getCanonicalName() + " with ID " + itemId + " of " + maxItems[pageIndex]);
        Intent intent = new Intent(this, activity);
        intent.putExtra("id", itemId);
        PrefetchingLib.notifyExtras(intent.getExtras());
        startActivity(intent);
    }

    public void navigateToFindPage(View view) {
        Log.d(LOG_TAG, "Navigating to find page");
        Intent intent = new Intent(this, FindItemActivity.class);
        PrefetchingLib.notifyExtras(intent.getExtras());
        startActivity(intent);
    }
}