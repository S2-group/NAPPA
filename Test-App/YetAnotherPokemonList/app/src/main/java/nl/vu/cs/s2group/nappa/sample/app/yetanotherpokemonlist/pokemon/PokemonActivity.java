package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.squareup.picasso.Picasso;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.APIResourceUtil;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.ViewUtil;

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
        setPokemonSprites();
        setPokemonCharacteristics();
        setPokemonStats();
        setNamedAPIResourceList(R.id.ll_pokemon_abilities, pokemon.abilities, "getAbility");
        setNamedAPIResourceList(R.id.ll_pokemon_types, pokemon.types, "getType");
        setNamedAPIResourceList(R.id.ll_pokemon_moves, pokemon.moves, "getMove");
    }

    private void setPageTitle() {
        ((TextView) findViewById(R.id.page_title)).setText(pokemon.name);
    }

    private void setPokemonSprites() {
        ImageView ivFront = findViewById(R.id.iv_pokemon_sprite_front);
        ImageView ivBack = findViewById(R.id.iv_pokemon_sprite_back);

        new Handler(Looper.getMainLooper()).post(() -> {
            Picasso.with(this).load(pokemon.sprites.frontDefault).into(ivFront);
            Picasso.with(this).load(pokemon.sprites.backDefault).into(ivBack);
        });

    }

    private void setPokemonCharacteristics() {
        String baseXp = String.format(Config.LOCALE, "%d", pokemon.base_experience) + " xp";
        ((TextView) findViewById(R.id.tv_pokemon_base_experience)).setText(baseXp);

        String height = String.format(Config.LOCALE, "%d", pokemon.height) + " dm";
        ((TextView) findViewById(R.id.tv_pokemon_height)).setText(height);

        String weight = String.format(Config.LOCALE, "%d", pokemon.weight) + " hg";
        ((TextView) findViewById(R.id.tv_pokemon_weight)).setText(weight);

        int isDefaultTextId = pokemon.isDefault ? R.string.yes : R.string.no;
        String isDefault = getResources().getString(isDefaultTextId);
        ((TextView) findViewById(R.id.tv_pokemon_species_default)).setText(isDefault);
    }

    private void setPokemonStats() {
        runOnUiThread(() -> {
            LinearLayoutCompat layout = findViewById(R.id.ll_pokemon_stats);
            for (PokemonStat pokemonStat : pokemon.stats) {
                String stateValueStr = String.format(Config.LOCALE, "%d", pokemonStat.getBaseStat()) +
                        " (" + String.format(Config.LOCALE, "%d", pokemonStat.effort) + " EV)";

                TextView tvStatLabel = ViewUtil.createTextView(this, pokemonStat.getStat().getName(), 0.5f, R.style.TextViewLabel);
                TextView tvStatValue = ViewUtil.createTextView(this, stateValueStr, 0.5f, R.style.TextViewValue);

                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.addView(tvStatLabel);
                rowLayout.addView(tvStatValue);

                layout.addView(rowLayout);
            }
        });
    }

    private void setNamedAPIResourceList(int viewId, List<?> list, String getterMethod) {
        List<NamedAPIResource> namedAPIResourceList = APIResourceUtil.parseListToNamedAPOResourceList(list, getterMethod);
        runOnUiThread(() -> {
            LinearLayoutCompat linearLayout = findViewById(viewId);
            for (NamedAPIResource namedAPIResource : namedAPIResourceList) {
                linearLayout.addView(ViewUtil.createTextView(this, namedAPIResource.getName()));
            }
        });
    }
}