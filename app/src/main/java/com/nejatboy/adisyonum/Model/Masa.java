package com.nejatboy.adisyonum.Model;

import java.util.List;

public class Masa {
    private String masaId;
    private int masaNo;
    private String restoranId;
    private Double masaTutar;
    private String garsonId;
    private  boolean masaAcik;
    private boolean masaYazdirildi;
    private List<String> urunler;


    public Masa(String masaId, int masaNo, String restoranId, Double masaTutar, String garsonId, boolean masaAcik, boolean masaYazdirildi, List<String> urunler) {
        this.masaId = masaId;
        this.masaNo = masaNo;
        this.restoranId = restoranId;
        this.masaTutar = masaTutar;
        this.garsonId = garsonId;
        this.masaAcik = masaAcik;
        this.masaYazdirildi = masaYazdirildi;
        this.urunler = urunler;
    }


    public String getMasaId() {
        return masaId;
    }

    public void setMasaId(String masaId) {
        this.masaId = masaId;
    }

    public int getMasaNo() {
        return masaNo;
    }

    public void setMasaNo(int masaNo) {
        this.masaNo = masaNo;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }

    public Double getMasaTutar() {
        return masaTutar;
    }

    public void setMasaTutar(Double masaTutar) {
        this.masaTutar = masaTutar;
    }

    public String getGarsonId() {
        return garsonId;
    }

    public void setGarsonId(String garsonId) {
        this.garsonId = garsonId;
    }

    public boolean isMasaAcik() {
        return masaAcik;
    }

    public void setMasaAcik(boolean masaAcik) {
        this.masaAcik = masaAcik;
    }

    public boolean isMasaYazdirildi() {
        return masaYazdirildi;
    }

    public void setMasaYazdirildi(boolean masaYazdirildi) {
        this.masaYazdirildi = masaYazdirildi;
    }

    public List<String> getUrunler() {
        return urunler;
    }

    public void setUrunler(List<String> urunler) {
        this.urunler = urunler;
    }
}
