package com.nejatboy.adisyonum.Model;

public class SingletonGarson {

    private String garsonId;
    private String garsonAd;
    private String garsonKullaniciAd;
    private String garsonSifre;
    private String restoranId;

    private static SingletonGarson singletonGarson;

    public static SingletonGarson getInstance() {
        if (singletonGarson == null ) {
            singletonGarson = new SingletonGarson();
        }
        return singletonGarson;
    }






    public String getGarsonId() {
        return garsonId;
    }

    public void setGarsonId(String garsonId) {
        this.garsonId = garsonId;
    }

    public String getGarsonAd() {
        return garsonAd;
    }

    public void setGarsonAd(String garsonAd) {
        this.garsonAd = garsonAd;
    }

    public String getGarsonKullaniciAd() {
        return garsonKullaniciAd;
    }

    public void setGarsonKullaniciAd(String garsonKullaniciAd) {
        this.garsonKullaniciAd = garsonKullaniciAd;
    }

    public String getGarsonSifre() {
        return garsonSifre;
    }

    public void setGarsonSifre(String garsonSifre) {
        this.garsonSifre = garsonSifre;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }
}
