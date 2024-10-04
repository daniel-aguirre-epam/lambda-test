package com.task09;

import java.util.List;

public class WeatherResponse {

    private double latitude;
    private double longitude;
    private double generationtimeMs;
    private int utcOffsetSeconds;
    private String timezone;
    private String timezoneAbbreviation;
    private double elevation;
    private CurrentUnits currentUnits;
    private Current current;
    private WeatherForecast.HourlyUnits hourlyUnits;
    private WeatherForecast.Hourly hourly;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getGenerationtimeMs() {
        return generationtimeMs;
    }

    public void setGenerationtimeMs(double generationtimeMs) {
        this.generationtimeMs = generationtimeMs;
    }

    public int getUtcOffsetSeconds() {
        return utcOffsetSeconds;
    }

    public void setUtcOffsetSeconds(int utcOffsetSeconds) {
        this.utcOffsetSeconds = utcOffsetSeconds;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezoneAbbreviation() {
        return timezoneAbbreviation;
    }

    public void setTimezoneAbbreviation(String timezoneAbbreviation) {
        this.timezoneAbbreviation = timezoneAbbreviation;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public CurrentUnits getCurrentUnits() {
        return currentUnits;
    }

    public void setCurrentUnits(CurrentUnits currentUnits) {
        this.currentUnits = currentUnits;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public WeatherForecast.HourlyUnits getHourlyUnits() {
        return hourlyUnits;
    }

    public void setHourlyUnits(WeatherForecast.HourlyUnits hourlyUnits) {
        this.hourlyUnits = hourlyUnits;
    }

    public WeatherForecast.Hourly getHourly() {
        return hourly;
    }

    public void setHourly(WeatherForecast.Hourly hourly) {
        this.hourly = hourly;
    }

    public static class CurrentUnits {
        private String time;
        private String interval;
        private String temperature2m;
        private String windSpeed10m;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(String temperature2m) {
            this.temperature2m = temperature2m;
        }

        public String getWindSpeed10m() {
            return windSpeed10m;
        }

        public void setWindSpeed10m(String windSpeed10m) {
            this.windSpeed10m = windSpeed10m;
        }
    }

    public static class Current {
        private String time;
        private int interval;
        private double temperature2m;
        private double windSpeed10m;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public double getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(double temperature2m) {
            this.temperature2m = temperature2m;
        }

        public double getWindSpeed10m() {
            return windSpeed10m;
        }

        public void setWindSpeed10m(double windSpeed10m) {
            this.windSpeed10m = windSpeed10m;
        }
    }

//    public static class HourlyUnits {
//        private String time;
//        private String temperature2m;
//        private String relativeHumidity2m;
//        private String windSpeed10m;
//
//        public String getTime() {
//            return time;
//        }
//
//        public void setTime(String time) {
//            this.time = time;
//        }
//
//        public String getTemperature2m() {
//            return temperature2m;
//        }
//
//        public void setTemperature2m(String temperature2m) {
//            this.temperature2m = temperature2m;
//        }
//
//        public String getRelativeHumidity2m() {
//            return relativeHumidity2m;
//        }
//
//        public void setRelativeHumidity2m(String relativeHumidity2m) {
//            this.relativeHumidity2m = relativeHumidity2m;
//        }
//
//        public String getWindSpeed10m() {
//            return windSpeed10m;
//        }
//
//        public void setWindSpeed10m(String windSpeed10m) {
//            this.windSpeed10m = windSpeed10m;
//        }
//    }
//
//    public static class Hourly {
//        private List<String> time;
//        private List<Double> temperature2m;
//        private List<Integer> relativeHumidity2m;
//        private List<Double> windSpeed10m;
//
//        public List<String> getTime() {
//            return time;
//        }
//
//        public void setTime(List<String> time) {
//            this.time = time;
//        }
//
//        public List<Double> getTemperature2m() {
//            return temperature2m;
//        }
//
//        public void setTemperature2m(List<Double> temperature2m) {
//            this.temperature2m = temperature2m;
//        }
//
//        public List<Integer> getRelativeHumidity2m() {
//            return relativeHumidity2m;
//        }
//
//        public void setRelativeHumidity2m(List<Integer> relativeHumidity2m) {
//            this.relativeHumidity2m = relativeHumidity2m;
//        }
//
//        public List<Double> getWindSpeed10m() {
//            return windSpeed10m;
//        }
//
//        public void setWindSpeed10m(List<Double> windSpeed10m) {
//            this.windSpeed10m = windSpeed10m;
//        }
//    }

}