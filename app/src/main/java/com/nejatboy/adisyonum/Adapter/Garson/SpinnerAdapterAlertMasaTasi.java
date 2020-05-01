package com.nejatboy.adisyonum.Adapter.Garson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.R;

import java.util.List;

public class SpinnerAdapterAlertMasaTasi extends ArrayAdapter<Masa> {

    public SpinnerAdapterAlertMasaTasi(Context context, List<Masa> bosMasalar) {
        super(context, 0, bosMasalar);
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }





    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }



    private View initView (int position, View convertView, ViewGroup parent) {      //Hazır metod değil ben yazdım (override metodlara return edecek)
        Masa bosMasa = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_satir_tasarimi, parent, false);
        }


        if (bosMasa != null) {
            TextView textView = convertView.findViewById(R.id.textViewSpinnerTasarim);
            textView.setText("" + bosMasa.getMasaNo());
        }

        return convertView;
    }
}
