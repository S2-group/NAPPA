package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.squareup.picasso.Picasso;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.NappaLifecycleObserver;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.AbilityActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.ViewUtil;

public class PokemonActivity extends AppCompatActivity {
    private static final String LOG_TAG = PokemonActivity.class.getSimpleName();
    Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NappaLifecycleObserver(this));
        setContentView(R.layout.activity_pokemon);
        toggleProgressBarVisibility(true);
        if (getIntent().getStringExtra("url") != null)
            PokemonAPI.makeRequest(getIntent().getStringExtra("url"), this::handleResponse);
        else
            PokemonAPI.makeRequest(getIntent().getIntExtra("id", 1), this::handleResponse);
    }

    private void handleResponse(Pokemon pokemon) {
        this.pokemon = pokemon;
        setPageTitle();
        setPokemonSprites();
        setPokemonCharacteristics();
        setPokemonStats();
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_pokemon_abilities, pokemon.abilities, "getAbility", (view) -> {
            String url = view.getTag().toString();
            Log.d(LOG_TAG, "Clicked on " + url);
            Intent intent = new Intent(this, AbilityActivity.class)
                    .putExtra("url", url);
            Nappa.notifyExtras(intent.getExtras());
            startActivity(intent);
        });
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_pokemon_types, pokemon.types, "getType");
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_pokemon_moves, pokemon.moves, "getMove");
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_pokemon_held_items, pokemon.held_items, "getItem");
        toggleProgressBarVisibility(false);
    }

    private void toggleProgressBarVisibility(boolean isVisible) {
        runOnUiThread(() -> findViewById(R.id.indeterminateBar).setVisibility(isVisible ? ProgressBar.VISIBLE : ProgressBar.GONE));
    }

    private void setPageTitle() {
        String title = getResources().getString(R.string.tv_pokemon) + ": " + pokemon.name + " (#" + pokemon.id + ")";
        ((TextView) findViewById(R.id.page_title)).setText(title);
    }

    private void setPokemonSprites() {
        ImageView ivFront = findViewById(R.id.iv_pokemon_sprite_front);
        ImageView ivBack = findViewById(R.id.iv_pokemon_sprite_back);

        new Handler(Looper.getMainLooper()).post(() -> {
            Picasso.with(this).load(pokemon.sprites.front_default).into(ivFront);
            Picasso.with(this).load(pokemon.sprites.back_default).into(ivBack);
        });

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

    private void setPokemonStats() {
        runOnUiThread(() -> {
            LinearLayoutCompat layout = findViewById(R.id.ll_pokemon_stats);
            for (PokemonStat pokemonStat : pokemon.stats) {
                String stateValueStr = String.format(Config.LOCALE, "%d", pokemonStat.getBase_stat()) +
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
}