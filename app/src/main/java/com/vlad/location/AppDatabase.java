package com.vlad.location;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Vlad on 12.04.2018.
 */
@Database(entities = {Light.class},version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LightDao lightDao();
}