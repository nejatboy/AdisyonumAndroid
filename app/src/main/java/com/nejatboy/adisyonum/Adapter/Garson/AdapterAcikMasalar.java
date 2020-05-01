package com.nejatboy.adisyonum.Adapter.Garson;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterAcikMasalar extends RecyclerView.Adapter<AdapterAcikMasalar.HucreAcikMasaTasarimi>{

    private Context context;
    private List<Masa> liste;
    private CollectionReference referenceMasalar = FirebaseFirestore.getInstance().collection("Masalar");
    private List<Masa> bosMasalar = new ArrayList<>();
    private Masa secilenBosMasa;




    public AdapterAcikMasalar(Context context, List<Masa> liste) {
        this.context = context;
        this.liste = liste;

        bosMasalariGetir();
    }




    @NonNull
    @Override
    public HucreAcikMasaTasarimi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_acik_masalar, parent, false);
        return new HucreAcikMasaTasarimi(view);
    }




    @Override
    public void onBindViewHolder(@NonNull HucreAcikMasaTasarimi holder, int position) {
        final Masa acikMasa = liste.get(position);

        if (acikMasa.isMasaYazdirildi()) {
            holder.hucre.setBackgroundColor(context.getResources().getColor(R.color.yazdirilmisMasaHucreRengi));
        } else {
            holder.hucre.setBackgroundColor(context.getResources().getColor(R.color.bosMasaHucreRengi));
        }

        holder.textViewMasaNo.setText("Masa: " + acikMasa.getMasaNo());
        holder.textViewMasaTutar.setText(acikMasa.getMasaTutar() + " TL" );
        holder.imageViewPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_acik_masa_secenekler, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.actionAcikMasaPopupAdisyonuYazdir) {
                            masayiYazdir(acikMasa.getMasaId());

                        } else if (item.getItemId() == R.id.actionAcikMasaPopupMasaTasi) {
                            alertGosterBosMasalar(acikMasa);
                        }
                        return false;
                    }
                });
            }
        });
    }




    @Override
    public int getItemCount() {
        return liste.size();
    }




    private void masayiYazdir(String masaId) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("masaYazdirildi", true);
        referenceMasalar.document(masaId).update(veri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Adisyon yazdırıldı.", Toast.LENGTH_SHORT).show();
                //PRINTER'DA adisyon çıkar
                notifyDataSetChanged();
            }
        });
    }




    private void masayiGuncelle (Masa masa) {
        if (bosMasalar.contains(secilenBosMasa)) {
            HashMap<String, Object> veri = new HashMap<>();
            veri.put("masaNo", secilenBosMasa.getMasaNo());
            referenceMasalar.document(masa.getMasaId()).update(veri).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "Taşıma başarılı.", Toast.LENGTH_SHORT).show();
                    bosMasalariGetir();
                    notifyDataSetChanged();

                }
            });
        } else {
            Toast.makeText(context, "Seçtiğiniz masa dolduğundan taşınma gerçekleştirilemedi.", Toast.LENGTH_SHORT).show();
        }
    }




    private void  alertGosterBosMasalar (final Masa gecerliMasa) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_masa_tasi, null);

        Spinner spinnerBosMasalar = view.findViewById(R.id.spinnerAlertMasaTasiBosMasalar);
        SpinnerAdapterAlertMasaTasi spinnerAdapter = new SpinnerAdapterAlertMasaTasi(context, bosMasalar);
        spinnerBosMasalar.setAdapter(spinnerAdapter);
        spinnerBosMasalar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secilenBosMasa = (Masa) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Masa Taşıması Yapılacak");
        alert.setMessage("Geçerli Masa: " + gecerliMasa.getMasaNo());
        alert.setView(view);
        alert.setPositiveButton("Taşı", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                masayiGuncelle(gecerliMasa);
            }
        }).show();
    }




    private void bosMasalariGetir() {
        bosMasalar.clear();

        referenceMasalar.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    List<Integer> doluMasaNumaralari = new ArrayList<>();

                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        long masaNo = (long) snapshot.get("masaNo");
                        doluMasaNumaralari.add((int) masaNo);
                    }

                    for (int i = 1; i<=SingletonRestoran.getInstance().getMasaSayisi(); i++) {
                        if (!doluMasaNumaralari.contains(i)) {
                            bosMasalar.add(new Masa("", i, "", 0.0, "", false, false, null));
                        }
                    }
                }
            }
        });
    }







    // INNER CLASS
    class HucreAcikMasaTasarimi extends RecyclerView.ViewHolder {

        TextView textViewMasaNo, textViewMasaTutar;
        ImageView imageViewPopup;
        View hucre;

        public HucreAcikMasaTasarimi(@NonNull View itemView) {
            super(itemView);
            textViewMasaNo = itemView.findViewById(R.id.textViewAcikMasalarHucreMasaNo);
            textViewMasaTutar = itemView.findViewById(R.id.textViewAcikMasalarHucreMasaTutar);
            imageViewPopup = itemView.findViewById(R.id.imageViewAcikMasalarHucrePopupAc);
            hucre = itemView;
        }
    }
}
