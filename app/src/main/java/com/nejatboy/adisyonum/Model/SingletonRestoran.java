package com.nejatboy.adisyonum.Model;

public class SingletonRestoran {

    private String restoranId;
    private String restoranAd;
    private int masaSayisi;
    private String yoneticiId;
    private String kasaKullaniciAdi;
    private String kasaSifre;


    private static  SingletonRestoran singletonRestoran;

    public static SingletonRestoran getInstance() {
        if (singletonRestoran == null) {
            singletonRestoran = new SingletonRestoran();
        }
        return singletonRestoran;
    }


    public String getRestoranId() {
        return restoranId;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }

    public String getRestoranAd() {
        return restoranAd;
    }

    public void setRestoranAd(String restoranAd) {
        this.restoranAd = restoranAd;
    }

    public int getMasaSayisi() {
        return masaSayisi;
    }

    public void setMasaSayisi(int masaSayisi) {
        this.masaSayisi = masaSayisi;
    }

    public String getYoneticiId() {
        return yoneticiId;
    }

    public void setYoneticiId(String yoneticiId) {
        this.yoneticiId = yoneticiId;
    }

    public String getKasaKullaniciAdi() {
        return kasaKullaniciAdi;
    }

    public void setKasaKullaniciAdi(String kasaKullaniciAdi) {
        this.kasaKullaniciAdi = kasaKullaniciAdi;
    }

    public String getKasaSifre() {
        return kasaSifre;
    }

    public void setKasaSifre(String kasaSifre) {
        this.kasaSifre = kasaSifre;
    }

    public static SingletonRestoran getSingletonRestoran() {
        return singletonRestoran;
    }

    public static void setSingletonRestoran(SingletonRestoran singletonRestoran) {
        SingletonRestoran.singletonRestoran = singletonRestoran;
    }
}
