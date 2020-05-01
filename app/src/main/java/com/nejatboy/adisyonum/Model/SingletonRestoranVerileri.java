package com.nejatboy.adisyonum.Model;

import java.util.List;

public class SingletonRestoranVerileri {

    private List<Urun> urunler;
    private List<Kategori> kategoriler;
    private List<Garson> garsonlar;



    private static SingletonRestoranVerileri singletonRestoranVerileri;

    public static SingletonRestoranVerileri getInstance() {
        if (singletonRestoranVerileri == null) {
            singletonRestoranVerileri = new SingletonRestoranVerileri();
        }
        return singletonRestoranVerileri;
    }


    public List<Urun> getUrunler() {
        return urunler;
    }

    public void setUrunler(List<Urun> urunler) {
        this.urunler = urunler;
    }

    public List<Kategori> getKategoriler() {
        return kategoriler;
    }

    public void setKategoriler(List<Kategori> kategoriler) {
        this.kategoriler = kategoriler;
    }

    public List<Garson> getGarsonlar() {
        return garsonlar;
    }

    public void setGarsonlar(List<Garson> garsonlar) {
        this.garsonlar = garsonlar;
    }

    public static SingletonRestoranVerileri getSingletonRestoranVerileri() {
        return singletonRestoranVerileri;
    }

    public static void setSingletonRestoranVerileri(SingletonRestoranVerileri singletonRestoranVerileri) {
        SingletonRestoranVerileri.singletonRestoranVerileri = singletonRestoranVerileri;
    }
}
