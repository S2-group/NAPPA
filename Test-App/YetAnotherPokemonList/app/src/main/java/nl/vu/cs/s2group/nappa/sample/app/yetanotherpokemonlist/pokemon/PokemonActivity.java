package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIAdapter;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.APIResourceUtil;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;

public class PokemonActivity extends AppCompatActivity {
    Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        PokemonAPI.makeRequest(getIntent().getStringExtra("url"), this::handleRequest);
    }

    private void handleRequest(Pokemon pokemon) {
        this.pokemon = pokemon;
        setPageTitle();
        setPokemonCharacteristics();
        setDataList(R.id.lv_pokemon_stats, pokemon.stats, "getStat");
        setDataList(R.id.lv_pokemon_abilities, pokemon.abilities, "getAbility");
        setDataList(R.id.lv_pokemon_types, pokemon.types, "getType");
    }

    private void setPageTitle() {
        ((TextView) findViewById(R.id.page_title)).setText(pokemon.name);
    }

    private void setPokemonCharacteristics() {
        String baseXp = String.format(Config.LOCALE, "%d", pokemon.base_experience) + " xp";
        ((TextView) findViewById(R.id.tv_pokemon_base_experience)).setText(baseXp);

        String height = String.format(Config.LOCALE, "%d", pokemon.height) + " dm";
        ((TextView) findViewById(R.id.tv_pokemon_height)).setText(height);

        String weight = String.format(Config.LOCALE, "%d", pokemon.weight) + " hg";
        ((TextView) findViewById(R.id.tv_pokemon_weight)).setText(weight);

        int isDefaultTextId = pokemon.is_default ? R.string.yes : R.string.no;
        String isDefault = getResources().getString(isDefaultTextId);
        ((TextView) findViewById(R.id.tv_pokemon_species_default)).setText(isDefault);
    }

    private void setDataList(int viewId, List<?> list, String getterMethod) {
        List<NamedAPIResource> namedAPIResourceList = APIResourceUtil.parseListToNamedAPOResourceList(list, getterMethod);
        runOnUiThread(() -> {
            NamedAPIAdapter adapter = new NamedAPIAdapter(this, R.layout.activity_pokemon, namedAPIResourceList);
            ListView listView = findViewById(viewId);
            listView.setAdapter(adapter);
        });
    }
}