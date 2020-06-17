package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import java.util.List;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.Config;

public abstract class ApiRequest {
    private static final String LOG_TAG = ApiRequest.class.getSimpleName();

    protected String baseApiUrl;
    protected int currentPage;
    protected String lastPageUrl;
    protected ApiResponseWrapper wrapper;

    protected ApiRequest(String baseApiUrl) {
        this.baseApiUrl = Config.API_URL + baseApiUrl;
    }

    public ApiRequest() {
        currentPage = 1;
    }

    public void getInitialContent(Consumer<List<? extends ApiModel>> callback) {
        sendRequest(baseApiUrl, callback);
    }

    public void getFirstPage(Consumer<List<? extends ApiModel>> callback) {
        sendRequest(baseApiUrl, callback);
        currentPage = 1;
    }

    public void getLastPage(Consumer<List<? extends ApiModel>> callback) {
        sendRequest(lastPageUrl, callback);
        currentPage = getTotalPages();
    }

    public void getNext(Consumer<List<? extends ApiModel>> callback) {
        sendRequest(wrapper.getNext(), callback);
        currentPage++;
    }

    public void getPrevious(Consumer<List<? extends ApiModel>> callback) {
        sendRequest(wrapper.getPrevious(), callback);
        currentPage--;
    }

    public boolean hasNext() {
        return wrapper.getNext() != null;
    }

    public int getTotalItems() {
        return wrapper.getCount();
    }

    public int getTotalPages() {
        return (int) Math.ceil(wrapper.getCount() / (double) Config.ITEMS_PER_PAGE);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean hasPrevious() {
        return wrapper.getPrevious() != null;
    }

    protected void makeLastPageUrl() {
        int offset = wrapper.getCount() - (wrapper.getCount() % 20);
        lastPageUrl = baseApiUrl + "?offset=" + offset + "&limit=20";
    }

    protected abstract void sendRequest(String url, Consumer<List<? extends ApiModel>> callback);
}
