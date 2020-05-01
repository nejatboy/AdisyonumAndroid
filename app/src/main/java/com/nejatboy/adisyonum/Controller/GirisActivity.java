package com.nejatboy.adisyonum.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Controller.Kasa.KasaActivity;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.Model.SingletonGarson;
import com.nejatboy.adisyonum.Model.SingletonRestoranVerileri;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.Model.Urun;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class GirisActivity extends AppCompatActivity {
    
    private Button buttonYoneticiGiris, buttonYoneticiKayit, buttonKasaGiris, buttonGarsonGiris;
    private RadioButton radioButtonYonetici, radioButtonGarson, radioButtonKasa;

    private boolean garsonSecildi = false;
    private boolean yoneticiSecildi = false;
    private boolean kasaSecildi = false;

    private EditText editTextYoneticiKullaniciAdi, editTextYoneticiSifre;
    private EditText editTextGarsonKullaniciAdi, editTextGarsonSifre, editTextGarsonRestoranKodu;
    private EditText editTextKasaKullaniciAdi, editTextKasaSifre, editTextKasaRestoranKodu;

    private FirebaseAuth auth;

    private ProgressBar progressBar;

    private ConstraintLayout layoutYonetici, layoutGarson, layoutKasa;

    private CollectionReference referenceGarsonlar = FirebaseFirestore.getInstance().collection("Garsonlar");
    private List<Garson> tumGarsonlar = new ArrayList<>();
    private Garson girisYapanGarson = null;

    private CollectionReference referenceRestoranlar = FirebaseFirestore.getInstance().collection("Restoranlar");
    private List<Restoran> tumRestoranlar = new ArrayList<>();

    private List<Garson> garsonlar = new ArrayList<>();
    private List<Urun> urunler = new ArrayList<>();
    private List<Kategori> kategoriler = new ArrayList<>();
    private CollectionReference referenceKategoriler = FirebaseFirestore.getInstance().collection("Kategoriler");
    private CollectionReference referenceUrunler = FirebaseFirestore.getInstance().collection("Urunler");

    private SingletonRestoranVerileri singletonRestoranVerileri;
    private SingletonGarson singletonGarson;
    private SingletonRestoran singletonRestoran;

    private Handler handler = new Handler();
    boolean urunlerHafizayaAlindi = false;
    boolean kategorilerHafizayaAlindi = false;
    boolean garsonlarHafizayaAlindi = false;

    private Restoran girisYapanKasa;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        layoutYonetici = findViewById(R.id.layoutYoneticiGirisActivity);
        layoutGarson = findViewById(R.id.layoutGarsonGirisActivity);
        layoutKasa = findViewById(R.id.layoutKasaGirisActivity);
        buttonYoneticiGiris = findViewById(R.id.buttonGirisActivityYoneticiGiris);
        buttonYoneticiKayit = findViewById(R.id.buttonGirisActivityYoneticiKayitOl);
        buttonKasaGiris = findViewById(R.id.buttonGirisActivityKasaGiris);
        buttonGarsonGiris = findViewById(R.id.buttonGirisActivityGarsonGiris);
        radioButtonGarson = findViewById(R.id.radioButtonGirisActivityGarson);
        radioButtonKasa = findViewById(R.id.radioButtonGirisActivityKasa);
        radioButtonYonetici = findViewById(R.id.radioButtonGirisActivityYonetici);
        editTextYoneticiKullaniciAdi = findViewById(R.id.editTextGirisActivityYoneticiKullaniciAdi);
        editTextYoneticiSifre = findViewById(R.id.editTextGirisActivityYoneticiSifre);
        editTextGarsonKullaniciAdi = findViewById(R.id.editTextGirisActivityGarsonKullaniciAdi);
        editTextGarsonSifre = findViewById(R.id.editTextGirisActivityGarsonSifre);
        editTextGarsonRestoranKodu = findViewById(R.id.editTextGirisActivityGarsonRestoranKodu);
        editTextKasaKullaniciAdi = findViewById(R.id.editTextGirisActivityKasaKullaniciAdi);
        editTextKasaSifre = findViewById(R.id.editTextGirisActivityKasaSifre);
        editTextKasaRestoranKodu = findViewById(R.id.editTextGirisActivityKasaRestoranKodu);
        progressBar = findViewById(R.id.progressBarGirisActivity);

        buttonYoneticiGiris.setOnClickListener(buttonYoneticiGirisListener);
        buttonYoneticiKayit.setOnClickListener(buttonYoneticiKayitOlListener);
        buttonKasaGiris.setOnClickListener(buttonKasaGirisListener);
        buttonGarsonGiris.setOnClickListener(buttonGarsonGirisListener);

        radioButtonYonetici.setOnCheckedChangeListener(yoneticiRadioButtonListener);
        radioButtonKasa.setOnCheckedChangeListener(kasaRadioButtonListener);
        radioButtonGarson.setOnCheckedChangeListener(garsonRadioButtonListener);

        auth = FirebaseAuth.getInstance();

        radioButtonGarson.setChecked(true);

        tumGarsonlariAl();
        tumRestoranlariAl();

        singletonRestoranVerileri = SingletonRestoranVerileri.getInstance();

        //KULLANICI HATIRLAMA YAPILACAK
    }




    private View.OnClickListener buttonGarsonGirisListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);

            String restoranKodu = editTextGarsonRestoranKodu.getText().toString();
            String sifre = editTextGarsonSifre.getText().toString();
            String kullaniciAdi = editTextGarsonKullaniciAdi.getText().toString();

            if (restoranKoduGecerli(restoranKodu)) {
                if (garsonGecerli(kullaniciAdi, sifre) ) {
                    restoraniGetirByRestoranId(girisYapanGarson.getRestoranId());
                    kategorileriGetirByRestoranId(girisYapanGarson.getRestoranId());
                    urunleriGetirByRestoranId(girisYapanGarson.getRestoranId());
                    garsonlariGetirByRestoranId(girisYapanGarson.getRestoranId());

                    handler.postDelayed(new Runnable() {        //1sn içinde veriler inerse intent yap inmezse tekrar 1sn bekle
                        public void run() {
                            if (kategorilerHafizayaAlindi && urunlerHafizayaAlindi && garsonlarHafizayaAlindi) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(GirisActivity.this, "Giriş yapıldı", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), GarsonActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }, 1000);

                } else {
                    Toast.makeText(GirisActivity.this, "Garson bulunamadı.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(GirisActivity.this, "Restoran kodu bulunamadı.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    };




    private View.OnClickListener buttonKasaGirisListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);

            String restoranId = editTextKasaRestoranKodu.getText().toString();
            String sifre = editTextKasaSifre.getText().toString();
            String kullaniciAdi = editTextKasaKullaniciAdi.getText().toString();

            if (restoranKoduGecerli(restoranId)) {
                if (kasaGecerli(kullaniciAdi, sifre)) {     //Girş yapılır
                    restoraniGetirByRestoranId(restoranId);     //Restoran verilerini singleton'a alırız
                    garsonlariGetirByRestoranId(restoranId);
                    kategorileriGetirByRestoranId(restoranId);
                    urunleriGetirByRestoranId(restoranId);

                    handler.postDelayed(new Runnable() {        //1sn içinde veriler inerse intent yap inmezse tekrar 1sn bekle
                        public void run() {
                            if (kategorilerHafizayaAlindi && urunlerHafizayaAlindi && garsonlarHafizayaAlindi) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(GirisActivity.this, "Giriş yapıldı", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), KasaActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }, 1000);

                } else {
                    Toast.makeText(GirisActivity.this, "Kasa bulunamadı.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(GirisActivity.this, "Restoran bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        }
    };
    
    


    private View.OnClickListener buttonYoneticiGirisListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);

            String kullaniciAdi = editTextYoneticiKullaniciAdi.getText().toString();
            String sifre = editTextYoneticiSifre.getText().toString();

            if (kullaniciAdi.equals("") || sifre.equals("")) {
                Toast.makeText(GirisActivity.this, "Kullanıcı adı ve şifre girmelisiniz.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
            } else {
                auth.signInWithEmailAndPassword(kullaniciAdi, sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(GirisActivity.this, "Giriş Yapıldı.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), YoneticiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GirisActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    };




    private View.OnClickListener buttonYoneticiKayitOlListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);

            String kullaniciAdi = editTextYoneticiKullaniciAdi.getText().toString();
            String sifre = editTextYoneticiSifre.getText().toString();

            if (kullaniciAdi.equals("") || sifre.equals("")) {
                Toast.makeText(GirisActivity.this, "Kullanıcı adı ve şifre girmelisiniz.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
            } else {
                auth.createUserWithEmailAndPassword(kullaniciAdi, sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(GirisActivity.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), YoneticiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GirisActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    };




    private CompoundButton.OnCheckedChangeListener garsonRadioButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            garsonSecildi = isChecked;

            if (garsonSecildi) {
                layoutGarson.setVisibility(View.VISIBLE);
                layoutKasa.setVisibility(View.INVISIBLE);
                layoutYonetici.setVisibility(View.INVISIBLE);
            }
        }
    };




    private CompoundButton.OnCheckedChangeListener yoneticiRadioButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            yoneticiSecildi = isChecked;

            if (yoneticiSecildi) {
                layoutYonetici.setVisibility(View.VISIBLE);
                layoutKasa.setVisibility(View.INVISIBLE);
                layoutGarson.setVisibility(View.INVISIBLE);
            }
        }
    };




    private CompoundButton.OnCheckedChangeListener kasaRadioButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            kasaSecildi = isChecked;

            if (kasaSecildi) {
                layoutKasa.setVisibility(View.VISIBLE);
                layoutYonetici.setVisibility(View.INVISIBLE);
                layoutGarson.setVisibility(View.INVISIBLE);
            }
        }
    };




    private void tumGarsonlariAl () {
        tumGarsonlar.clear();

        referenceGarsonlar.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String garsonAd = (String) snapshot.get("garsonAd");
                        String garsonId = snapshot.getId();
                        String restoranId = (String) snapshot.get("restoranId");
                        String kullaniciAdi = (String) snapshot.get("garsonKullaniciAd");
                        String sifre = (String) snapshot.get("garsonSifre");

                        tumGarsonlar.add(new Garson(garsonId, garsonAd, kullaniciAdi, sifre, restoranId));
                    }
                }
            }
        });
    }




    private boolean restoranKoduGecerli (String restoranKodu) {
        for (Garson garson: tumGarsonlar) {
            if (garson.getRestoranId().equals(restoranKodu)) {
                return true;
            }
        }
        return  false;
    }




    private boolean garsonGecerli (String kullaniciAd, String sifre) {
        for (Garson garson: tumGarsonlar) {
            if (garson.getGarsonKullaniciAd().equals(kullaniciAd) && garson.getGarsonSifre().equals(sifre)) {
                girisYapanGarson = garson;

                singletonGarson = SingletonGarson.getInstance();
                singletonGarson.setGarsonAd(garson.getGarsonAd());
                singletonGarson.setGarsonId(garson.getGarsonId());
                singletonGarson.setGarsonKullaniciAd(garson.getGarsonKullaniciAd());
                singletonGarson.setGarsonSifre(garson.getGarsonSifre());
                singletonGarson.setRestoranId(garson.getRestoranId());
                return true;
            }
        }
        return false;
    }




    private boolean kasaGecerli (String kullaniciAd, String sifre) {
        for (Restoran restoran: tumRestoranlar) {
            if (restoran.getKasaKullaniciAdi().equals(kullaniciAd) && restoran.getKasaSifre().equals(sifre)) {
                return true;
            }
        }
        return false;
    }




    private void  tumRestoranlariAl () {
        tumRestoranlar.clear();

        referenceRestoranlar.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String restoranId = snapshot.getId();
                        String yoneticiId = (String) snapshot.get("yoneticiId");
                        String restoranAd = (String) snapshot.get("restoranAd");
                        long masaSaiyisi = (long) snapshot.get("masaSayisi");
                        String kasaKullaniciAdi = (String) snapshot.get("kasaKullaniciAdi");
                        String kasaSifre = (String) snapshot.get("kasaSifre");

                        tumRestoranlar.add(new Restoran(restoranId, restoranAd, (int) masaSaiyisi, yoneticiId, kasaKullaniciAdi, kasaSifre));
                    }
                }
            }
        });
    }




    private void restoraniGetirByRestoranId (String restoranId) {
        for (Restoran restoran: tumRestoranlar) {
            if (restoran.getRestoranId().equals(restoranId)) {
                singletonRestoran = SingletonRestoran.getInstance();
                singletonRestoran.setYoneticiId(restoran.getYoneticiId());
                singletonRestoran.setRestoranId(restoran.getRestoranId());
                singletonRestoran.setRestoranAd(restoran.getRestoranAd());
                singletonRestoran.setMasaSayisi(restoran.getMasaSayisi());
                singletonRestoran.setKasaKullaniciAdi(restoran.getKasaKullaniciAdi());
                singletonRestoran.setKasaSifre(restoran.getKasaSifre());
            }
        }
    }




    private void kategorileriGetirByRestoranId (String restoranId) {
        kategoriler.clear();

        Query query = referenceKategoriler.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String kategoriId = snapshot.getId();
                        String kategoriAd = (String) snapshot.get("kategoriAd");
                        String restoranId = (String) snapshot.get("restoranId");

                        kategoriler.add(new Kategori(kategoriId, kategoriAd, restoranId));
                    }
                    singletonRestoranVerileri.setKategoriler(kategoriler);
                    kategorilerHafizayaAlindi = true;
                }
            }
        });
    }




    private void garsonlariGetirByRestoranId (String restoranId) {
        Query query = referenceGarsonlar.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String garsonId = snapshot.getId();
                        String restoranId = (String) snapshot.get("restoranId");
                        String garsonAd = (String) snapshot.get("garsonAd");
                        String garsonKullaniciAd = (String) snapshot.get("garsonKullaniciAd");
                        String garsonSifre = (String) snapshot.get("garsonSifre");

                        garsonlar.add(new Garson(garsonId, garsonAd, garsonKullaniciAd, garsonSifre, restoranId));
                    }
                    singletonRestoranVerileri.setGarsonlar(garsonlar);
                    garsonlarHafizayaAlindi = true;
                }
            }
        });
    }




    private void urunleriGetirByRestoranId (String restoranId) {
        urunler.clear();

        Query query = referenceUrunler.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String urunId = snapshot.getId();
                        String kategoriId = (String) snapshot.get("kategoriId");
                        String restoranId = (String) snapshot.get("restoranId");
                        String urunAd = (String) snapshot.get("urunAd");
                        double urunFiyat = (double) snapshot.get("urunFiyat");

                        urunler.add(new Urun(urunId, urunAd, urunFiyat, kategoriId, restoranId));
                    }
                    singletonRestoranVerileri.setUrunler(urunler);
                    urunlerHafizayaAlindi = true;
                }
            }
        });
    }


}
