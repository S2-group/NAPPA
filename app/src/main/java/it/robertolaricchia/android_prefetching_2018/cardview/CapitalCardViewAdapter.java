package it.robertolaricchia.android_prefetching_2018.cardview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.robertolaricchia.android_prefetching_2018.CityDetailsActivity;
import it.robertolaricchia.android_prefetching_2018.R;
import it.robertolaricchia.android_prefetching_2018.WeatherActivity;
import it.robertolaricchia.android_prefetching_2018.data.Capital;
import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;

public class CapitalCardViewAdapter extends RecyclerView.Adapter<CapitalCardViewAdapter.NewsViewHolder> {

    public List<Capital> capitalList = new ArrayList<Capital>();

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_capitals, parent, false);
        return new NewsViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Capital capital = capitalList.get(position);
        holder.city.setText(capital.city);
        holder.country.setText(capital.country);
        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CityDetailsActivity.class);
            intent.putExtra("capital", capital.city);
            PrefetchingLib.notifyExtra("capital", capital.city);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return capitalList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView country;
        TextView city;
        View view;

        NewsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            country = itemView.findViewById(R.id.text_country);
            city = itemView.findViewById(R.id.text_city);
        }
    }
}
