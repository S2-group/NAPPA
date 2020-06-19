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
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.PokemonAbility;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.type.PokemonType;

public class PokemonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        PokemonAPI.makeRequest(getIntent().getStringExtra("url"), this::handleRequest);
    }

    private void handleRequest(Pokemon pokemon) {
        setPageTitle(pokemon.name);
        setPokemonTypes(pokemon.types);
        setPokemonAbilities(pokemon.abilities);
    }

    private void setPageTitle(String pokemonName) {
        ((TextView) findViewById(R.id.page_title)).setText(pokemonName);
    }

    private void setPokemonTypes(List<PokemonType> wrapper) {
        List<NamedAPIResource> types = APIResourceUtil.parseListToNamedAPOResourceList(wrapper, "getType");
        runOnUiThread(() -> {
            NamedAPIAdapter adapter = new NamedAPIAdapter(this, R.layout.activity_pokemon, types);
            ListView listView = findViewById(R.id.lv_types);
            listView.setAdapter(adapter);
        });
    }

    private void setPokemonAbilities(List<PokemonAbility> wrapper) {
        List<NamedAPIResource> types = APIResourceUtil.parseListToNamedAPOResourceList(wrapper, "getAbility");
        runOnUiThread(() -> {
            NamedAPIAdapter adapter = new NamedAPIAdapter(this, R.layout.activity_pokemon, types);
            ListView listView = findViewById(R.id.lv_abilities);
            listView.setAdapter(adapter);
        });
    }
}