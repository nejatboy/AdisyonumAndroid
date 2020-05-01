package com.nejatboy.adisyonum.Model;

public class Kategori {

    private String kategoriId;
    private String kategoriAd;
    private String restoranId;

    public Kategori(String kategoriId, String kategoriAd, String restoranId) {
        this.kategoriId = kategoriId;
        this.kategoriAd = kategoriAd;
        this.restoranId = restoranId;
    }

    public String getKategoriId() {
        return kategoriId;
    }

    public String getKategoriAd() {
        return kategoriAd;
    }

    public String getRestoranId() {
        return restoranId;
    }

    public void setKategoriId(String kategoriId) {
        this.kategoriId = kategoriId;
    }

    public void setKategoriAd(String kategoriAd) {
        this.kategoriAd = kategoriAd;
    }

    public void setRestoranId(String restoranId) {
        this.restoranId = restoranId;
    }
}
