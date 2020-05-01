package com.nejatboy.adisyonum.Adapter.Yonetici;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nejatboy.adisyonum.Controller.YoneticiActivity;
import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.R;

import java.util.HashMap;
import java.util.List;

public class AdapterKategoriler extends RecyclerView.Adapter<AdapterKategoriler.AdapterKategorilerHucre>{

    private Context context;
    private List<Kategori> kategoriler;
    private CollectionReference referenceKategoriler = FirebaseFirestore.getInstance().collection("Kategoriler");
    private EditText editTextKategoriAdi;



    public AdapterKategoriler(Context context, List<Kategori> kategoriler) {
        this.context = context;
        this.kategoriler = kategoriler;
    }




    @NonNull
    @Override
    public AdapterKategorilerHucre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_kategorler, parent, false);
        return new AdapterKategorilerHucre(view);
    }




    @Override
    public void onBindViewHolder(@NonNull AdapterKategorilerHucre holder, int position) {
        final Kategori kategori = kategoriler.get(position);

        holder.textViewKategoriHucre.setText(kategori.getKategoriAd());

        holder.imageViewKategoriHucre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_secenekler, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.popupActionSil) {
                            kategoriyiSil(kategori);

                        } else if (item.getItemId() == R.id.popupActionDuzenle) {
                            alertGoster(kategori);
                        }
                        return true;
                    }
                });
            }
        });
    }




    @Override
    public int getItemCount() {
        return kategoriler.size();
    }




    private void alertGoster (final Kategori kategori) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_kategori_ekle, null);
        editTextKategoriAdi = view.findViewById(R.id.editTextAlertKategoriEklekategoriAd);

        editTextKategoriAdi.setText(kategori.getKategoriAd());

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Kategoriyi Düzenle");
        alert.setView(view);
        alert.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kategoriyiGuncelle(new Kategori(kategori.getKategoriId(), editTextKategoriAdi.getText().toString(), kategori.getRestoranId()));
            }
        });
        alert.show();
    }




    private void kategoriyiGuncelle (Kategori kategori) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("kategoriAd", kategori.getKategoriAd());
        referenceKategoriler.document(kategori.getKategoriId()).update(veri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Güncellendi", Toast.LENGTH_SHORT).show();
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



    private void kategoriyiSil (Kategori kategori) {
        referenceKategoriler.document(kategori.getKategoriId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show();
                sayfayiYenidenYukle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }




    private void sayfayiYenidenYukle () {
        Intent intent = new Intent(context, YoneticiActivity.class);
        intent.putExtra("fragmentId", 3);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();
    }










    class AdapterKategorilerHucre extends RecyclerView.ViewHolder {
        TextView textViewKategoriHucre;
        ImageView imageViewKategoriHucre;

        public AdapterKategorilerHucre(@NonNull View itemView) {
            super(itemView);
            textViewKategoriHucre = itemView.findViewById(R.id.textViewKategorilerHucre);
            imageViewKategoriHucre = itemView.findViewById(R.id.imageViewKategorilerHucrePopupAc);
        }
    }
}
