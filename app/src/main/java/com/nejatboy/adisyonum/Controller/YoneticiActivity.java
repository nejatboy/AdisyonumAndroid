package com.nejatboy.adisyonum.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.nejatboy.adisyonum.R;
import com.nejatboy.adisyonum.View.Yonetici.GarsonlarFragment;
import com.nejatboy.adisyonum.View.Yonetici.KategorilerFragment;
import com.nejatboy.adisyonum.View.Yonetici.RestoranlarFragment;
import com.nejatboy.adisyonum.View.Yonetici.UrunlerFragment;

public class YoneticiActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private FirebaseAuth auth ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yonetici);

        toolbar = findViewById(R.id.toolbarYoneticiActivity);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewYoneticiActivity);

        toolbar.setTitle("Yönetici");
        setSupportActionBar(toolbar);

        int fragmentId = getIntent().getIntExtra("fragmentId", 0);
        baslangicFragmentYukle(fragmentId);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        auth = FirebaseAuth.getInstance();
    }




    private void baslangicFragmentYukle (int fragmentId) {
        Fragment secilenFragment = null;

        if (fragmentId == 0 || fragmentId == 1) {
            secilenFragment = new RestoranlarFragment();
        } else if (fragmentId == 2) {
            secilenFragment = new GarsonlarFragment() ;
        } else if (fragmentId == 3) {
            secilenFragment = new KategorilerFragment();
        } else if (fragmentId == 4) {
            secilenFragment = new UrunlerFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.fragementTutucuYoneticiActivity, secilenFragment).commit();
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment secilenFragment = null;
            if (item.getItemId() == R.id.yoneticiNavRestoranlar) {
                secilenFragment = new RestoranlarFragment();
            } else if (item.getItemId() == R.id.yoneticiNavGarsonlar) {
                secilenFragment = new GarsonlarFragment();
            } else if (item.getItemId() == R.id.yoneticiNavKategoriler) {
                secilenFragment = new KategorilerFragment();
            } else if (item.getItemId() == R.id.yoneticiNavUrunler) {
                secilenFragment = new UrunlerFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragementTutucuYoneticiActivity, secilenFragment).commit();

            return true;
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yonetici_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.yoneticiToolbarCikis) {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), GirisActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();

        }

        return super.onOptionsItemSelected(item);
    }
}
