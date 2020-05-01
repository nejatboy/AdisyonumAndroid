package com.nejatboy.adisyonum.Model;

public class Urun {

    private String urunId;
    private String urunAd;
    private double urunFiyat;
    private String kategoriId;
    private String restoranId;

    public Urun(String urunId, String urunAd, double urunFiyat, String kategoriId, String restoranId) {
        this.urunId = urunId;
        this.urunAd = urunAd;
        this.urunFiyat = urunFiyat;
        this.kategoriId = kategoriId;
        this.restoranId = restoranId;
    }

    public String getUrunId() {
        return urunId;
    }

    public String getUrunAd() {
        return urunAd;
    }

    public double getUrunFiyat() {
        return urunFiyat;
    }

    public String getKategoriId() {
        return kategoriId;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public void setUrunId(String urunId) {
        this.urunId = urunId;
    }

    public void setUrunAd(String urunAd) {
        this.urunAd = urunAd;
    }

    public void setUrunFiyat(double urunFiyat) {
        this.urunFiyat = urunFiyat;
    }

    public void setKategoriId(String kategoriId) {
        this.kategoriId = kategoriId;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }
}
