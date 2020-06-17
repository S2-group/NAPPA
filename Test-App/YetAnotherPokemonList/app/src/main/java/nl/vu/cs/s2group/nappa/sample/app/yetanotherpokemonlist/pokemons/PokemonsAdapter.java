package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultApiModel;

/**
 * Implements an adapter to list Pokemons in the UI
 */
public class PokemonsAdapter extends ArrayAdapter<DefaultApiModel> {
    public PokemonsAdapter(@NonNull Context context, int resource, @NonNull List<DefaultApiModel> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        DefaultApiModel pokemon = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pokemon, parent, false);
        }
        // Lookup view for data population
        TextView pokemonName = convertView.findViewById(R.id.pokemonName);
        // Populate the data into the template view using the data object
        pokemonName.setText(Objects.requireNonNull(pokemon).getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
