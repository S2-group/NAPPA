package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location;

import android.content.Intent;
import android.util.Log;

import nl.vu.cs.s2group.nappa.*;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIActivity;

public class LocationsActivity extends NamedAPIActivity {
    private static final String LOG_TAG = LocationsActivity.class.getSimpleName();
    private static final String API_URL = "location/";

    public LocationsActivity() {
        super(R.layout.activity_locations, LOG_TAG, API_URL);
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
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("url", url);
        PrefetchingLib.notifyExtras(intent.getExtras());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }
}