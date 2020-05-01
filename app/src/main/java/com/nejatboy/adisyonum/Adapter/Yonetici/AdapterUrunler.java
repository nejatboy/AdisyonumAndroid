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
import com.nejatboy.adisyonum.Model.Urun;
import com.nejatboy.adisyonum.R;

import java.util.HashMap;
import java.util.List;

public class AdapterUrunler extends RecyclerView.Adapter<AdapterUrunler.AdapterUrunlerHucre>{

    private Context context;
    private List<Urun> urunler;
    private CollectionReference referenceUrunler = FirebaseFirestore.getInstance().collection("Urunler");
    private EditText editTextUrunAd;
    private EditText editTextUrunFiyat;



    public AdapterUrunler(Context context, List<Urun> urunler) {
        this.context = context;
        this.urunler = urunler;
    }




    @NonNull
    @Override
    public AdapterUrunlerHucre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_urunler, parent, false);
        return new AdapterUrunlerHucre(view);
    }




    @Override
    public void onBindViewHolder(@NonNull AdapterUrunlerHucre holder, int position) {
        final Urun urun = urunler.get(position);

        holder.textViewUrunlerUrunAd.setText(urun.getUrunAd());
        holder.textViewUrunlerUrunFiyat.setText(urun.getUrunFiyat() + " TL");

        holder.imageViewUrunlerHucre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_secenekler, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.popupActionSil) {
                            urunuSil(urun);

                        } else if (item.getItemId() == R.id.popupActionDuzenle) {
                            alertGoster(urun);
                        }
                        return false;
                    }
                });
            }
        });
    }




    @Override
    public int getItemCount() {
        return urunler.size();
    }




    private void alertGoster (final Urun urun) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_urun_ekle, null);
        editTextUrunAd = view.findViewById(R.id.editTextAlertUrunEkleUrunAd);
        editTextUrunFiyat = view.findViewById(R.id.editTextAlertUrunEkleUrunFiyat);

        editTextUrunFiyat.setText("" + urun.getUrunFiyat());
        editTextUrunAd.setText(urun.getUrunAd());


        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Ürünü Düzenle");
        alert.setView(view);
        alert.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String urunAd = editTextUrunAd.getText().toString();
                double urunFiyat = Double.parseDouble(editTextUrunFiyat.getText().toString());

                urunuGucelle(new Urun(urun.getUrunId(), urunAd, urunFiyat, urun.getKategoriId(), urun.getRestoranId()));
            }
        }).show();
    }




    private void urunuGucelle (Urun urun) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("urunAd", urun.getUrunAd());
        veri.put("urunFiyat", urun.getUrunFiyat());

        referenceUrunler.document(urun.getUrunId()).update(veri)
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




    private void urunuSil (Urun urun) {
        referenceUrunler.document(urun.getUrunId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("fragmentId", 4);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();
    }










    class AdapterUrunlerHucre extends RecyclerView.ViewHolder {
        TextView textViewUrunlerUrunAd, textViewUrunlerUrunFiyat;
        ImageView imageViewUrunlerHucre;

        public AdapterUrunlerHucre(@NonNull View itemView) {
            super(itemView);
            textViewUrunlerUrunAd = itemView.findViewById(R.id.textViewUrunlerHucreUrunAd);
            imageViewUrunlerHucre = itemView.findViewById(R.id.imageViewUrunlerHucrePopupAc);
            textViewUrunlerUrunFiyat = itemView.findViewById(R.id.textViewUrunlerHucreUrunFiyat);
        }
    }
}
