package ru.mirea.tenyutinmm.domain.weather;

public class Weather {
    public final String temperature;
    public final String description;

    public Weather(String temperature, String description) {
        this.temperature = temperature;
        this.description = description;
    }
}