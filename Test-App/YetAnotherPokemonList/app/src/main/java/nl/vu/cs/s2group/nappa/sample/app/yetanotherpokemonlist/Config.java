package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist;

public abstract class Config {
    private Config() {
        throw new IllegalStateException("Config is a class with constants and should be instantiated!");
    }

    public static final String API_URL = "https://pokeapi.co/api/v2/";
}
