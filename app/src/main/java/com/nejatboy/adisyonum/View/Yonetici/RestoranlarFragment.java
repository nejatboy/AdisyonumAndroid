package com.nejatboy.adisyonum.View.Yonetici;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.nejatboy.adisyonum.Adapter.Yonetici.AdapterRestoranlar;
import com.nejatboy.adisyonum.Controller.YoneticiActivity;
import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class RestoranlarFragment extends Fragment {
    private static final String TAG = "RestoranlarFragment";

    private Button buttonRestoranEkle;
    private ListView listView;
    private List<Restoran> restoranlar = new ArrayList<>();
    private AdapterRestoranlar adapterRestoranlar;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference referenceRestoranlar = firestore.collection("Restoranlar");
    private String yoneticiId = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restoranlar, container, false);

        buttonRestoranEkle = view.findViewById(R.id.buttonRestoranlarFragmentRestoranEkle);
        listView = view.findViewById(R.id.listViewRestoranlarFragment);

        yoneticiId = auth.getCurrentUser().getUid();

        restoranlariGetir();

        buttonRestoranEkle.setOnClickListener(buttonClickListener);

        adapterRestoranlar = new AdapterRestoranlar(getContext(), restoranlar);
        listView.setAdapter(adapterRestoranlar);

        return view;
    }




    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_restoran_ekle, null);
            final EditText editTextRestoranAd = view.findViewById(R.id.editTextAlertRestoranEkleRestoranAd);
            final EditText editTextMasaSayisi = view.findViewById(R.id.editTextAlertRestoranEkleMasaSayisi);
            final EditText editTextKasaKullaniciAdi = view.findViewById(R.id.editTextAlertRestoranEkleKasaKullaniciAdi);
            final EditText editTextKasaSifre = view.findViewById(R.id.editTextAlertRestoranEkleKasaSifre);

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Restoran Ekle");
            alert.setView(view);
            alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String restoranAd = editTextRestoranAd.getText().toString();
                    String masaSayisi = editTextMasaSayisi.getText().toString();
                    String kasaKullaniciAdi = editTextKasaKullaniciAdi.getText().toString();
                    String kasaSifre = editTextKasaSifre.getText().toString();

                    restoraniVeriTabaninaYaz(restoranAd, masaSayisi, kasaKullaniciAdi, kasaSifre);
                }
            });

            alert.show();
        }
    };




    private void restoraniVeriTabaninaYaz (String restoranAd, String masaSayisi, String kasaKullaniciAdi, String kasaSifre) {

        if (restoranAd.equals("") || masaSayisi.equals("") || kasaKullaniciAdi.equals("") || kasaSifre.equals("")) {
            Toast.makeText(getContext(), "Tüm alanları doldurmanız gerekir.", Toast.LENGTH_SHORT).show();

        } else {
            int masaSayisiInt = Integer.parseInt(masaSayisi);
            Restoran restoran = new Restoran("", restoranAd, masaSayisiInt, yoneticiId, kasaKullaniciAdi, kasaSifre);

            referenceRestoranlar.add(restoran)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(), "Kayıt başarılı", Toast.LENGTH_SHORT).show();
                            sayfayiYenidenYukle();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }




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
                    adapterRestoranlar.notifyDataSetChanged();
                }
            }
        });
    }




    private void sayfayiYenidenYukle () {
        Intent intent = new Intent(getContext(), YoneticiActivity.class);
        intent.putExtra("fragmentId", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }
}
