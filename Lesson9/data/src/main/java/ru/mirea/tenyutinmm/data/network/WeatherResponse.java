package ru.mirea.tenyutinmm.data.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("current_condition")
    public List<CurrentCondition> currentCondition;

    public static class CurrentCondition {
        @SerializedName("temp_C")
        public String tempC;

        @SerializedName("weatherDesc")
        public List<WeatherDesc> weatherDesc;
    }

    public static class WeatherDesc {
        public String value;
    }
}