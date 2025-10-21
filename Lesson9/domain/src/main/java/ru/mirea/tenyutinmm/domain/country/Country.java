package ru.mirea.tenyutinmm.domain.country;

public class Country {
    public final String name;
    public final String capital;
    public final String flagUrl;

    public Country(String name, String capital, String flagUrl) {
        this.name = name;
        this.capital = capital;
        this.flagUrl = flagUrl;
    }
}