package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry;

import android.os.Bundle;
import android.util.Log;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultActivity;

public class BerriesActivity extends DefaultActivity {
    private static final String LOG_TAG = BerriesActivity.class.getSimpleName();
    private static final String API_URL = "berry/";

    public BerriesActivity() {
        super(R.layout.activity_berries, LOG_TAG, API_URL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berries);
    }

    @Override
    protected void setTotalItems() {
        setTotalItems("berries");
    }

    @Override
    protected void setHeaderText() {
        setHeaderText("Berries");
    }

    @Override
    protected void onItemClickListener(String url) {
        Log.d(LOG_TAG, "Clicked on " + url);
    }
}