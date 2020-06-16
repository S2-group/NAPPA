package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.model.pokemon.Pokemon;

/**
 * Implements an adapter to list Pokemons in the UI
 */
public class PokemonsAdapter extends ArrayAdapter<Pokemon> {
    public PokemonsAdapter(@NonNull Context context, int resource, @NonNull List<Pokemon> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Object row = getItem(position);
        Pokemon pokemon = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pokemon, parent, false);
        }
        // Lookup view for data population
        TextView pokemonName = convertView.findViewById(R.id.pokemonName);
        // Populate the data into the template view using the data object
        pokemonName.setText(pokemon.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
