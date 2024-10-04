package com.task09;


import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;
import java.util.UUID;

@DynamoDbBean
public class WeatherForecast {

    private String id;
    private Forecast forecast;

    public WeatherForecast() {
        this.id = UUID.randomUUID().toString();
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public static class Forecast {
        private double elevation;
        private double generationtimeMs;
        private Hourly hourly;
        private HourlyUnits hourlyUnits;
        private double latitude;
        private double longitude;
        private String timezone;
        private String timezoneAbbreviation;
        private int utcOffsetSeconds;

        public Forecast() {
        }

        public Forecast(double elevation, double generationtimeMs, Hourly hourly, HourlyUnits hourlyUnits, double latitude, double longitude, String timezone, String timezoneAbbreviation, int utcOffsetSeconds) {
            this.elevation = elevation;
            this.generationtimeMs = generationtimeMs;
            this.hourly = hourly;
            this.hourlyUnits = hourlyUnits;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timezone = timezone;
            this.timezoneAbbreviation = timezoneAbbreviation;
            this.utcOffsetSeconds = utcOffsetSeconds;
        }

        public double getElevation() {
            return elevation;
        }

        public void setElevation(double elevation) {
            this.elevation = elevation;
        }


        public double getGenerationtimeMs() {
            return generationtimeMs;
        }

        public void setGenerationtimeMs(double generationtimeMs) {
            this.generationtimeMs = generationtimeMs;
        }


        public Hourly getHourly() {
            return hourly;
        }

        public void setHourly(Hourly hourly) {
            this.hourly = hourly;
        }


        public HourlyUnits getHourlyUnits() {
            return hourlyUnits;
        }

        public void setHourlyUnits(HourlyUnits hourlyUnits) {
            this.hourlyUnits = hourlyUnits;
        }


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


        public int getUtcOffsetSeconds() {
            return utcOffsetSeconds;
        }

        public void setUtcOffsetSeconds(int utcOffsetSeconds) {
            this.utcOffsetSeconds = utcOffsetSeconds;
        }
    }

    public static class Hourly {
        private List<Double> temperature2m;
        private List<String> time;


        public List<Double> getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(List<Double> temperature2m) {
            this.temperature2m = temperature2m;
        }


        public List<String> getTime() {
            return time;
        }

        public void setTime(List<String> time) {
            this.time = time;
        }
    }

    public static class HourlyUnits {
        private String temperature2m;
        private String time;


        public String getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(String temperature2m) {
            this.temperature2m = temperature2m;
        }


        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}