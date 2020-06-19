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
        if (getIntent().getStringExtra("url") != null)
            AbilityAPI.makeRequest(getIntent().getStringExtra("url"), this::handleResponse);
        else
            AbilityAPI.makeRequest(getIntent().getIntExtra("id", 1), this::handleResponse);
    }

    private void handleResponse(Ability ability) {
        this.ability = ability;

        setPageTitle();
        toggleProgressBarVisibility(false);
        setAbilityEffectChange();
        setAbilityCharacteristics();


        ViewUtil.addNamedAPIResourceListWithLanguageToUI(this, R.id.ll_ability_effect_entries, ability.effect_entries, "getEffect");
        ViewUtil.addNamedAPIResourceListWithLanguageToUI(this, R.id.ll_ability_flavor, ability.flavor_text_entries, "getFlavorTextWithVersionGroup");
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_ability_pokemons, ability.pokemon, "getPokemon", (view) -> {
            String url = view.getTag().toString();
            Log.d(LOG_TAG, "Clicked on " + url);
            startActivity(new Intent(this, PokemonActivity.class)
                    .putExtra("url", url));
        });
    }

    private void toggleProgressBarVisibility(boolean isVisible) {
        runOnUiThread(() -> findViewById(R.id.indeterminateBar).setVisibility(isVisible ? ProgressBar.VISIBLE : ProgressBar.GONE));
    }

    private void setPageTitle() {
        ((TextView) findViewById(R.id.page_title)).setText(ability.name);
    }

    private void setAbilityCharacteristics() {
        int isDefaultTextId = ability.is_main_series ? R.string.yes : R.string.no;
        String isDefault = getResources().getString(isDefaultTextId);
        ((TextView) findViewById(R.id.tv_ability_from_main_serie)).setText(isDefault);

        ((TextView) findViewById(R.id.tv_ability_generation)).setText(ability.generation.getName());
    }

    private void setAbilityEffectChange() {
        for (AbilityEffectChange effectChange : ability.effect_changes) {
            ViewUtil.addNamedAPIResourceListWithLanguageToUI(this, R.id.ll_ability_effect_change, effectChange.effect_entries, "getEffect");
        }
    }
}