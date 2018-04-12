package com.vlad.location;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Vlad on 12.04.2018.
 */

@Dao
public interface LightDao {
    @Query("SELECT * FROM light GROUP BY id ORDER BY created_at DESC")
    List<Light> getAll();

    @Insert
    void insertAll(Light... lights);


}
