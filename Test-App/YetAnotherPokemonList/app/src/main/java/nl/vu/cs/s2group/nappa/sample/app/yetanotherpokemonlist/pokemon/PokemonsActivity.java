package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public class PokemonsActivity extends AppCompatActivity {
    String[] mobileArray = {"Android", "IPhone", "WindowsMobile", "Blackberry",
            "WebOS", "Ubuntu", "Windows7", "Max OS X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, mobileArray);
        ListView listView = (ListView) findViewById(R.id.pokemon_list);
        listView.setAdapter(adapter);
    }
}