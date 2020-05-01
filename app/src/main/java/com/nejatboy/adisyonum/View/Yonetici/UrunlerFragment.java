package com.nejatboy.adisyonum.View.Yonetici;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Adapter.Yonetici.AdapterUrunler;
import com.nejatboy.adisyonum.Adapter.Yonetici.SpinnerAdapterKategoriler;
import com.nejatboy.adisyonum.Adapter.Yonetici.SpinnerAdapterRestoranlar;
import com.nejatboy.adisyonum.Controller.YoneticiActivity;
import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.Model.Urun;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class UrunlerFragment extends Fragment {

    private Spinner spinnerRestoranlar;
    private SpinnerAdapterRestoranlar spinnerAdapterRestoranlar;
    private String yoneticiId = "";
    private List<Restoran> restoranlar = new ArrayList<>();
    private CollectionReference referenceRestoranlar = FirebaseFirestore.getInstance().collection("Restoranlar");

    private Spinner spinnerKategoriler;
    private SpinnerAdapterKategoriler spinnerAdapterKategoriler;
    private List<Kategori> kategoriler = new ArrayList<>();
    private CollectionReference referenceKategoriler = FirebaseFirestore.getInstance().collection("Kategoriler");
    private String secilenRestoranId = "";

    private CollectionReference referenceUrunler = FirebaseFirestore.getInstance().collection("Urunler");
    private Button buttonUrunEkle;
    private String secilenKategoriId;
    private List<Urun> urunler = new ArrayList<>();

    private RecyclerView recyclerView;
    private AdapterUrunler adapterUrunler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urunler, container, false);

        spinnerRestoranlar = view.findViewById(R.id.spinnerUrunlerFragmentRestoranlar);
        spinnerKategoriler = view.findViewById(R.id.spinnerUrunlerFragmentKategoriler);
        buttonUrunEkle = view.findViewById(R.id.buttonUrunlerFragmentUrunEkle);
        recyclerView = view.findViewById(R.id.recyclerViewUrunlerFragment);

        spinnerAdapterRestoranlar = new SpinnerAdapterRestoranlar(getContext(), restoranlar);
        spinnerRestoranlar.setAdapter(spinnerAdapterRestoranlar);
        spinnerRestoranlar.setOnItemSelectedListener(spinnerRestoranlarItemClickListener);

        yoneticiId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        restoranlariGetir();

        spinnerAdapterKategoriler = new SpinnerAdapterKategoriler(getContext(), kategoriler);
        spinnerKategoriler.setAdapter(spinnerAdapterKategoriler);
        spinnerKategoriler.setOnItemSelectedListener(spinnerKategorilerItemClickListener);

        buttonUrunEkle.setOnClickListener(buttonUrunEkleClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterUrunler = new AdapterUrunler(getContext(), urunler);
        recyclerView.setAdapter(adapterUrunler);

        return view;
    }




    private View.OnClickListener buttonUrunEkleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_urun_ekle, null);
            final EditText editTextUrunAd = view.findViewById(R.id.editTextAlertUrunEkleUrunAd);
            final EditText editTextUrunFiyat = view.findViewById(R.id.editTextAlertUrunEkleUrunFiyat);

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Ürün Ekle");
            alert.setView(view);
            alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String urunAd = editTextUrunAd.getText().toString();
                    double urunFiyat = Double.parseDouble(editTextUrunFiyat.getText().toString());

                    urunuVeriTabaninaYaz(new Urun("", urunAd, urunFiyat, secilenKategoriId, secilenRestoranId));
                }
            });
            alert.show();
        }
    };




    private void urunuVeriTabaninaYaz (Urun urun) {
        referenceUrunler.add(urun).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Kayıt Başarılı.", Toast.LENGTH_SHORT).show();
                sayfayiYenidenYukle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void sayfayiYenidenYukle () {
        Intent intent = new Intent(getContext(), YoneticiActivity.class);
        intent.putExtra("fragmentId", 4);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }




    private AdapterView.OnItemSelectedListener spinnerRestoranlarItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Restoran restoran = (Restoran) parent.getItemAtPosition(position);
            secilenRestoranId = restoran.getRestoranId();
            kategorilerGetirByRestoranId(secilenRestoranId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };




    private AdapterView.OnItemSelectedListener spinnerKategorilerItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Kategori kategori = (Kategori) parent.getItemAtPosition(position);
            secilenKategoriId = kategori.getKategoriId();
            urunleriGetirByRestoranIdByKategoriId(secilenRestoranId, secilenKategoriId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };




    private void restoranlariGetir () {
        restoranlar.clear();
        Query query = referenceRestoranlar.whereEqualTo("yoneticiId", yoneticiId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String restoranId = snapshot.getId();
                        String restoranAd = (String) snapshot.get("restoranAd");
                        long masaSaiyisi = (long) snapshot.get("masaSayisi");
                        String kasaKullaniciAdi = (String) snapshot.get("kasaKullaniciAdi");
                        String kasaSifre = (String) snapshot.get("kasaSifre");

                        restoranlar.add(new Restoran(restoranId, restoranAd, (int) masaSaiyisi, yoneticiId, kasaKullaniciAdi, kasaSifre));
                    }
                    spinnerAdapterRestoranlar.notifyDataSetChanged();
                }
            }
        });
    }




    private void kategorilerGetirByRestoranId (String restoranId) {
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
                    spinnerAdapterKategoriler.notifyDataSetChanged();
                }
            }
        });
    }




    private void urunleriGetirByRestoranIdByKategoriId (String restoranId, String kategoriId) {
        urunler.clear();

        Query  query = referenceUrunler.whereEqualTo("restoranId", restoranId).whereEqualTo("kategoriId", kategoriId);
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
                    adapterUrunler.notifyDataSetChanged();
                }
            }
        });
    }
}
