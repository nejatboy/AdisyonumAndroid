package com.nejatboy.adisyonum.Adapter.Yonetici;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nejatboy.adisyonum.Model.Restoran;
import com.nejatboy.adisyonum.R;

import java.util.List;

public class SpinnerAdapterRestoranlar extends ArrayAdapter<Restoran> {


    public SpinnerAdapterRestoranlar(Context context, List<Restoran> restoranlar) {
        super(context, 0, restoranlar);
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
        Restoran restoran = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_satir_tasarimi, parent, false);
        }


        if (restoran != null) {
            TextView textView = convertView.findViewById(R.id.textViewSpinnerTasarim);
            textView.setText(restoran.getRestoranAd());
        }

        return convertView;
    }
}
