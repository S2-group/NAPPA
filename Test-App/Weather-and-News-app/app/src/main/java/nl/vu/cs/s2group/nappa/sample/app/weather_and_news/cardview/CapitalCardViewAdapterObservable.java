package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.cardview;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.reactivex.Observable;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.CityDetailsActivity;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.data.Capital;
import nl.vu.cs.s2group.nappa.PrefetchingLib;

public class CapitalCardViewAdapterObservable extends RecyclerView.Adapter<CapitalCardViewAdapterObservable.NewsViewHolder> {

    public Observable<Capital> capitalList;

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_capitals, parent, false);
        return new NewsViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        //Capital capital = capitalList.get(position);
        Capital capital = capitalList.elementAt(position).blockingGet();
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
        if (capitalList!=null) {
            return capitalList.count().blockingGet().intValue();
        }
        return 0;
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
