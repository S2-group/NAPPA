package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PokemonsViewModel extends ViewModel {
    private Executor executor;
//    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<Pokemon>> articleLiveData;

    private void init() {
        executor = Executors.newFixedThreadPool(5);

        PokemonsDataFactory pokemonsDataFactory = new PokemonsDataFactory();
//        networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
//                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(20).build();

        articleLiveData = (new LivePagedListBuilder<>(pokemonsDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<PagedList<Pokemon>> getArticleLiveData() {
        return articleLiveData;
    }
}
