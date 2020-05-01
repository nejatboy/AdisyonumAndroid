package com.nejatboy.adisyonum.Model;

import java.io.Serializable;

public class Garson implements Serializable {

    private String garsonId;
    private String garsonAd;
    private String garsonKullaniciAd;
    private String garsonSifre;
    private String restoranId;

    public Garson(String garsonId, String garsonAd, String garsonKullaniciAd, String garsonSifre, String restoranId) {
        this.garsonId = garsonId;
        this.garsonAd = garsonAd;
        this.garsonKullaniciAd = garsonKullaniciAd;
        this.garsonSifre = garsonSifre;
        this.restoranId = restoranId;
    }

    public String getGarsonId() {
        return garsonId;
    }

    public String getGarsonAd() {
        return garsonAd;
    }

    public String getGarsonKullaniciAd() {
        return garsonKullaniciAd;
    }

    public String getGarsonSifre() {
        return garsonSifre;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public void setGarsonId(String garsonId) {
        this.garsonId = garsonId;
    }

    public void setGarsonAd(String garsonAd) {
        this.garsonAd = garsonAd;
    }

    public void setGarsonKullaniciAd(String garsonKullaniciAd) {
        this.garsonKullaniciAd = garsonKullaniciAd;
    }

    public void setGarsonSifre(String garsonSifre) {
        this.garsonSifre = garsonSifre;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }
}
