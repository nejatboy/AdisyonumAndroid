package com.nejatboy.adisyonum.Adapter.Kasa;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.Rapor;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.Model.SingletonRestoranVerileri;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterTumMasalar extends RecyclerView.Adapter<AdapterTumMasalar.HucreTasarim>{

    private Context context;
    private List<Masa> tumMasalar;
    private SingletonRestoran singletonRestoran = SingletonRestoran.getInstance();
    private SingletonRestoranVerileri singletonRestoranVerileri = SingletonRestoranVerileri.getInstance();
    private Masa secilenMasa;
    private CollectionReference referenceMasalar = FirebaseFirestore.getInstance().collection("Masalar");

    private Dialog dialog;
    private RadioButton radioButtonNakit, radioButtonKrediKarti, radioButtonMultinet, radioButtonTicket, radioButtonSodexo, radioButtonSetcard, radioButtonMetropol;
    private Button buttonHesapAlDialogTutariAl;
    private TextView textViewHesapAlDialogMasaNo, textViewHesapAlDialogMasaTutari;
    private EditText editTextHesapAlDialogTahsilEdilen;

    private HashMap<String, Double> hesaplar;
    private Double alinanTutar = 0.0;

    private CollectionReference referenceAnlikRaporlar = FirebaseFirestore.getInstance().collection("AnlikRaporlar");




    public AdapterTumMasalar(Context context, List<Masa> tumMasalar) {
        this.context = context;
        this.tumMasalar = tumMasalar;
    }




    private void dialogHesapAlKurulumu () {
        dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);        //Tam ekran dialog
        dialog.setContentView(R.layout.popup_kasa_hesap_al);

        radioButtonNakit = dialog.findViewById(R.id.radioButtonHesalAlNakit);
        radioButtonKrediKarti = dialog.findViewById(R.id.radioButtonHesalAlKrediKarti);
        radioButtonMultinet = dialog.findViewById(R.id.radioButtonHesalAlMultinet);
        radioButtonTicket = dialog.findViewById(R.id.radioButtonHesalAlTicket);
        radioButtonSodexo = dialog.findViewById(R.id.radioButtonHesalAlSodexo);
        radioButtonSetcard = dialog.findViewById(R.id.radioButtonHesalAlSetcard);
        radioButtonMetropol = dialog.findViewById(R.id.radioButtonHesalAlMetropol);
        buttonHesapAlDialogTutariAl = dialog.findViewById(R.id.buttonDialogHesapAlTutariAl);
        textViewHesapAlDialogMasaNo = dialog.findViewById(R.id.textViewHesapAlDialogMasaNo);
        textViewHesapAlDialogMasaTutari = dialog.findViewById(R.id.textViewHesapAlDialogMasaToplamTutar);
        editTextHesapAlDialogTahsilEdilen = dialog.findViewById(R.id.editTextHesapAlDialogGirilenTutar);

        hesaplar = new HashMap<>();
        hesaplar.put("nakit", 0.0);
        hesaplar.put("krediKarti", 0.0);
        hesaplar.put("multinet", 0.0);
        hesaplar.put("ticket", 0.0);
        hesaplar.put("sodexo", 0.0);
        hesaplar.put("setcard", 0.0);
        hesaplar.put("metropol", 0.0);

        radioButtonNakit.setChecked(true);

        buttonHesapAlDialogTutariAl.setOnClickListener(buttonHesapAlDialogListener);


    }




    @NonNull
    @Override
    public HucreTasarim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hucre_tum_masalar, parent, false);
        return new HucreTasarim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HucreTasarim holder, int position) {
        final Masa masa = tumMasalar.get(position);

        if (masa.isMasaAcik() && !masa.isMasaYazdirildi()) {
            holder.hucre.setCardBackgroundColor(context.getResources().getColor(R.color.doluMasaHucreRengi));
        } else if (masa.isMasaAcik()&& masa.isMasaYazdirildi()){
            holder.hucre.setCardBackgroundColor(context.getResources().getColor(R.color.yazdirilmisMasaHucreRengi));
        } else {
            holder.hucre.setCardBackgroundColor(context.getResources().getColor(R.color.bosMasaHucreRengi));
        }

        holder.textViewMasaNo.setText("" + masa.getMasaNo());
        holder.textViewTutar.setText(masa.getMasaTutar() + " TL");
        holder.textViewGarsonAdi.setText(garsonAdiGetirByGarsonId(masa.getGarsonId()));

        holder.hucre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_kasa_secenekler, popupMenu.getMenu());
                popupMenu.show();

                secilenMasa = masa;
                popupMenu.setOnMenuItemClickListener(popupMenuItemClickListener);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tumMasalar.size();
    }




    private String garsonAdiGetirByGarsonId (String garsonId) {
        for (Garson garson: singletonRestoranVerileri.getGarsonlar()) {
            if (garson.getGarsonId().equals(garsonId)) {
                return garson.getGarsonAd();
            }
        }
        return null;
    }




    private void adisyonuYazdirYadaGeriAl(Masa masa, final boolean islem) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("masaYazdirildi", islem);
        referenceMasalar.document(masa.getMasaId()).update(veri);
    }




    private void masaninHesabiniAl () {
        //Restorana ait belge var mı?
        referenceAnlikRaporlar.whereEqualTo("restoranId", singletonRestoran.getRestoranId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.getDocuments().size() != 0) {     //Belge var
                        DocumentSnapshot snapshotBelge = queryDocumentSnapshots.getDocuments().get(0);
                        String restoranId = (String) snapshotBelge.get("restoranId");
                        HashMap<String, Double> hesaplar = (HashMap<String, Double>) snapshotBelge.get("hesaplar");
                        HashMap<String, Double> garsonSatislari = (HashMap<String, Double>) snapshotBelge.get("garsonSatislari");
                        Double ciro = (Double) snapshotBelge.get("ciro");

                        raporuGuncelle(new Rapor(snapshotBelge.getId(), restoranId, hesaplar, garsonSatislari, ciro));

                    } else {        //Belge yok
                        anlikRaporOlustur();
                    }
                }
            }
        });
    }




    private void anlikRaporOlustur () {
        HashMap<String, Double> garsonSatisi = new HashMap<>();
        garsonSatisi.put(secilenMasa.getGarsonId(), alinanTutar);

        Rapor rapor = new Rapor("", singletonRestoran.getRestoranId(), hesaplar, garsonSatisi, alinanTutar);
        referenceAnlikRaporlar.add(rapor).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                hesapAlindiMasayiSil();
                alinanTutar = 0.0;
            }
        });
    }




    private void raporuGuncelle (Rapor mevcutRapor) {
        Double toplamCiro = mevcutRapor.getCiro() + alinanTutar;

        Double nakitToplami = hesaplar.get("nakit") + mevcutRapor.getHesaplar().get("nakit");
        Double krediKartiToplami = hesaplar.get("krediKarti") + mevcutRapor.getHesaplar().get("krediKarti");
        Double multinetToplami = hesaplar.get("multinet") + mevcutRapor.getHesaplar().get("multinet");
        Double ticketToplami = hesaplar.get("ticket") + mevcutRapor.getHesaplar().get("ticket");
        Double sodexoToplami = hesaplar.get("sodexo") + mevcutRapor.getHesaplar().get("sodexo");
        Double setcardToplami = hesaplar.get("setcard") + mevcutRapor.getHesaplar().get("setcard");
        Double metropolToplami = hesaplar.get("metropol") + mevcutRapor.getHesaplar().get("metropol");

        hesaplar.put("nakit", nakitToplami);
        hesaplar.put("krediKarti", krediKartiToplami);
        hesaplar.put("multinet", multinetToplami);
        hesaplar.put("ticket", ticketToplami);
        hesaplar.put("sodexo", sodexoToplami);
        hesaplar.put("setcard", setcardToplami);
        hesaplar.put("metropol", metropolToplami);

        HashMap<String, Double> garsonSatislari = mevcutRapor.getGarsonSatislari();
        String secilemMasaGarsonId = secilenMasa.getGarsonId();

        if (garsonSatislari.containsKey(secilemMasaGarsonId)) {       //Hesabı alınan masanın garsonu raporda var ise
            Double garsonunToplamSatisi = garsonSatislari.get(secilemMasaGarsonId) + alinanTutar;
            garsonSatislari.put(secilenMasa.getGarsonId(), garsonunToplamSatisi);

        } else {
            garsonSatislari.put(secilemMasaGarsonId, alinanTutar);
        }

        guncelRaporuVeriTabaninaYaz(new Rapor(mevcutRapor.getRaporId(), mevcutRapor.getRestoranId(), hesaplar, garsonSatislari, toplamCiro));
    }




    private void guncelRaporuVeriTabaninaYaz (Rapor rapor) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("ciro", rapor.getCiro());
        veri.put("garsonSatislari", rapor.getGarsonSatislari());
        veri.put("hesaplar", rapor.getHesaplar());
        referenceAnlikRaporlar.document(rapor.getRaporId()).update(veri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hesapAlindiMasayiSil();
                alinanTutar = 0.0;
            }
        });
    }




    private void hesapAlindiMasayiSil () {
            referenceMasalar.document(secilenMasa.getMasaId()).delete();
    }




    private PopupMenu.OnMenuItemClickListener popupMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.actionKasaPopupHesapAl) {      //Hesap alacaz (secilenMasa'ya göre)
                if (secilenMasa.isMasaAcik()) {
                    if (secilenMasa.isMasaYazdirildi()) {       //Hesap alma işlemi
                        dialogHesapAlKurulumu();
                        textViewHesapAlDialogMasaNo.setText("Masa: " + secilenMasa.getMasaNo());
                        textViewHesapAlDialogMasaTutari.setText(secilenMasa.getMasaTutar() + "TL");
                        editTextHesapAlDialogTahsilEdilen.setText("" + secilenMasa.getMasaTutar());
                        dialog.show();

                    } else {
                        Toast.makeText(context, "Önce adisyonu yazdırmalısınız", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Masa boş!", Toast.LENGTH_SHORT).show();
                }

            } else if (item.getItemId() == R.id.actionKasaPopupAdisyonuYazdirGeriAl){       // Adisyon yazdıracaz veya geri alacaz (secilenMasa'ya göre)
                if (secilenMasa.isMasaAcik()) {
                    if (secilenMasa.isMasaYazdirildi()) {   //Geri alırız
                        adisyonuYazdirYadaGeriAl(secilenMasa, false);
                    } else {    //Yazdırırız
                        adisyonuYazdirYadaGeriAl(secilenMasa, true);
                    }
                } else {
                    Toast.makeText(context, "Masa boş!", Toast.LENGTH_SHORT).show();
                }
            }

            return false;
        }
    };




    private View.OnClickListener buttonHesapAlDialogListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String odemeSekli = null;
            if (radioButtonNakit.isChecked()) {
                odemeSekli = "nakit";
            } else if (radioButtonKrediKarti.isChecked()) {
                odemeSekli = "krediKarti";
            }else if (radioButtonMultinet.isChecked()) {
                odemeSekli = "multinet";
            }else if (radioButtonTicket.isChecked()) {
                odemeSekli = "ticket";
            }else if (radioButtonSodexo.isChecked()) {
                odemeSekli = "sodexo";
            }else if (radioButtonSetcard.isChecked()) {
                odemeSekli = "setcard";
            }else if (radioButtonMetropol.isChecked()) {
                odemeSekli = "metropol";
            }
            
            Double girilenTutar = Double.parseDouble(editTextHesapAlDialogTahsilEdilen.getText().toString());
            
            if (girilenTutar <= secilenMasa.getMasaTutar() - alinanTutar) {
                Double tahsilEdilenTutar = hesaplar.get(odemeSekli) + girilenTutar;
                hesaplar.put(odemeSekli, tahsilEdilenTutar);

                alinanTutar = alinanTutar + girilenTutar;

                textViewHesapAlDialogMasaTutari.setText("" + (secilenMasa.getMasaTutar() - alinanTutar));
                editTextHesapAlDialogTahsilEdilen.setText("" + (secilenMasa.getMasaTutar() - alinanTutar));

                if (alinanTutar >= secilenMasa.getMasaTutar()) {        //Hesap alma tamamlanır
                    dialog.dismiss();
                    masaninHesabiniAl();
                }
            } else {
                Toast.makeText(context, "Girdiğiniz tutar hesaptan fazla!", Toast.LENGTH_SHORT).show();
            }
            
        }
    };









    // INNER CLASS
    class HucreTasarim extends RecyclerView.ViewHolder {
        TextView textViewMasaNo, textViewTutar, textViewGarsonAdi;
        CardView hucre;

        public HucreTasarim(@NonNull View itemView) {
            super(itemView);
            hucre = itemView.findViewById(R.id.cardViewTumMasalarHucre);
            textViewMasaNo = itemView.findViewById(R.id.textViewTumMasalarHucreMasaNo);
            textViewTutar = itemView.findViewById(R.id.textViewTumMasalarHucreTutar);
            textViewGarsonAdi = itemView.findViewById(R.id.textViewTumMasalarHucreGarsonAdi);
        }
    }
}