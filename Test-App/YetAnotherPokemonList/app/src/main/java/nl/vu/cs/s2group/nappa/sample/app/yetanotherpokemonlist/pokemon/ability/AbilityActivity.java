package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.ViewUtil;

public class AbilityActivity extends AppCompatActivity {
    private static final String LOG_TAG = AbilityActivity.class.getSimpleName();
    Ability ability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability);
        toggleProgressBarVisibility(true);
        AbilityAPI.makeRequest(getIntent().getStringExtra("url"), this::handleRequest);
    }

    private void handleRequest(Ability ability) {
        this.ability = ability;
        setPageTitle();
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_ability_pokemons, ability.pokemon, "getPokemon", (view) -> {
            String url = view.getTag().toString();
            Log.d(LOG_TAG, "Clicked on " + url);
            startActivity(new Intent(this, PokemonActivity.class)
                    .putExtra("url", url));
        });
        toggleProgressBarVisibility(false);
    }

    private void toggleProgressBarVisibility(boolean isVisible) {
        runOnUiThread(() -> findViewById(R.id.indeterminateBar).setVisibility(isVisible ? ProgressBar.VISIBLE : ProgressBar.GONE));
    }

    private void setPageTitle() {
        ((TextView) findViewById(R.id.page_title)).setText(ability.name);
    }
}