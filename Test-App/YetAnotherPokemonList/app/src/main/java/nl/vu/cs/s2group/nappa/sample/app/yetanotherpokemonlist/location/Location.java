package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels.GenerationGameIndex;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels.Name;

/**
 * Representation of https://pokeapi.co/docs/v2#location
 */
public class Location {
    int id;
    String name;
    NamedAPIResource region;
    List<Name> names;
    GenerationGameIndex game_indices;
    List<NamedAPIResource> areas;

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region=" + region +
                ", names=" + names +
                ", game_indices=" + game_indices +
                ", areas=" + areas +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public NamedAPIResource getRegion() {
        return region;
    }

    public List<Name> getNames() {
        return names;
    }

    public GenerationGameIndex getGameIndices() {
        return game_indices;
    }

    public List<NamedAPIResource> getAreas() {
        return areas;
    }
}
