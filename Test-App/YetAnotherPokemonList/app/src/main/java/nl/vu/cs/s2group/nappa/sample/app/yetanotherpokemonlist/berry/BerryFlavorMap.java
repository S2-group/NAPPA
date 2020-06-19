package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.berry;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#berryflavormap
 */
public class BerryFlavorMap {
    int potency;
    NamedAPIResource flavor;

    @NonNull
    @Override
    public String toString() {
        return "BerryFlavorMap{" +
                "potency=" + potency +
                ", flavor=" + flavor +
                '}';
    }

    public int getPotency() {
        return potency;
    }

    public NamedAPIResource getFlavor() {
        return flavor;
    }

    public String getFlavorWithPotency() {
        return flavor.getName() + " (" + potency + " p)";
    }
}
