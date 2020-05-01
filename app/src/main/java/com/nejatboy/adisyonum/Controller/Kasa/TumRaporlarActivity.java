package com.nejatboy.adisyonum.Controller.Kasa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Adapter.Kasa.AdapterTumRaporlar;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.GunlukRapor;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.Model.SingletonRestoranVerileri;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TumRaporlarActivity extends AppCompatActivity {

    private CollectionReference referenceGunlukRaporlar = FirebaseFirestore.getInstance().collection("GunlukRaporlar");
    private SingletonRestoran singletonRestoran = SingletonRestoran.getInstance();
    private List<GunlukRapor>  tumRaporlar = new ArrayList<>();
    private ListView listViewTumRaporlar;
    private AdapterTumRaporlar adapterTumRaporlar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);       //Bildirim çubuğunu gizle
        setContentView(R.layout.activity_tum_raporlar);

        listViewTumRaporlar = findViewById(R.id.listViewTumRaporlarActivity);

        adapterTumRaporlar = new AdapterTumRaporlar(TumRaporlarActivity.this, tumRaporlar);
        listViewTumRaporlar.setAdapter(adapterTumRaporlar);

        raporlariGetir(singletonRestoran.getRestoranId());

        listViewTumRaporlar.setOnItemClickListener(listViewTumRaporlarItemClickListener);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), KasaActivity.class);
        startActivity(intent);
        finishAffinity();
    }




    private void raporlariGetir(final String restoranId) {
        tumRaporlar.clear();

        referenceGunlukRaporlar.whereEqualTo("restoranId", restoranId).orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String raporId = snapshot.getId();
                        HashMap<String, Double> hesaplar = (HashMap<String, Double>) snapshot.get("hesaplar");
                        HashMap<String, Double> garsonSatislari = (HashMap<String, Double>) snapshot.get("garsonSatislari");
                        Double ciro = (Double) snapshot.get("ciro");
                        Date tarih = snapshot.getDate("tarih");

                        GunlukRapor gunlukRapor = new GunlukRapor(raporId, restoranId, hesaplar, garsonSatislari, ciro, tarih);
                        tumRaporlar.add(gunlukRapor);
                    }
                    adapterTumRaporlar.notifyDataSetChanged();
                }
            }
        });
    }




    private void dialogGunlukRaporuGoster (GunlukRapor rapor) {
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

        Dialog dialog = new Dialog(TumRaporlarActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(view);
        dialog.show();
    }




    private AdapterView.OnItemClickListener listViewTumRaporlarItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GunlukRapor rapor = tumRaporlar.get(position);
            dialogGunlukRaporuGoster(rapor);
        }
    };
}
