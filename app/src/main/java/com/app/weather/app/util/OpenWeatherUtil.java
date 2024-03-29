package com.app.weather.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.Toast;

import com.app.weather.app.dto.OpenWeatherDataResponseDto;
import com.app.weather.app.dto.OpenWeatherGeoResponseDto;
import com.app.weather.app.model.Cord;
import com.app.weather.app.model.Current;
import com.app.weather.app.model.GeoDetails;
import com.app.weather.app.model.Main;
import com.app.weather.app.model.WeatherDetailsBase;
import com.app.weather.app.model.WeatherDetailsCurrent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class OpenWeatherUtil {

    private static OpenWeatherUtil openWeatherUtil = null;

    public static OpenWeatherUtil getInstance() {
        if (openWeatherUtil == null) {
            openWeatherUtil = new OpenWeatherUtil();
        }

        return openWeatherUtil;
    }

    public GeoDetails geoDetailsMapper(OpenWeatherGeoResponseDto openWeatherGeoResponseDto) {
        return new GeoDetails(
                openWeatherGeoResponseDto.getCountry(),
                openWeatherGeoResponseDto.getName(),
                new Cord(openWeatherGeoResponseDto.getLon(), openWeatherGeoResponseDto.getLat())
        );
    }

    public WeatherDetailsCurrent weatherDetailsCurrentMapper(OpenWeatherDataResponseDto openWeatherDataResponseDto) {
        Current current = openWeatherDataResponseDto.getCurrent();

        return new WeatherDetailsCurrent(
                new Main(current.getTemp(), current.getPressure(), current.getWindSpeed(), current.getHumidity()),
                current.getWeatherList()[0].getIcon(),
                current.getDt(),
                FileStorageUtil.getInstance().getLastSelectedUnitSystem(),
                current.getVisibility(),
                openWeatherDataResponseDto.getTimezoneOffset()
        );
    }

    public List<WeatherDetailsBase> weatherDetailsBaseListMapper(OpenWeatherDataResponseDto openWeatherDataResponseDto) {
        return Arrays.stream(openWeatherDataResponseDto.getDailyList()).sequential().map(daily -> new WeatherDetailsBase(
                new Main(daily.getTemp().getDay(), daily.getPressure(), daily.getWindSpeed(), daily.getHumidity()),
                daily.getWeatherList()[0].getIcon(),
                daily.getDt(),
                FileStorageUtil.getInstance().getLastSelectedUnitSystem()
        )).collect(Collectors.toList());
    }

    public void getFavouriteCities(HashMap<String, List<String>> expandableListDetail) {
        List<String> cities = FileStorageUtil.getInstance().getFavouriteCityList().getFavouriteCities();
        expandableListDetail.put("Favourite cities", cities);
    }

    public void getUnitSystems(HashMap<String, List<String>> expandableListDetail) {
        List<String> unitSystems = Arrays.asList("Metric", "Imperial");
        expandableListDetail.put("Unit system", unitSystems);
    }

    public void getIntervals(HashMap<String, List<String>> expandableListDetail) {
        List<String> intervals = Arrays.asList("1 minute", "15 minutes", "30 minutes", "60 minutes");
        expandableListDetail.put("Refreshing interval", intervals);
    }

    public boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (networkCapabilities == null) {
            return false;
        }

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("NETWORK", "WIFI-CONNECTION");
            return true;
        }

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("NETWORK", "ETHERNET-CONNECTION");
            return true;
        }

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("NETWORK", "CELLULAR-CONNECTION");
            return true;
        }

        Log.i("NETWORK", "NO-CONNECTION");
        return false;
    }

    public void showToast(Context context, String text) {
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public String windSpeedUnitSystemConverter(double value, String toUnitSystem, String separatedSign) {
        if ("Imperial".equals(toUnitSystem)) {
            return String.format("%02.02f", value * 2.23694) + separatedSign + "mph";
        } else {
            return String.format("%02.02f", value) + separatedSign + "m/s";
        }
    }

    public String temperatureUnitSystemConverter(double value, String toUnitSystem, String separatedSign) {
        if ("Imperial".equals(toUnitSystem)) {
            return String.format("%02.02f", (value * 9 / 5) + 32) + separatedSign + "°F";
        } else {
            return String.format("%02.02f", value) + separatedSign + "°C";
        }
    }
}
