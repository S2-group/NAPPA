package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

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

public class DefaultAdapter extends ArrayAdapter<DefaultApiModel> {
    public DefaultAdapter(@NonNull Context context, int resource, @NonNull List<DefaultApiModel> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        DefaultApiModel model = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_default, parent, false);
        }
        // Lookup view for data population
        TextView name = convertView.findViewById(R.id.defaultItemName);
        // Populate the data into the template view using the data object
        name.setText(Objects.requireNonNull(model).getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
