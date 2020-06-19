package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry.BerriesActivity;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultActivity;

public class LocationsActivity extends DefaultActivity {
    private static final String LOG_TAG = BerriesActivity.class.getSimpleName();
    private static final String API_URL = "location/";

    public LocationsActivity() {
        super(R.layout.activity_locations, LOG_TAG, API_URL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
    }

    @Override
    protected void setTotalItems() {
        setTotalItems("locations");
    }

    @Override
    protected void setHeaderText() {
        setHeaderText("Locations");
    }

    @Override
    protected void onItemClickListener(String url) {
        Log.d(LOG_TAG, "Clicked on " + url);
    }
}