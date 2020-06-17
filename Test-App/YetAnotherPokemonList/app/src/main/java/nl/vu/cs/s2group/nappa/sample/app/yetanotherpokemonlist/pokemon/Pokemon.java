package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

/**
 * Wrap the entries for the {@code /pokemon} API
 */
public class Pokemon implements Parcelable {
    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public static DiffUtil.ItemCallback<Pokemon> DIFF_CALLBACK = new DiffUtil.ItemCallback<Pokemon>() {
        @Override
        public boolean areItemsTheSame(@NonNull Pokemon oldItem, @NonNull Pokemon newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pokemon oldItem, @NonNull Pokemon newItem) {
            return oldItem.equals(newItem);
        }
    };
    /**
     * The name of the pokemon
     */
    String name;
    /**
     * The URL link containing information of the pokemon
     */
    String url;

    protected Pokemon(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return Objects.equals(name, pokemon.name) &&
                Objects.equals(url, pokemon.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }
}
