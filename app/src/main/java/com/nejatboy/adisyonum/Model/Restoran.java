package com.nejatboy.adisyonum.Model;

import java.io.Serializable;

public class Restoran implements Serializable {

    private String restoranId;
    private String restoranAd;
    private int masaSayisi;
    private String yoneticiId;
    private String kasaKullaniciAdi;
    private String kasaSifre;

    public Restoran(String restoranId, String restoranAd, int masaSayisi, String yoneticiId, String kasaKullaniciAdi, String kasaSifre) {
        this.restoranId = restoranId;
        this.restoranAd = restoranAd;
        this.masaSayisi = masaSayisi;
        this.yoneticiId = yoneticiId;
        this.kasaKullaniciAdi = kasaKullaniciAdi;
        this.kasaSifre = kasaSifre;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public String getRestoranAd() {
        return restoranAd;
    }

    public int getMasaSayisi() {
        return masaSayisi;
    }

    public String getYoneticiId() {
        return yoneticiId;
    }

    public String getKasaKullaniciAdi() {
        return kasaKullaniciAdi;
    }

    public String getKasaSifre() {
        return kasaSifre;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }

    public void setRestoranAd(String restoranAd) {
        this.restoranAd = restoranAd;
    }

    public void setMasaSayisi(int masaSayisi) {
        this.masaSayisi = masaSayisi;
    }

    public void setYoneticiId(String yoneticiId) {
        this.yoneticiId = yoneticiId;
    }

    public void setKasaKullaniciAdi(String kasaKullaniciAdi) {
        this.kasaKullaniciAdi = kasaKullaniciAdi;
    }

    public void setKasaSifre(String kasaSifre) {
        this.kasaSifre = kasaSifre;
    }
}
