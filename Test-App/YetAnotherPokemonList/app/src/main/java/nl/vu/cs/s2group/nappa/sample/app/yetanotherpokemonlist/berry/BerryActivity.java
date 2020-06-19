package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.ViewUtil;

public class BerryActivity extends AppCompatActivity {
    private static final String LOG_TAG = BerryActivity.class.getSimpleName();
    Berry berry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berry);
        toggleProgressBarVisibility(true);
        BerryAPI.makeRequest(getIntent().getStringExtra("url"), this::handleResponse);
    }

    private void handleResponse(Berry berry) {
        this.berry = berry;

        setPageTitle();
        toggleProgressBarVisibility(false);
        setAbilityCharacteristics();

        ViewUtil.addNamedAPIResourceListToUI(this, R.id.ll_berry_flavors, berry.flavors, "getFlavorWithPotency");
    }

    private void toggleProgressBarVisibility(boolean isVisible) {
        runOnUiThread(() -> findViewById(R.id.indeterminateBar).setVisibility(isVisible ? ProgressBar.VISIBLE : ProgressBar.GONE));
    }

    private void setPageTitle() {
        ((TextView) findViewById(R.id.page_title)).setText(berry.name);
    }

    private void setAbilityCharacteristics() {
        String grownTimeStr = String.format(Config.LOCALE, "%d", berry.growth_time) + " h";
        ((TextView) findViewById(R.id.tv_berry_grown_time)).setText(grownTimeStr);

        String maxHarvestStr = String.format(Config.LOCALE, "%d", berry.max_harvest);
        ((TextView) findViewById(R.id.tv_berry_max_harvest)).setText(maxHarvestStr);

        String naturalGiftPowerStr = String.format(Config.LOCALE, "%d", berry.natural_gift_power);
        ((TextView) findViewById(R.id.tv_berry_natural_gift_power)).setText(naturalGiftPowerStr);

        String sizeStr = String.format(Config.LOCALE, "%d", berry.size) + " mm";
        ((TextView) findViewById(R.id.tv_berry_size)).setText(sizeStr);

        String smoothnessStr = String.format(Config.LOCALE, "%d", berry.smoothness);
        ((TextView) findViewById(R.id.tv_berry_smoothness)).setText(smoothnessStr);

        String soilDrynessStr = String.format(Config.LOCALE, "%d", berry.soil_dryness);
        ((TextView) findViewById(R.id.tv_berry_soil_dryness)).setText(soilDrynessStr);

        ((TextView) findViewById(R.id.tv_berry_natural_gift_power)).setText(berry.getNaturalGiftType().getName());
        ((TextView) findViewById(R.id.tv_berry_item)).setText(berry.item.getName());
        ((TextView) findViewById(R.id.tv_berry_firmness)).setText(berry.firmness.getName());
    }
}