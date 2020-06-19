package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.ViewUtil;

public class LocationActivity extends AppCompatActivity {
    private static final String LOG_TAG = LocationActivity.class.getSimpleName();
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        toggleProgressBarVisibility(true);
        if (getIntent().getStringExtra("url") != null)
            LocationAPI.makeRequest(getIntent().getStringExtra("url"), this::handleResponse);
        else
            LocationAPI.makeRequest(getIntent().getIntExtra("id", 1), this::handleResponse);
    }

    private void handleResponse(Location location) {
        this.location = location;

        setPageTitle();
        toggleProgressBarVisibility(false);
        setAbilityCharacteristics();

        ViewUtil.addListToUI(this, R.id.ll_location_areas, location.areas, "getName");
        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_location_game_indices, location.game_indices, "getGeneration");
    }

    private void toggleProgressBarVisibility(boolean isVisible) {
        runOnUiThread(() -> findViewById(R.id.indeterminateBar).setVisibility(isVisible ? ProgressBar.VISIBLE : ProgressBar.GONE));
    }

    private void setPageTitle() {
        String title = getResources().getString(R.string.tv_location) + ": " + location.name + " (#" + location.id + ")";
        ((TextView) findViewById(R.id.page_title)).setText(title);
    }

    private void setAbilityCharacteristics() {
        ((TextView) findViewById(R.id.tv_location_region)).setText(location.region.getName());
    }
}