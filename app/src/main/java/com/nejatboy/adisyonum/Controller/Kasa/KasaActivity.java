package com.nejatboy.adisyonum.Controller.Kasa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Adapter.Kasa.AdapterTumMasalar;
import com.nejatboy.adisyonum.Controller.GirisActivity;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.GunlukRapor;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.Rapor;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.Model.SingletonRestoranVerileri;
import com.nejatboy.adisyonum.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class KasaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SingletonRestoran singletonRestoran = SingletonRestoran.getInstance();
    private List<Masa> tumMasalar = new ArrayList<>();
    private CollectionReference referenceMasalar = FirebaseFirestore.getInstance().collection("Masalar");
    private AdapterTumMasalar adapterTumMasalar;

    private CollectionReference referenceAnlikRaporlar = FirebaseFirestore.getInstance().collection("AnlikRaporlar");
    private CollectionReference referenceGunlukRaporlar = FirebaseFirestore.getInstance().collection("GunlukRaporlar");
    private List<String> doluMasalar = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);       //Bildirim çubuğunu gizle
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);       //Ekranı açık tut
        setContentView(R.layout.activity_kasa);

        recyclerView = findViewById(R.id.recyclerViewKasaActivity);
        drawerLayout = findViewById(R.id.drawerLayoutKasaActivity);
        navigationView = findViewById(R.id.navigationViewKasaActivity);

        tumMasalariOlustur();
        doluMasalariGetir();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 6));

        adapterTumMasalar = new AdapterTumMasalar(this, tumMasalar);
        recyclerView.setAdapter(adapterTumMasalar);

        navigationView.setNavigationItemSelectedListener(navDrawerItemListener);
        
        veriDegisirseArayuzuGuncelle();
    }




    @Override
    protected void onStart() {
        super.onStart();
        drawerLayout.openDrawer(Gravity.LEFT);      //Drawer menüsünü aç
    }




    private void tumMasalariOlustur () {
        tumMasalar.clear();
        for (int i = 1; i <=singletonRestoran.getMasaSayisi(); i++) {
            tumMasalar.add(new Masa("", i, "", 0.0, "", false, false, null));
        }
    }




    private void doluMasalariGetir () {
        doluMasalar.clear();

        Query query = referenceMasalar.whereEqualTo("restoranId", singletonRestoran.getRestoranId());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String masaId = snapshot.getId();
                        String garsonId = (String) snapshot.get("garsonId");
                        boolean masaAcik = (boolean) snapshot.get("masaAcik");
                        long masaNo = (long) snapshot.get("masaNo");
                        Double hesap = snapshot.getDouble("masaTutar");
                        boolean masaYazdirildi = (boolean) snapshot.get("masaYazdirildi");
                        String restoranId = (String) snapshot.get("restoranId");
                        List<String> urunler = (List<String>) snapshot.get("urunler");

                        doluMasalar.add(masaId);

                        tumMasalar.set((int) (masaNo - 1), new Masa(masaId, (int) masaNo, restoranId, hesap, garsonId, masaAcik, masaYazdirildi, urunler));     //Pozisyona açık masayı set et
                    }
                    adapterTumMasalar.notifyDataSetChanged();
                }
            }
        });
    }




    private void veriDegisirseArayuzuGuncelle () {
        referenceMasalar.whereEqualTo("restoranId", singletonRestoran.getRestoranId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                tumMasalariOlustur();
                doluMasalariGetir();
            }
        });
    }




    private void anlikRaporuGetirGunlukRaporOlusturulacak() {
        referenceAnlikRaporlar.whereEqualTo("restoranId", singletonRestoran.getRestoranId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.getDocuments().size() != 0) {     //Belge var
                        DocumentSnapshot snapshotBelge = queryDocumentSnapshots.getDocuments().get(0);
                        
                        String raporId = snapshotBelge.getId();
                        String restoranId = (String) snapshotBelge.get("restoranId");
                        HashMap<String, Double> hesaplar = (HashMap<String, Double>) snapshotBelge.get("hesaplar");
                        HashMap<String, Double> garsonSatislari = (HashMap<String, Double>) snapshotBelge.get("garsonSatislari");
                        Double ciro = (Double) snapshotBelge.get("ciro");

                        Rapor rapor = new Rapor(raporId, restoranId, hesaplar, garsonSatislari, ciro);
                        alertGosterKasaKapatilacak(rapor);
                    } else {        //Belge yok
                        Toast.makeText(KasaActivity.this, "Alınmış hesabınız yok!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    
    
    
    
    private void alertGosterKasaKapatilacak (final Rapor rapor) {
        AlertDialog.Builder alert = new AlertDialog.Builder(KasaActivity.this);
        alert.setTitle("Kasa Kapatılacak");
        alert.setMessage("Bugünkü alınan hesaplar raporlara kaydedilecek. Emin misiniz?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gunlukRaporuKaydetAnlikRaporuSil(rapor);
            }
        }).show();
    }




    private void gunlukRaporuKaydetAnlikRaporuSil (final Rapor rapor) {
        HashMap<String, Double> hesaplar = rapor.getHesaplar();
        HashMap<String, Double> garsonSatislari = rapor.getGarsonSatislari();
        Double ciro = rapor.getCiro();
        String restoranId = rapor.getRestoranId();
        Date gecerliTarih =  Calendar.getInstance().getTime();

        GunlukRapor gunlukRapor = new GunlukRapor("", restoranId, hesaplar, garsonSatislari, ciro, gecerliTarih);
        referenceGunlukRaporlar.add(gunlukRapor).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {    //Günlük rapor kaydedildi
                referenceAnlikRaporlar.document(rapor.getRaporId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {   //Anlık raporu sil
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(KasaActivity.this, "Rapor başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KasaActivity.this, "Hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void anlikRaporuGetirGoruntulenecek() {
        referenceAnlikRaporlar.whereEqualTo("restoranId", singletonRestoran.getRestoranId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.getDocuments().size() != 0) {     //Belge var
                        DocumentSnapshot snapshotBelge = queryDocumentSnapshots.getDocuments().get(0);

                        String raporId = snapshotBelge.getId();
                        String restoranId = (String) snapshotBelge.get("restoranId");
                        HashMap<String, Double> hesaplar = (HashMap<String, Double>) snapshotBelge.get("hesaplar");
                        HashMap<String, Double> garsonSatislari = (HashMap<String, Double>) snapshotBelge.get("garsonSatislari");
                        Double ciro = (Double) snapshotBelge.get("ciro");

                        Rapor rapor = new Rapor(raporId, restoranId, hesaplar, garsonSatislari, ciro);
                        dialogAnlikRapor(rapor);

                    } else {        //Belge yok
                        Toast.makeText(KasaActivity.this, "Rapor mevcut değil.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }




    private void dialogAnlikRapor (Rapor rapor) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_anlik_rapor_goruntule, null);

        LinearLayout layoutGarsonSatisi = view.findViewById(R.id.linearLayoutDialogAnlikRaporGoruntuleGarsonTutucu);
        TextView textViewNakit = view.findViewById(R.id.textViewDialogAnlikRaporNakit);
        TextView textViewKrediKarti = view.findViewById(R.id.textViewDialogAnlikRaporKrediKarti);
        TextView textViewMultinet = view.findViewById(R.id.textViewDialogAnlikRaporMultinet);
        TextView textViewTicket = view.findViewById(R.id.textViewDialogAnlikRaporTicket);
        TextView textViewSodexo = view.findViewById(R.id.textViewDialogAnlikRaporSodexo);
        TextView textViewSetcard = view.findViewById(R.id.textViewDialogAnlikRaporSetcard);
        TextView textViewMetropol = view.findViewById(R.id.textViewDialogAnlikRaporMetropol);
        TextView textViewToplam = view.findViewById(R.id.textViewDialogAnlikRaporToplam);

        textViewNakit.setText("Nakit: " + rapor.getHesaplar().get("nakit"));
        textViewKrediKarti.setText("Kredi Kartı: " + rapor.getHesaplar().get("krediKarti"));
        textViewMultinet.setText("Multinet: " + rapor.getHesaplar().get("multinet"));
        textViewTicket.setText("Ticket: " + rapor.getHesaplar().get("ticket"));
        textViewSetcard.setText("Setcard: " + rapor.getHesaplar().get("setcard"));
        textViewSodexo.setText("Sodexo: " + rapor.getHesaplar().get("sodexo"));
        textViewMetropol.setText("Metropol: " + rapor.getHesaplar().get("metropol"));
        textViewToplam.setText("Toplam: " + rapor.getCiro());

        List<Garson> restoranGarsonlari = SingletonRestoranVerileri.getInstance().getGarsonlar();
        for (Garson garson: restoranGarsonlari) {
            TextView textViewGarson = new TextView(getApplicationContext());
            textViewGarson.setTextSize(18);
            textViewGarson.setTextColor(getResources().getColor(R.color.primaryText));
            layoutGarsonSatisi.addView(textViewGarson);

            if (rapor.getGarsonSatislari().containsKey(garson.getGarsonId())) {
                textViewGarson.setText(garson.getGarsonAd()  +": " + rapor.getGarsonSatislari().get(garson.getGarsonId()));
            } else {
                textViewGarson.setText(garson.getGarsonAd() + ": 0.0");
            }
        }

        Dialog dialog = new Dialog(KasaActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(view);
        dialog.show();
    }



  
    private NavigationView.OnNavigationItemSelectedListener navDrawerItemListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.actionDrawerMenuCikisYap) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(), GirisActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finishAffinity();

            } else if (item.getItemId() == R.id.actionDrawerMenuBugunkuRaporlar) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                anlikRaporuGetirGoruntulenecek();

            } else if (item.getItemId() == R.id.actionDrawerMenuTumRaporlar) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(), TumRaporlarActivity.class);
                startActivity(intent);

            }else if (item.getItemId() == R.id.actionDrawerMenuKasayiKapat) {      //Anlık Raporu günlük rapora al (tarih ekle)
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (doluMasalar.isEmpty()) {
                    anlikRaporuGetirGunlukRaporOlusturulacak();
                } else {
                    Toast.makeText(KasaActivity.this, "Hesabı alınmamış masanız var!", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    };



}


