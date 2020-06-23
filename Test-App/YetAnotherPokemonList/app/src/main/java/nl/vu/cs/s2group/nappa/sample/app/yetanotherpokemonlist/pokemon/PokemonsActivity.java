package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.content.Intent;
import android.util.Log;

import nl.vu.cs.s2group.nappa.*;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIActivity;

public class PokemonsActivity extends NamedAPIActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();
    private static final String API_URL = "pokemon/";

    public PokemonsActivity() {
        super(R.layout.activity_pokemons, LOG_TAG, API_URL);
    }

    @Override
    protected void setTotalItems() {
        setTotalItems("pokemons");
    }

    @Override
    protected void setHeaderText() {
        setHeaderText("Pokemons");
    }

    @Override
    protected void onItemClickListener(String url) {
        Log.d(LOG_TAG, "Clicked on " + url);
        Intent intent = new Intent(this, PokemonActivity.class);
        intent.putExtra("url", url);
        PrefetchingLib.notifyExtras(intent.getExtras());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }
}