package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultActivity;

public class PokemonsActivity extends DefaultActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();
    private static final String API_URL = "pokemon/";

    public PokemonsActivity() {
        super(LOG_TAG, API_URL);
    }
}