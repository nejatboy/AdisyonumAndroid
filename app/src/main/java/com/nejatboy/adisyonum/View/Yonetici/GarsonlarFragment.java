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
import com.nejatboy.adisyonum.Adapter.Yonetici.AdapterGarsonlar;
import com.nejatboy.adisyonum.Adapter.Yonetici.SpinnerAdapterRestoranlar;
import com.nejatboy.adisyonum.Controller.YoneticiActivity;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class GarsonlarFragment extends Fragment {

    private Spinner spinnerRestoranlar;
    private Button buttonGarsonEkle;
    private SpinnerAdapterRestoranlar spinnerAdapterRestoranlar ;
    private List<Restoran> restoranlar = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference referenceRestoranlar = firestore.collection("Restoranlar");
    private String yoneticiId = "";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private AdapterGarsonlar adapterGarsonlar;
    private List<Garson> garsonlar = new ArrayList<>();
    private CollectionReference referenceGarsonlar = firestore.collection("Garsonlar");
    private String secilenRestoranId = "";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garsonlar, container, false);

        spinnerRestoranlar = view.findViewById(R.id.spinnerGarsonlarFragment);
        recyclerView = view.findViewById(R.id.recyclerViewGarsonlarFragment);
        buttonGarsonEkle = view.findViewById(R.id.buttonGarsonlarFragmentGarsonEkle);

        yoneticiId = auth.getCurrentUser().getUid();

        restoranlariGetir();

        spinnerAdapterRestoranlar = new SpinnerAdapterRestoranlar(getContext(), restoranlar);
        spinnerRestoranlar.setAdapter(spinnerAdapterRestoranlar);
        spinnerRestoranlar.setOnItemSelectedListener(spinnerItemSelectListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterGarsonlar = new AdapterGarsonlar(getContext(), garsonlar);
        recyclerView.setAdapter(adapterGarsonlar);

        buttonGarsonEkle.setOnClickListener(buttonGarsonEkleClickListener);

        return view;
    }




    private View.OnClickListener buttonGarsonEkleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_garson_ekle, null);
            final EditText editTextGarsonAd = view.findViewById(R.id.editTextAlertGarsonEkleGarsonAd);
            final EditText editTextGarsonKullaniciAd = view.findViewById(R.id.editTextAlertGarsonEkleGarsonKullaniciAdi);
            final EditText editTextGarsonSifre = view.findViewById(R.id.editTextAlertGarsonEkleGarsonSifre);

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Garson Ekle");
            alert.setView(view);
            alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String garsonAd = editTextGarsonAd.getText().toString();
                    String garsonKullaniciAdi = editTextGarsonKullaniciAd.getText().toString();
                    String garsonSifre = editTextGarsonSifre.getText().toString();

                    garsonuVeriTabaninaYaz(garsonAd, garsonKullaniciAdi, garsonSifre);
                }
            });
            alert.show();
        }
    };




    private void garsonuVeriTabaninaYaz (String garsonAd, String garsonKullaniciAdi, String garsonSifre) {
        if (garsonAd.equals("") || garsonKullaniciAdi.equals("") || garsonSifre.equals("") || secilenRestoranId.equals("")) {
            Toast.makeText(getContext(), "Tüm Alanları Doldurunuz.", Toast.LENGTH_SHORT).show();

        } else {
            Garson garson = new Garson("", garsonAd, garsonKullaniciAdi, garsonSifre, secilenRestoranId);
            referenceGarsonlar.add(garson).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
    }




    private AdapterView.OnItemSelectedListener spinnerItemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Restoran restoran = (Restoran) parent.getItemAtPosition(position);
            secilenRestoranId = restoran.getRestoranId();
            garsonlariGetirByRestoranId(secilenRestoranId);
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




    private void sayfayiYenidenYukle () {
        Intent intent = new Intent(getContext(), YoneticiActivity.class);
        intent.putExtra("fragmentId", 2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }




    private void garsonlariGetirByRestoranId (final String restoranId) {
        garsonlar.clear();

        Query query = referenceGarsonlar.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String garsonId = snapshot.getId();
                        String garsonAd = (String) snapshot.get("garsonAd");
                        String garsonKullaniciAdi = (String) snapshot.get("garsonKullaniciAd");
                        String garsonSifre = (String) snapshot.get("garsonSifre");

                        garsonlar.add(new Garson(garsonId, garsonAd, garsonKullaniciAdi, garsonSifre, restoranId));
                    }
                    adapterGarsonlar.notifyDataSetChanged();
                }
            }
        });
    }
}
