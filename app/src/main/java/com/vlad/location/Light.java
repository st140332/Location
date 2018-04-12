package com.vlad.location;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Vlad on 12.04.2018.
 */

@Entity
public class Light {
    public Light(double latitude, double longitude, double light, String createdAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.light = light;
        this.createdAt = createdAt;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "coord_lat")
    public double latitude;
    @ColumnInfo(name = "coord_lon")
    public double longitude;
    @ColumnInfo(name = "light")
    public double light;
    @ColumnInfo(name = "created_at")
    public String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
