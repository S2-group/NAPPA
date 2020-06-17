package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.ability;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonsActivity;

public class AbilityActivity extends DefaultActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();
    private static final String API_URL = "pokemon/";

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
}