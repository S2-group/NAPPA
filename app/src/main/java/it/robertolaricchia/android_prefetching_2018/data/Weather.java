package it.robertolaricchia.android_prefetching_2018.data;

public class Weather {

    public MainData main;

    public static class MainData {
        public Float temp;

        public Float getTemp() {
            return temp-273;
        }
    }

}
