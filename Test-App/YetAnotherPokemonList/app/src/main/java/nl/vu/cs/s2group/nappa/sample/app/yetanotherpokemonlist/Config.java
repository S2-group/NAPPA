package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

public abstract class Config {
    public static final String API_URL = "https://pokeapi.co/api/v2/";

    private Config() {
        throw new IllegalStateException("Config is a class with constants and should be instantiated!");
    }
}
