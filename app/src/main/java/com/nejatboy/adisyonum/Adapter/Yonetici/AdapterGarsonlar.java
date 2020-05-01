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
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.R;

import java.util.HashMap;
import java.util.List;

public class AdapterGarsonlar extends RecyclerView.Adapter<AdapterGarsonlar.AdapterGarsonlarHucre>{

    private Context context;
    private List<Garson> garsonlar;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference referenceGarsonlar = firestore.collection("Garsonlar");
    private EditText editTextGarsonAd;
    private EditText editTextGarsonKullaniciAd;
    private EditText editTextGarsonSifre;




    public AdapterGarsonlar(Context context, List<Garson> garsonlar) {
        this.context = context;
        this.garsonlar = garsonlar;
    }




    @NonNull
    @Override
    public AdapterGarsonlarHucre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_garsonlar, parent, false);
        return new AdapterGarsonlarHucre(view);
    }




    @Override
    public void onBindViewHolder(@NonNull AdapterGarsonlarHucre holder, int position) {
        final Garson garson = garsonlar.get(position);

        holder.textViewGaronHucre.setText(garson.getGarsonAd());

        holder.imageViewGarsonHucrePopUpAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_secenekler, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.popupActionSil) {
                            garsonuSil(garson);

                        } else if (item.getItemId() == R.id.popupActionDuzenle) {
                            alertGoster(garson);
                        }
                        return true;
                    }
                });
            }
        });
    }




    @Override
    public int getItemCount() {
        return garsonlar.size();
    }




    private void garsonuSil (Garson garson) {
        referenceGarsonlar.document(garson.getGarsonId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sayfayiYenidenYukle();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void sayfayiYenidenYukle () {
        Intent intent = new Intent(context, YoneticiActivity.class);
        intent.putExtra("fragmentId", 2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();       //Burası önemli
    }




    private void alertGoster (final Garson garson) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_garson_ekle, null);
        editTextGarsonAd = view.findViewById(R.id.editTextAlertGarsonEkleGarsonAd);
        editTextGarsonKullaniciAd = view.findViewById(R.id.editTextAlertGarsonEkleGarsonKullaniciAdi);
        editTextGarsonSifre = view.findViewById(R.id.editTextAlertGarsonEkleGarsonSifre);

        editTextGarsonAd.setText(garson.getGarsonAd());
        editTextGarsonKullaniciAd.setText(garson.getGarsonKullaniciAd());
        editTextGarsonSifre.setText(garson.getGarsonSifre());

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Garsonu Düzenle");
        alert.setView(view);
        alert.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String garsonAd = editTextGarsonAd.getText().toString();
                String garsonKullaniciAd = editTextGarsonKullaniciAd.getText().toString();
                String garsonSifre = editTextGarsonSifre.getText().toString();

                garsonuGuncelle(new Garson(garson.getGarsonId(), garsonAd, garsonKullaniciAd, garsonSifre, garson.getRestoranId()));
            }
        });
        alert.show();
    }




    private void garsonuGuncelle (Garson garson) {
        HashMap<String, Object> veri = new HashMap<>();

        veri.put("garsonAd", garson.getGarsonAd());
        veri.put("garsonKullaniciAd", garson.getGarsonKullaniciAd());
        veri.put("garsonSifre", garson.getGarsonSifre());

        referenceGarsonlar.document(garson.getGarsonId()).update(veri)
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
                });
    }









    class AdapterGarsonlarHucre extends RecyclerView.ViewHolder {

        TextView textViewGaronHucre;
        ImageView imageViewGarsonHucrePopUpAc;

        public AdapterGarsonlarHucre(@NonNull View itemView) {
            super(itemView);
            textViewGaronHucre = itemView.findViewById(R.id.textViewGarsonlarHucre);
            imageViewGarsonHucrePopUpAc = itemView.findViewById(R.id.imageViewGarsonlarHucrePopupAc);
        }
    }
}
