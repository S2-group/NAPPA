package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public class PokemonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        PokemonApi.makeRequest(getIntent().getStringExtra("url"), this::handleRequest);
    }

    private void handleRequest(Pokemon pokemon) {
        setPageTitle(pokemon.name);
    }

    private void setPageTitle(String pokemonName) {
        ((TextView) findViewById(R.id.page_title)).setText(pokemonName);
    }
}