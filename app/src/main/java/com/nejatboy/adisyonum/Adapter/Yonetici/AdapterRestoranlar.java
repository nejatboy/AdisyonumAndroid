package com.nejatboy.adisyonum.Adapter.Yonetici;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Controller.YoneticiActivity;
import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.R;

import java.util.HashMap;
import java.util.List;

public class AdapterRestoranlar extends BaseAdapter {

    private Context context;
    private List<Restoran> restoranlar;
    private String yoneticiId;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference referenceRestoranlar = firestore.collection("Restoranlar");
    private CollectionReference referenceKategoriler = firestore.collection("Kategoriler");
    private CollectionReference referenceGarsonlar = firestore.collection("Garsonlar");
    private CollectionReference referenceUrunler = firestore.collection("Urunler");
    private EditText editTextRestoranAd;
    private EditText editTextMasaSayisi ;
    private EditText editTextKasaKullaniciAdi;
    private EditText editTextKasaSifre;


    

    public AdapterRestoranlar(Context context, List<Restoran> restoranlar) {
        this.context = context;
        this.restoranlar = restoranlar;
        yoneticiId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }




    @Override
    public int getCount() {
        return restoranlar.size();
    }




    @Override
    public Object getItem(int position) {
        return null;
    }




    @Override
    public long getItemId(int position) {
        return 0;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_restoranlar, parent, false);
        final Restoran restoran = restoranlar.get(position);

        TextView textView = view.findViewById(R.id.textViewRestoranlarHucre);
        textView.setText(restoranlar.get(position).getRestoranAd());

        ImageView imageViewPopUpAc = view.findViewById(R.id.imageViewRestoranlarHucrePopupAc);
        imageViewPopUpAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_restoran_secenekler, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.popupRestoranSeceneklerActionSil) {
                            alertSilinecekGoster(restoran);

                        } else if (item.getItemId() == R.id.popupRestoranSeceneklerActionDuzenle) {
                            alertGoster(restoran);

                        } else if (item.getItemId() == R.id.popupRestoranSeceneklerRestoranIdKopyala) {
                            ClipboardManager pano = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("restoranKodu", restoran.getRestoranId());
                            pano.setPrimaryClip(clipData);
                            Toast.makeText(context, "Restoran kodu panoya kopyalandı.", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });
            }
        });

        return view;
    }




    private void alertSilinecekGoster (final Restoran restoran) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Silinecek");
        alert.setMessage("Restorana ait tüm veriler (Kategoriler, Garsonlar, Ürünler) silinecek. Emin misiniz?");
        alert.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restoraniSil(restoran);
            }
        }).show();
    }




    private void restoraniSil (final Restoran restoran) {
        referenceRestoranlar.document(restoran.getRestoranId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        kategorileriSilByRestoranId(restoran.getRestoranId());
                    }
                })
        ;
    }




    private void kategorileriSilByRestoranId (final String restoranId) {
        Query query = referenceKategoriler.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots!= null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        referenceKategoriler.document(snapshot.getId()).delete();
                    }
                    garsonlariSilByRestoranId(restoranId);
                }
            }
        });
    }




    private void garsonlariSilByRestoranId (final String restoranId) {
        Query query = referenceGarsonlar.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots!= null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        referenceGarsonlar.document(snapshot.getId()).delete();
                    }
                    urunleriSilByRestoranId(restoranId);
                }
            }
        });
    }




    private void urunleriSilByRestoranId (String restoranId) {
        Query query = referenceUrunler.whereEqualTo("restoranId", restoranId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots!= null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        referenceUrunler.document(snapshot.getId()).delete();
                    }
                    Toast.makeText(context, "Silindi.", Toast.LENGTH_SHORT).show();
                    sayfayiYenidenYukle();
                }
            }
        });
    }




    private void alertGoster (final Restoran restoran) {
        final View alertView = LayoutInflater.from(context).inflate(R.layout.alert_restoran_ekle, null);

        editTextRestoranAd = alertView.findViewById(R.id.editTextAlertRestoranEkleRestoranAd);
        editTextMasaSayisi = alertView.findViewById(R.id.editTextAlertRestoranEkleMasaSayisi);
        editTextKasaKullaniciAdi = alertView.findViewById(R.id.editTextAlertRestoranEkleKasaKullaniciAdi);
        editTextKasaSifre = alertView.findViewById(R.id.editTextAlertRestoranEkleKasaSifre);

        editTextRestoranAd.setText(restoran.getRestoranAd());
        editTextMasaSayisi.setText("" + restoran.getMasaSayisi());
        editTextKasaKullaniciAdi.setText(restoran.getKasaKullaniciAdi());
        editTextKasaSifre.setText(restoran.getKasaSifre());

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Restoranı Düzenle");
        alert.setView(alertView);
        alert.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String restoranAd = editTextRestoranAd.getText().toString();
                int masaSayisi = Integer.parseInt(editTextMasaSayisi.getText().toString());
                String kasaKullaniciAdi = editTextKasaKullaniciAdi.getText().toString();
                String kasaSifre = editTextKasaSifre.getText().toString();

                restoraniGuncelle(new Restoran(restoran.getRestoranId(), restoranAd, masaSayisi, restoran.getYoneticiId(), kasaKullaniciAdi, kasaSifre));
            }
        });

        alert.show();
    }




    private void restoraniGuncelle (Restoran restoran) {
        HashMap<String, Object> veri = new HashMap<>();

        veri.put("restoranAd", restoran.getRestoranAd());
        veri.put("kasaKullaniciAdi", restoran.getKasaKullaniciAdi());
        veri.put("kasaSifre", restoran.getKasaSifre());
        veri.put("masaSayisi", restoran.getMasaSayisi());

        referenceRestoranlar.document(restoran.getRestoranId()).update(veri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Güncellendi.", Toast.LENGTH_SHORT).show();
                        sayfayiYenidenYukle();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        ;
    }




    private void sayfayiYenidenYukle () {
        Intent intent = new Intent(context, YoneticiActivity.class);
        intent.putExtra("fragmentId", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();       //Burası önemli
    }
}
