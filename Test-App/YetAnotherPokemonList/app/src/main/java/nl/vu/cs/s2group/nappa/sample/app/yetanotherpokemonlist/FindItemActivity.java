package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

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
            807,
            233,
            64,
    };

    private int[] maxItemsGap = new int[]{
            10143,
            10060,
            -1,
    };

    private int[] indexesWithGap = new int[]{
            0,
            1,
    };

    private int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);
        setActivityList();
        setTextListener();
    }

    public void findItem(View view) {
        EditText et = findViewById(R.id.et_item_id);
        int id = Integer.parseInt(et.getText().toString());
        startActivity(new Intent(this, activitiesItem[selectedIndex])
                .putExtra("id", id));
    }

    private void setTextListener() {
        EditText et = findViewById(R.id.et_item_id);
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) return;
                int id = Integer.parseInt(s.toString());
                Button btn = findViewById(R.id.btn_find);
                btn.setEnabled(id >= 1 && id <= maxItems[selectedIndex] || Arrays.stream(indexesWithGap).anyMatch(j -> j == selectedIndex) && id >= 10001 && id <= maxItemsGap[selectedIndex]);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
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
                TextView tv = findViewById(R.id.tv_available_id);
                String str = "1 to " + maxItems[i];
                if (Arrays.stream(indexesWithGap).anyMatch(j -> j == i))
                    str += "\n10001 to " + maxItemsGap[i];
                tv.setText(str);

                EditText et = findViewById(R.id.et_item_id);
                et.getText().clear();
                selectedIndex = i;
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