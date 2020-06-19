package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerryActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.PokemonActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.AbilityActivity;

public class FindItemActivity extends AppCompatActivity {
    private Class<AppCompatActivity>[] activitiesItem = new Class[]{
            PokemonActivity.class,
            AbilityActivity.class,
            BerryActivity.class,
    };
    private int[] maxItems = new int[]{
            964,
            293,
            64,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);
        setActivityList();
    }

    public void findItem(View view) {

    }

    private void setActivityList() {
        String[] activitiesItemStr = new String[]{
                getResources().getString(R.string.tv_pokemon),
                getResources().getString(R.string.tv_ability),
                getResources().getString(R.string.tv_berry),
        };
        Spinner sp = findViewById(R.id.sp_page_type);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activitiesItemStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }
}