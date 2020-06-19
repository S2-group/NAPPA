package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#berry
 */
public class Berry {
    int id;
    String name;
    int growth_time;
    int max_harvest;
    int natural_gift_power;
    int size;
    int smoothness;
    int soil_dryness;
    NamedAPIResource firmness;
    List<BerryFlavorMap> flavors;
    NamedAPIResource item;
    NamedAPIResource natural_gift_type;

    @NonNull
    @Override
    public String toString() {
        return "Berry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", growth_time=" + growth_time +
                ", max_harvest=" + max_harvest +
                ", natural_gift_power=" + natural_gift_power +
                ", size=" + size +
                ", smoothness=" + smoothness +
                ", soil_dryness=" + soil_dryness +
                ", firmness=" + firmness +
                ", flavors=" + flavors +
                ", item=" + item +
                ", natural_gift_type=" + natural_gift_type +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGrowth_time() {
        return growth_time;
    }

    public int getMax_harvest() {
        return max_harvest;
    }

    public int getNatural_gift_power() {
        return natural_gift_power;
    }

    public int getSize() {
        return size;
    }

    public int getSmoothness() {
        return smoothness;
    }

    public int getSoil_dryness() {
        return soil_dryness;
    }

    public NamedAPIResource getFirmness() {
        return firmness;
    }

    public List<BerryFlavorMap> getFlavors() {
        return flavors;
    }

    public NamedAPIResource getItem() {
        return item;
    }

    public NamedAPIResource getNatural_gift_type() {
        return natural_gift_type;
    }
}
