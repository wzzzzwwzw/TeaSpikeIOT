package com.taimoorsikander.cityguide.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TelemetriaViewModel extends AndroidViewModel {

    private TelemetriaRepository tmRepository;

    private LiveData<List<TelemetriaEntity>> ldList;

    /**
     * Constructor
     *
     * @param application app
     */
    public TelemetriaViewModel(Application application) {
        super(application);
        tmRepository = new TelemetriaRepository(application);
        ldList = tmRepository.getAll();
    }

    public LiveData<List<TelemetriaEntity>> getAll() {
        return ldList;
    }

    /**
     * Se inserta el registro (si no está ya)
     * @param item
     */
    public void insert(TelemetriaEntity item) {
        if(!exists(item)) tmRepository.insert(item);
    }

    public void deleteAll() {
        tmRepository.deleteAll();
    }

    /**
     * Devuelve true cuando en BD ya existe un registro en con el mismo timestamp (clave)
     * que el del item que se pasa como parametro
     *
     * @param item
     * @return
     */
    public boolean exists(TelemetriaEntity item) {
        List<TelemetriaEntity> listTme = ldList.getValue();

        int count = 0;
        boolean b = false;
        while ((listTme.size() > count) && !b) {
            b = listTme.get(count).getTimestamp().equals(item.getTimestamp());
            count++;
        }
        return b;
    }

    public int count() {
        return ldList.getValue().size()+1;
    }

    public void delete(TelemetriaEntity item) {
        tmRepository.delete(item);
    }


}
