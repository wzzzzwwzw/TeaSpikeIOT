package com.taimoorsikander.cityguide.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ITelemetriaDAO {
    @Query("SELECT * FROM " + TelemetriaEntity.TABLA)
    LiveData<List<TelemetriaEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(TelemetriaEntity item);

    @Query("DELETE FROM " + TelemetriaEntity.TABLA)
    void deleteAll();

    @Delete
    void delete(TelemetriaEntity item);
}
