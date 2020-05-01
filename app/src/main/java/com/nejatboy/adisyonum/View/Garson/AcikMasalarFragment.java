package com.nejatboy.adisyonum.View.Garson;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Adapter.Garson.AdapterAcikMasalar;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.SingletonGarson;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class AcikMasalarFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Masa> acikMasalar = new ArrayList<>();
    private CollectionReference referenceMasalar = FirebaseFirestore.getInstance().collection("Masalar");
    private AdapterAcikMasalar adapterAcikMasalar;
    private SingletonGarson singletonGarson = SingletonGarson.getInstance();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acik_masalar, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAcikMasalarFragment);

        acikMasalariAl();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterAcikMasalar = new AdapterAcikMasalar(getContext(), acikMasalar);
        recyclerView.setAdapter(adapterAcikMasalar);

        veriDegisirseArayuzuGuncelle();

        return view;
    }




    private void acikMasalariAl () {
        acikMasalar.clear();

        Query query = referenceMasalar.whereEqualTo("garsonId", singletonGarson.getGarsonId()).orderBy("masaNo", Query.Direction.ASCENDING);
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

                        acikMasalar.add(new Masa(masaId, (int) masaNo, restoranId, hesap, garsonId, masaAcik, masaYazdirildi, urunler));
                    }
                    adapterAcikMasalar.notifyDataSetChanged();
                }
            }
        });
    }




    private void veriDegisirseArayuzuGuncelle () {
        referenceMasalar.whereEqualTo("restoranId", SingletonRestoran.getInstance().getRestoranId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                acikMasalariAl();
            }
        });
    }
}
