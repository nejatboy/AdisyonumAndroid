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
import com.nejatboy.adisyonum.Adapter.Yonetici.AdapterKategoriler;
import com.nejatboy.adisyonum.Adapter.Yonetici.SpinnerAdapterRestoranlar;
import com.nejatboy.adisyonum.Controller.YoneticiActivity;
import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class KategorilerFragment extends Fragment {

    private Spinner spinnerRestoranlar;
    private SpinnerAdapterRestoranlar spinnerAdapterRestoranlar;
    private List<Restoran> restoranlar = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference referenceRestoranlar = firestore.collection("Restoranlar");
    private String yoneticiId = "";

    private Button buttonKategoriEkle;
    private CollectionReference referenceKategoriler = firestore.collection("Kategoriler");
    private String secilenrestoranId = "";

    private RecyclerView recyclerView;
    private List<Kategori> kategoriler = new ArrayList<>();
    private AdapterKategoriler adapterKategoriler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kategoriler, container, false);

        spinnerRestoranlar = view.findViewById(R.id.spinnerKategorilerFragment);
        buttonKategoriEkle = view.findViewById(R.id.buttonKategorilerFragmentKategoriEkle);
        recyclerView = view.findViewById(R.id.recyclerViewKategorilerFragment);

        yoneticiId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        restoranlariGetir();

        spinnerAdapterRestoranlar = new SpinnerAdapterRestoranlar(getContext(), restoranlar);
        spinnerRestoranlar.setAdapter(spinnerAdapterRestoranlar);
        spinnerRestoranlar.setOnItemSelectedListener(spinnerItemSelectListener);

        buttonKategoriEkle.setOnClickListener(buttonKategoriEkleListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterKategoriler = new AdapterKategoriler(getContext(), kategoriler);
        recyclerView.setAdapter(adapterKategoriler);

        return view;
    }




    private View.OnClickListener buttonKategoriEkleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_kategori_ekle, null);
            final EditText editTextKategoriAd = view.findViewById(R.id.editTextAlertKategoriEklekategoriAd);

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Kategori Ekle");
            alert.setView(view);
            alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Kategori kategori = new Kategori("", editTextKategoriAd.getText().toString(), secilenrestoranId);
                    kategoriyiVeriTabaninaYaz(kategori);
                }
            });

            alert.show();
        }
    };




    private void kategoriyiVeriTabaninaYaz (Kategori kategori) {
        referenceKategoriler.add(kategori).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("fragmentId", 3);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }




    private AdapterView.OnItemSelectedListener spinnerItemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Restoran restoran = (Restoran) parent.getItemAtPosition(position);
            secilenrestoranId = restoran.getRestoranId();
            kategorileriGetirByRestoranId(secilenrestoranId);
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
                    adapterKategoriler.notifyDataSetChanged();
                }
            }
        });
    }
}
