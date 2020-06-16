package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, PokemonsActivity.class);
        startActivity(intent);
    }
}