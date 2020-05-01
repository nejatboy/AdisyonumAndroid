package com.nejatboy.adisyonum.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

public class Rapor {

    private String raporId;
    private String restoranId;
    private HashMap<String, Double> hesaplar;
    private HashMap<String, Double> garsonSatislari;
    private Double ciro;

    public Rapor(String raporId, String restoranId, HashMap<String, Double> hesaplar, HashMap<String, Double> garsonSatislari, Double ciro) {
        this.raporId = raporId;
        this.restoranId = restoranId;
        this.hesaplar = hesaplar;
        this.garsonSatislari = garsonSatislari;
        this.ciro = ciro;
    }


    public String getRaporId() {
        return raporId;
    }

    public void setRaporId(String raporId) {
        this.raporId = raporId;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }

    public HashMap<String, Double> getHesaplar() {
        return hesaplar;
    }

    public void setHesaplar(HashMap<String, Double> hesaplar) {
        this.hesaplar = hesaplar;
    }

    public HashMap<String, Double> getGarsonSatislari() {
        return garsonSatislari;
    }

    public void setGarsonSatislari(HashMap<String, Double> garsonSatislari) {
        this.garsonSatislari = garsonSatislari;
    }

    public Double getCiro() {
        return ciro;
    }

    public void setCiro(Double ciro) {
        this.ciro = ciro;
    }
}
