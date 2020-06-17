package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public class DefaultActivity extends AppCompatActivity {
    private String logTag;
    private String apiUrl;
    private DefaultAdapter adapter;
    private DefaultApi api;

    public DefaultActivity(String logTag, String apiUrl) {
        super(R.layout.activity_default);
        this.logTag = logTag;
        this.apiUrl = apiUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
        api = new DefaultApi(apiUrl, logTag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beforeRequest();
        api.getInitialContent(this::handleResponse);
    }

    public void onFirst(View view) {
        beforeRequest();
        api.getFirstPage(this::handleResponse);
    }


    public void onPrevious(View view) {
        beforeRequest();
        api.getPrevious(this::handleResponse);
    }

    public void onNext(View view) {
        beforeRequest();
        api.getNext(this::handleResponse);
    }

    public void onLast(View view) {
        beforeRequest();
        api.getLastPage(this::handleResponse);
    }

    private void handleResponse(List<DefaultApiModel> pokemons) {
        runOnUiThread(() -> {
            if (adapter == null)
                adapter = new DefaultAdapter(this, R.layout.activity_pokemons, pokemons);
            else {
                adapter.clear();
                adapter.addAll(pokemons);
            }
            ListView listView = findViewById(R.id.default_list);
            listView.setAdapter(adapter);
            setPaginationButtonState();
            setProgressBarState(false);
            setCurrentPage();
            setTotalItems();
        });
    }

    private void beforeRequest() {
        disableButtonsOnLoad();
        setProgressBarState(true);
    }

    private void setProgressBarState(boolean state) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void disableButtonsOnLoad() {
        findViewById(R.id.btn_first).setEnabled(false);
        findViewById(R.id.btn_previous).setEnabled(false);
        findViewById(R.id.btn_next).setEnabled(false);
        findViewById(R.id.btn_last).setEnabled(false);
    }

    private void setPaginationButtonState() {
        findViewById(R.id.btn_first).setEnabled(api.hasPrevious());
        findViewById(R.id.btn_previous).setEnabled(api.hasPrevious());
        findViewById(R.id.btn_next).setEnabled(api.hasNext());
        findViewById(R.id.btn_last).setEnabled(api.hasNext());
    }

    private void setTotalItems() {
        String str = api.getTotalItems() + " Pokemons";
        ((TextView) findViewById(R.id.tv_total_itens)).setText(str);

    }

    private void setCurrentPage() {
        String str = "Page " + api.getCurrentPage() +
                " of " + api.getTotalPages();
        ((TextView) findViewById(R.id.tv_current_page)).setText(str);
    }
}