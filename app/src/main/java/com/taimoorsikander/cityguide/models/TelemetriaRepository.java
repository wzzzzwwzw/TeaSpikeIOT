package com.taimoorsikander.cityguide.models;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TelemetriaRepository {
    private ITelemetriaDAO iItemDAO;
    private LiveData<List<TelemetriaEntity>> ldList;

    /**
     * Constructor
     *
     * @param application app
     */
    public TelemetriaRepository(Application application) {
        TelemetriaRoomDatabase db = TelemetriaRoomDatabase.getDatabase(application);
        iItemDAO = db.IDao();
        ldList = iItemDAO.getAll();
    }

    public LiveData<List<TelemetriaEntity>> getAll() {
        return ldList;
    }

    public long insert(TelemetriaEntity item) {
        return iItemDAO.insert(item);
    }

    public void deleteAll() {
        iItemDAO.deleteAll();
    }

    public void delete(TelemetriaEntity item)  {
        iItemDAO.delete(item);
    }
}
