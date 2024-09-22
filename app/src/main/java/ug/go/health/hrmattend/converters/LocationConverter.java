package ug.go.health.hrmattend.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import ug.go.health.hrmattend.models.Location;

public class LocationConverter {
    @TypeConverter
    public static Location fromString(String value) {
        return value == null ? null : new Gson().fromJson(value, Location.class);
    }

    @TypeConverter
    public static String locationToString(Location location) {
        return location == null ? null : new Gson().toJson(location);
    }
}