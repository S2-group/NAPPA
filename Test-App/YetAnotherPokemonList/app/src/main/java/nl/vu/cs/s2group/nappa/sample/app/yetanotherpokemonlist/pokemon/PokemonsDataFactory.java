package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class PokemonsDataFactory extends DataSource.Factory<Integer, Pokemon> {
    private MutableLiveData<PokemonDataSource> mutableLiveData;
    private PokemonDataSource pokemonDataSource;

    public PokemonsDataFactory() {
        this.mutableLiveData = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<Integer, Pokemon> create() {
        pokemonDataSource = new PokemonDataSource();
        mutableLiveData.postValue(pokemonDataSource);
        return pokemonDataSource;
    }

    public MutableLiveData<PokemonDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
