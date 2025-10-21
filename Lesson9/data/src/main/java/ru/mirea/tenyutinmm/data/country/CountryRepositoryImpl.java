package ru.mirea.tenyutinmm.data.country;

import java.util.ArrayList;
import java.util.List;
import ru.mirea.tenyutinmm.domain.country.Country;
import ru.mirea.tenyutinmm.domain.country.CountryRepository;

public class CountryRepositoryImpl implements CountryRepository {
    @Override
    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Бразилия", "Бразилиа", "https://upload.wikimedia.org/wikipedia/commons/0/05/Flag_of_Brazil.svg"));
        countries.add(new Country("Китай", "Пекин", "https://upload.wikimedia.org/wikipedia/commons/f/fa/Flag_of_the_People%27s_Republic_of_China.svg"));
        countries.add(new Country("Индия", "Нью-Дели", "https://upload.wikimedia.org/wikipedia/commons/4/41/Flag_of_India.svg"));
        countries.add(new Country("Россия", "Москва", "https://upload.wikimedia.org/wikipedia/commons/f/f3/Flag_of_Russia.svg"));
        countries.add(new Country("ЮАР", "Претория", "https://upload.wikimedia.org/wikipedia/commons/a/af/Flag_of_South_Africa.svg"));
        return countries;
    }
}