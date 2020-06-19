package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import android.util.Log;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonsActivity;

public class AbilityActivity extends DefaultActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();
    private static final String API_URL = "ability/";

    public AbilityActivity() {
        super(R.layout.activity_ability, LOG_TAG, API_URL);
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
    }
}