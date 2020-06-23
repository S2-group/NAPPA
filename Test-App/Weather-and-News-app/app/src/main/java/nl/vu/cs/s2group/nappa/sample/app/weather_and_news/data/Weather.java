package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.data;

public class Weather {

    public MainData main;

    public static class MainData {
        public Float temp;

        public Float getTemp() {
            return temp - 273;
        }
    }

}
