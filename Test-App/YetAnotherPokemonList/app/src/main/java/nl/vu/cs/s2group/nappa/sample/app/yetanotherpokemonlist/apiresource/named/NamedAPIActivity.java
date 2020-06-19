package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public abstract class NamedAPIActivity extends AppCompatActivity {
    private String logTag;
    private String apiUrl;
    private NamedAPIAdapter adapter;
    private NamedAPI api;
    private int contentLayoutId;

    public NamedAPIActivity(int contentLayoutId, String logTag, String apiUrl) {
        super(contentLayoutId);
        this.contentLayoutId = contentLayoutId;
        this.logTag = logTag;
        this.apiUrl = apiUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentLayoutId);
        api = new NamedAPI(apiUrl, logTag);
        setHeaderText();
        setOnItemClickListener();
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

    private void handleResponse(List<NamedAPIResource> list) {
        runOnUiThread(() -> {
            if (adapter == null)
                adapter = new NamedAPIAdapter(this, contentLayoutId, list);
            else {
                adapter.clear();
                adapter.addAll(list);
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
        ProgressBar progressBar = findViewById(R.id.indeterminateBar);
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

    protected void setTotalItems(String label) {
        String str = api.getTotalItems() + " " + label;
        ((TextView) findViewById(R.id.tv_total_itens)).setText(str);
    }

    protected abstract void setTotalItems();

    protected void setHeaderText(String title) {
        ((TextView) findViewById(R.id.default_header)).setText(title);
    }

    protected abstract void setHeaderText();

    private void setCurrentPage() {
        String str = "Page " + api.getCurrentPage() +
                " of " + api.getTotalPages();
        ((TextView) findViewById(R.id.tv_current_page)).setText(str);
    }

    private void setOnItemClickListener() {
        ListView list = findViewById(R.id.default_list);
        list.setOnItemClickListener((adapterView, view, i, l) -> onItemClickListener((String) view.getTag()));
    }

    protected abstract void onItemClickListener(String url);
}