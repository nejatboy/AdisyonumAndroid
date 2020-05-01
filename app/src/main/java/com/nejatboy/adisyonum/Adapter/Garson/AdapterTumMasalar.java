package com.nejatboy.adisyonum.Adapter.Garson;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Adapter.Yonetici.SpinnerAdapterKategoriler;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.SingletonGarson;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.Model.SingletonRestoranVerileri;
import com.nejatboy.adisyonum.Model.Urun;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterTumMasalar  extends RecyclerView.Adapter<AdapterTumMasalar.HucreTasarim>{

    private Context context;
    private List<Masa> tumMasalar;
    private SingletonGarson singletonGarson;

    private Dialog dialogUrunEkle;
    private Button buttonDialogKapat;
    private Button buttonDialogGonder;
    private Spinner spinnerKategoriler;
    private ListView listViewDialogUrunEkleUrunler;
    private TextView textViewDialogYeniMasaMasaNo;
    private ProgressBar progressBar;

    private SingletonRestoranVerileri singletonRestoranVerileri;
    private SingletonRestoran singletonRestoran = SingletonRestoran.getInstance();
    private SpinnerAdapterKategoriler spinnerAdapterKategoriler;
    private List<Urun> urunler = new ArrayList<>();
    private AdapterDialogUrunler adapterDialogUrunEkleUrunler;

    private List<Urun> siparisler = new ArrayList<>();
    private Masa secilenMasa;

    private CollectionReference referenceMasalar = FirebaseFirestore.getInstance().collection("Masalar");
    private AdapterDialogUrunler adapterDialogUrunEkleSiparisler;
    private ListView listViewDialogUrunEkleSiparisler;

    private Dialog dialogUrunGoruntule;
    private ListView listViewDialogUrunGoruntuleUrunler;
    private AdapterDialogUrunler adapterDialogUrunGoruntuleUrunler;
    private Button buttonDialogUrunGoruntuleUrunEkle;

    private Boolean ekSiparis = false;
    private Masa ekMasa;





    public AdapterTumMasalar(Context context, List<Masa> tumMasalar) {
        this.context = context;
        this.tumMasalar = tumMasalar;

        acikMasalariAl();

        singletonGarson = SingletonGarson.getInstance();
        singletonRestoranVerileri = SingletonRestoranVerileri.getInstance();

        dialogUrunEkleIslemleri();

        kategoriIslemleri();

        dialogUrunGoruntuleIslemleri();

        veriDegisirseArayuzuGuncelle();
    }




    private void dialogUrunEkleIslemleri() {
        dialogUrunEkle = new Dialog(context, R.style.AppTheme);
        dialogUrunEkle.setContentView(R.layout.popup_garson_urun_ekle);
        buttonDialogKapat = dialogUrunEkle.findViewById(R.id.buttonPopupUrunEklePopupKapat);
        buttonDialogGonder = dialogUrunEkle.findViewById(R.id.buttonDialogUrunEkleGonder);
        spinnerKategoriler = dialogUrunEkle.findViewById(R.id.spinnerDialogUrunEkleKategoriler);
        listViewDialogUrunEkleUrunler = dialogUrunEkle.findViewById(R.id.listViewDialogUrunEkleUrunler);
        textViewDialogYeniMasaMasaNo = dialogUrunEkle.findViewById(R.id.textViewDialogUrunEkleMasaNo);
        listViewDialogUrunEkleSiparisler = dialogUrunEkle.findViewById(R.id.listViewDialogUrunEkleSiparisler);
        progressBar = dialogUrunEkle.findViewById(R.id.progressBarDialogUrunEkle);

        adapterDialogUrunEkleSiparisler = new AdapterDialogUrunler(context, siparisler);
        listViewDialogUrunEkleSiparisler.setAdapter(adapterDialogUrunEkleSiparisler);

        buttonDialogKapat.setOnClickListener(buttonDialogKapatCliclkListener);
        buttonDialogGonder.setOnClickListener(buttonDialogGonderCliclkListener);
        listViewDialogUrunEkleUrunler.setOnItemClickListener(dialogUrunSecListener);
        listViewDialogUrunEkleSiparisler.setOnItemClickListener(dialogUrunSiparisListener);
    }




    private void kategoriIslemleri () {
        spinnerAdapterKategoriler = new SpinnerAdapterKategoriler(context, singletonRestoranVerileri.getKategoriler());
        spinnerKategoriler.setAdapter(spinnerAdapterKategoriler);
        spinnerKategoriler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategori kategori = (Kategori) parent.getItemAtPosition(position);
                urunler.clear();
                urunler = urunleriGetirByKategoriId(kategori.getKategoriId());
                adapterDialogUrunEkleUrunler = new AdapterDialogUrunler(context, urunler);
                listViewDialogUrunEkleUrunler.setAdapter(adapterDialogUrunEkleUrunler);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    private void dialogUrunGoruntuleIslemleri () {
        dialogUrunGoruntule = new Dialog(context);
        dialogUrunGoruntule.setContentView(R.layout.popup_garson_urun_goruntule);
        listViewDialogUrunGoruntuleUrunler = dialogUrunGoruntule.findViewById(R.id.listViewPopupUrunGoruntuleUrunler);
        buttonDialogUrunGoruntuleUrunEkle = dialogUrunGoruntule.findViewById(R.id.buttonPopupUrunGoruntuleUrunEkle);
    }




    @NonNull
    @Override
    public HucreTasarim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_tum_masalar, parent, false);
        return new HucreTasarim(view);
    }




    @Override
    public void onBindViewHolder(@NonNull HucreTasarim holder, int position) {
        final Masa masa = tumMasalar.get(position);

        if (masa.isMasaAcik() && !masa.isMasaYazdirildi()) {
            holder.hucre.setCardBackgroundColor(context.getResources().getColor(R.color.doluMasaHucreRengi));
        } else if (masa.isMasaAcik()&& masa.isMasaYazdirildi()){
            holder.hucre.setCardBackgroundColor(context.getResources().getColor(R.color.yazdirilmisMasaHucreRengi));
        } else {
            holder.hucre.setCardBackgroundColor(context.getResources().getColor(R.color.bosMasaHucreRengi));
        }

        holder.textViewTutar.setText(masa.getMasaTutar() + " TL");
        holder.textViewMasaNo.setText("" + masa.getMasaNo());
        holder.textViewGarsonAdi.setText(garsonAdiGetirByGarsonId(masa.getGarsonId()));

        holder.hucre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (masa.isMasaAcik()) {
                    if (masa.getGarsonId().equals(singletonGarson.getGarsonId())) {
                        List<Urun> masaUrunleri = new ArrayList<>();
                        for (String urunId: masa.getUrunler()) {
                            masaUrunleri.add(urunGetirByUrunId(urunId));
                        }
                        adapterDialogUrunGoruntuleUrunler = new AdapterDialogUrunler(context, masaUrunleri);
                        listViewDialogUrunGoruntuleUrunler.setAdapter(adapterDialogUrunGoruntuleUrunler);
                        if (masa.isMasaYazdirildi()) {
                            buttonDialogUrunGoruntuleUrunEkle.setVisibility(View.GONE);

                        } else {
                            buttonDialogUrunGoruntuleUrunEkle.setVisibility(View.VISIBLE);
                            buttonDialogUrunGoruntuleUrunEkle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogUrunGoruntule.dismiss();
                                    secilenMasa = masa;
                                    dialogUrunEkle.show();
                                    textViewDialogYeniMasaMasaNo.setText("Masa: " + masa.getMasaNo());
                                    siparisler.clear();
                                    ekSiparis = true;
                                    ekMasa = masa;
                                }
                            });
                        }

                        dialogUrunGoruntule.show();

                    } else {
                        List<Urun> masaUrunleri = new ArrayList<>();
                        for (String urunId: masa.getUrunler()) {
                            masaUrunleri.add(urunGetirByUrunId(urunId));
                        }
                        buttonDialogUrunGoruntuleUrunEkle.setVisibility(View.GONE);
                        adapterDialogUrunGoruntuleUrunler = new AdapterDialogUrunler(context, masaUrunleri);
                        listViewDialogUrunGoruntuleUrunler.setAdapter(adapterDialogUrunGoruntuleUrunler);
                        dialogUrunGoruntule.show();
                    }
                } else {
                    secilenMasa = masa;
                    dialogUrunEkle.show();
                    textViewDialogYeniMasaMasaNo.setText("Masa: " + masa.getMasaNo());
                    siparisler.clear();
                }
            }
        });

        holder.hucre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Yazdırma veya transfer seçenekleri popup açılacak
                return false;
            }
        });
    }




    @Override
    public int getItemCount() {
        return tumMasalar.size();
    }




    private  View.OnClickListener buttonDialogKapatCliclkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogUrunEkle.dismiss();
            siparisler.clear();
        }
    };




    private  View.OnClickListener buttonDialogGonderCliclkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);

            if (ekSiparis) {
                if (!siparisler.isEmpty()) {
                    Double hesap = ekMasa.getMasaTutar();
                    for (Urun urun: siparisler) {
                        ekMasa.getUrunler().add(urun.getUrunId());
                        hesap = hesap + urun.getUrunFiyat();
                    }
                    masaGuncelle(ekMasa.getMasaId(), hesap, ekMasa.getUrunler());
                }

            } else {
                List<String> siperisIdleri = new ArrayList<>();
                Double hesap = 0.0;
                for (Urun urun: siparisler)  {
                    siperisIdleri.add(urun.getUrunId());
                    hesap = hesap + urun.getUrunFiyat();
                }
                Masa masa = new Masa("", secilenMasa.getMasaNo(), secilenMasa.getRestoranId(), hesap, singletonGarson.getGarsonId(), true, false, siperisIdleri);
                masayiVeriTabaninaYaz(masa);
            }
        }
    };




    private void masayiVeriTabaninaYaz (Masa masa) {
        referenceMasalar.add(masa).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                dialogUrunEkle.dismiss();
                siparisler.clear();
            }
        });
    }




    private void acikMasalariAl () {


        Query query = referenceMasalar.whereEqualTo("restoranId", SingletonRestoran.getInstance().getRestoranId());
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

                        tumMasalar.set((int) (masaNo - 1), new Masa(masaId, (int) masaNo, restoranId, hesap, garsonId, masaAcik, masaYazdirildi, urunler));
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }




    private List<Urun> urunleriGetirByKategoriId (String kategoriId)  {
        List<Urun> urunler = new ArrayList<>();

        for (Urun urun: singletonRestoranVerileri.getUrunler()) {
            if (urun.getKategoriId().equals(kategoriId)) {
                urunler.add(urun);
            }
        }

        return urunler;
    }




    private AdapterView.OnItemClickListener dialogUrunSecListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Urun urun = urunler.get(position);
            siparisler.add(urun);
            adapterDialogUrunEkleSiparisler.notifyDataSetChanged();
        }
    };




    private String garsonAdiGetirByGarsonId (String garsonId) {
        for (Garson garson: singletonRestoranVerileri.getGarsonlar()) {
            if (garson.getGarsonId().equals(garsonId)) {
                return garson.getGarsonAd();
            }
        }
        return null;
    }




    private AdapterView.OnItemClickListener dialogUrunSiparisListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            siparisler.remove(position);
            adapterDialogUrunEkleSiparisler.notifyDataSetChanged();
        }
    };




    private Urun urunGetirByUrunId (String urunId) {
        for (Urun urun: singletonRestoranVerileri.getUrunler()) {
            if (urunId.equals(urun.getUrunId())) {
                return urun;
            }
        }
        return null;
    }




    private void masaGuncelle (String masaId, Double hesap, List<String> urunler) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("masaTutar", hesap);
        veri.put("urunler", urunler);

        referenceMasalar.document(masaId).update(veri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                dialogUrunEkle.dismiss();
                siparisler.clear();
                ekSiparis = false;
                ekMasa = null;
            }
        });
    }




    private void veriDegisirseArayuzuGuncelle () {
        referenceMasalar.whereEqualTo("restoranId", SingletonRestoran.getInstance().getRestoranId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                tumMasalar.clear();
                for(int i=1; i<=singletonRestoran.getMasaSayisi(); i++) {
                    tumMasalar.add(new Masa("", i, singletonRestoran.getRestoranId(), 0.0, "", false, false, null));
                }
                acikMasalariAl();
            }
        });
    }















    // ---------------------------          INNER CLASS         -------------------------------------

    class HucreTasarim extends RecyclerView.ViewHolder {
        TextView textViewMasaNo, textViewTutar, textViewGarsonAdi;
        CardView hucre;

        public HucreTasarim(@NonNull View itemView) {
            super(itemView);
            hucre = itemView.findViewById(R.id.cardViewTumMasalarHucre);
            textViewMasaNo = itemView.findViewById(R.id.textViewTumMasalarHucreMasaNo);
            textViewTutar = itemView.findViewById(R.id.textViewTumMasalarHucreTutar);
            textViewGarsonAdi = itemView.findViewById(R.id.textViewTumMasalarHucreGarsonAdi);
        }
    }
}
