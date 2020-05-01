package com.nejatboy.adisyonum.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nejatboy.adisyonum.Adapter.Garson.AdapterAcikMasalar;
import com.nejatboy.adisyonum.Adapter.Garson.SpinnerAdapterAlertMasaTransferi;
import com.nejatboy.adisyonum.Model.Garson;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.SingletonGarson;
import com.nejatboy.adisyonum.Model.SingletonRestoranVerileri;
import com.nejatboy.adisyonum.R;
import com.nejatboy.adisyonum.View.Garson.AcikMasalarFragment;
import com.nejatboy.adisyonum.View.Garson.TumMasalarFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GarsonActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private SingletonGarson singletonGarson = SingletonGarson.getInstance();
    private SingletonRestoranVerileri singletonRestoranVerileri = SingletonRestoranVerileri.getInstance();

    private List<Masa> acikMasalarByGarson = new ArrayList<>();
    private CollectionReference referenceMasalar = FirebaseFirestore.getInstance().collection("Masalar");
    private View viewAlertMasaTransferi;
    private LinearLayout layoutAlertMasaTransferiView;
    Garson masaTransferiSecilenGarson;
    private Boolean acikMasalarAlindi = false;
    private HashMap<CheckBox, Masa> checkBoxMasaMap = new HashMap<>();
    private List<Masa> transferEdilecekMasalar = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garson);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewGarsonActivity);
        toolbar = findViewById(R.id.toolbarGarsonActivitiy);

        toolbar.setTitle(singletonGarson.getGarsonAd());
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentTutucuGarsonActivity, new TumMasalarFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }




    private void acikMasalariAlByGarsonId () {
        acikMasalarByGarson.clear();

        Query query = referenceMasalar.whereEqualTo("garsonId", singletonGarson.getGarsonId()).orderBy("masaNo", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        String masaId = snapshot.getId();
                        long masaNo = (long) snapshot.get("masaNo");
                        Boolean masaAcik = (Boolean) snapshot.get("masaAcik");
                        String garsonId = (String) snapshot.get("garsonId");
                        Double hesap = (Double) snapshot.get("masaTutar");
                        Boolean masaYazdirildi = (Boolean) snapshot.get("masaYazdirildi");
                        List<String> urunler = (List<String>) snapshot.get("urunler");

                        acikMasalarByGarson.add(new Masa(masaId, (int) masaNo, singletonGarson.getRestoranId(), hesap, garsonId, masaAcik, masaYazdirildi, urunler));
                    }
                    acikMasalarAlindi = true;
                }
            }
        });
    }




    private void masayiTransferEt (Masa masa, Garson garson) {
        HashMap<String, Object> veri = new HashMap<>();
        veri.put("garsonId", garson.getGarsonId());

        referenceMasalar.document(masa.getMasaId()).update(veri);
    }




    private void alertMasaTransferiniOlustur () {
        viewAlertMasaTransferi = LayoutInflater.from(this).inflate(R.layout.alert_masa_transferi, null);
        layoutAlertMasaTransferiView = viewAlertMasaTransferi.findViewById(R.id.layoutAlertMasaTransferiCheckBoxTutucu);

        spinnerOlustur(viewAlertMasaTransferi);
        checkBoxlariOlustur(layoutAlertMasaTransferiView);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Masa Transferi");
        alert.setMessage("Seçtiğiniz masaları başka garsona aktarabilirisiniz.");
        alert.setView(viewAlertMasaTransferi);
        alert.setPositiveButton("Aktar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (transferEdilecekMasalar.isEmpty()) {
                    Toast.makeText(GarsonActivity.this, "Masa seçilmedi", Toast.LENGTH_SHORT).show();
                } else {
                    for (Masa masa: transferEdilecekMasalar) {
                        masayiTransferEt(masa, masaTransferiSecilenGarson);
                    }
                    transferEdilecekMasalar.clear();
                    acikMasalarByGarson.clear();
                    acikMasalarAlindi = false;
                }
            }
        }).show();
    }




    private void checkBoxlariOlustur(final LinearLayout layout) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (acikMasalarAlindi) {
                    List<CheckBox> checkBoxlar = new ArrayList<>();

                    for (Masa masa: acikMasalarByGarson) {
                        CheckBox checkBox = new CheckBox(getApplicationContext());
                        checkBox.setText("" + masa.getMasaNo());
                        layout.addView(checkBox);
                        checkBoxlar.add(checkBox);
                        checkBoxMasaMap.put(checkBox, masa);
                    }

                    for (final CheckBox checkBox: checkBoxlar) {
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (checkBox.isChecked()) {
                                     transferEdilecekMasalar.add(checkBoxMasaMap.get(checkBox));
                                } else {
                                    if (transferEdilecekMasalar.contains(checkBoxMasaMap.get(checkBox))) {
                                        transferEdilecekMasalar.remove(checkBoxMasaMap.get(checkBox));
                                    }
                                }
                            }
                        });
                    }

                    acikMasalarAlindi = false;
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }




    private void spinnerOlustur(View view) {
        Spinner spinnerGarsonlar = view.findViewById(R.id.spinnerAlertMasaTransferiGarsonlar);
        List<Garson> restoranGarsonlari = new ArrayList<>();

        for (Garson garson: singletonRestoranVerileri.getGarsonlar()) {     //Restoranın diğer garsonlarını getir
            if (!singletonGarson.getGarsonId().equals(garson.getGarsonId())) {
                restoranGarsonlari.add(garson);
            }
        }

        spinnerGarsonlar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                masaTransferiSecilenGarson = (Garson) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerAdapterAlertMasaTransferi spinnerAdapterAlertMasaTransferi = new SpinnerAdapterAlertMasaTransferi(getApplicationContext(), restoranGarsonlari);
        spinnerGarsonlar.setAdapter(spinnerAdapterAlertMasaTransferi);
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment secilenFragment = null;

            if (item.getItemId() == R.id.garsonNavTumMasalar) {
                secilenFragment = new TumMasalarFragment();
            } else if (item.getItemId() == R.id.garsonNavAcikMasalar) {
                secilenFragment = new AcikMasalarFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentTutucuGarsonActivity, secilenFragment).commit();
            return true;
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.garson_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.garsonToolbarMenuMasaTransferiYap) {
            acikMasalariAlByGarsonId();

            alertMasaTransferiniOlustur();

        }  else if (item.getItemId() == R.id.garsonToolbarMenuCikisYap) {
            Intent intent = new Intent(getApplicationContext(), GirisActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}
