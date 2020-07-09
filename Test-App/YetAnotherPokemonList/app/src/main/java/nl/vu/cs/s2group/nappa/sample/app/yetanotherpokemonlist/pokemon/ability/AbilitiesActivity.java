package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.NappaLifecycleObserver;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIActivity;

public class AbilitiesActivity extends NamedAPIActivity {
    private static final String LOG_TAG = AbilitiesActivity.class.getSimpleName();
    private static final String API_URL = "ability/";

    public AbilitiesActivity() {
        super(R.layout.activity_abilities, LOG_TAG, API_URL);
    }

    @Override
    protected void setTotalItems() {
        setTotalItems("abilities");
    }

    @Override
    protected void setHeaderText() {
        setHeaderText("Pokemons abilities");
    }

    @Override
    protected void onItemClickListener(String url) {
        Log.d(LOG_TAG, "Clicked on " + url);
        Intent intent = new Intent(this, AbilityActivity.class);
        intent.putExtra("url", url);
        Nappa.notifyExtras(intent.getExtras());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NappaLifecycleObserver(this));
    }
}