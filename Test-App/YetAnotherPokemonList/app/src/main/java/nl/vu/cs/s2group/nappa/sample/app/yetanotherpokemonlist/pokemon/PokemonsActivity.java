package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public class PokemonsActivity extends AppCompatActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();

    private PokemonsAdapter adapter;
    private PokemonsApi pokemonsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
        pokemonsApi = new PokemonsApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pokemonsApi.getInitialContent(this::handleResponse);
    }

    public void onPrevious(View view) {
        disableButtonsOnLoad();
        pokemonsApi.getPrevious(this::handleResponse);
    }

    public void onNext(View view) {
        disableButtonsOnLoad();
        pokemonsApi.getNext(this::handleResponse);
    }

    private void handleResponse(List<Pokemon> pokemons) {
        updatePaginationButtons();
        runOnUiThread(() -> {
            if (adapter == null)
                adapter = new PokemonsAdapter(this, R.layout.activity_pokemons, pokemons);
            else {
                adapter.clear();
                adapter.addAll(pokemons);
            }
            ListView listView = findViewById(R.id.pokemon_list);
            listView.setAdapter(adapter);
        });
    }

    private void disableButtonsOnLoad() {
        findViewById(R.id.pokemons_btn_previous).setEnabled(false);
        findViewById(R.id.pokemons_btn_next).setEnabled(false);

    }

    private void updatePaginationButtons() {
        findViewById(R.id.pokemons_btn_previous).setEnabled(pokemonsApi.hasPrevious());
        findViewById(R.id.pokemons_btn_next).setEnabled(pokemonsApi.hasNext());
    }
}