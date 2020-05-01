package com.nejatboy.adisyonum.Adapter.Yonetici;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nejatboy.adisyonum.Model.Kategori;
import com.nejatboy.adisyonum.R;

import java.util.List;

public class SpinnerAdapterKategoriler extends ArrayAdapter<Kategori> {

    public SpinnerAdapterKategoriler(Context context, List<Kategori> kategoriler) {
        super(context, 0, kategoriler);
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
        Kategori kategori = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_satir_tasarimi, parent, false);
        }


        if (kategori != null) {
            TextView textView = convertView.findViewById(R.id.textViewSpinnerTasarim);
            textView.setText(kategori.getKategoriAd());
        }

        return convertView;
    }
}
