package com.nejatboy.adisyonum.Model;

import java.util.Date;
import java.util.HashMap;

public class GunlukRapor {
    private String raporId;
    private String restoranId;
    private HashMap<String, Double> hesaplar;
    private HashMap<String, Double> garsonSatislari;
    private Double ciro;
    private Date tarih;


    public GunlukRapor(String raporId, String restoranId, HashMap<String, Double> hesaplar, HashMap<String, Double> garsonSatislari, Double ciro, Date tarih) {
        this.raporId = raporId;
        this.restoranId = restoranId;
        this.hesaplar = hesaplar;
        this.garsonSatislari = garsonSatislari;
        this.ciro = ciro;
        this.tarih = tarih;
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

    public Date getTarih() {
        return tarih;
    }

    public void setTarih(Date tarih) {
        this.tarih = tarih;
    }
}
