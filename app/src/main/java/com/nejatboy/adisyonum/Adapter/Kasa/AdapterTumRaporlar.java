package com.nejatboy.adisyonum.Adapter.Kasa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nejatboy.adisyonum.Model.GunlukRapor;
import com.nejatboy.adisyonum.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterTumRaporlar extends BaseAdapter {

    private Context context;
    private List<GunlukRapor> raporlar;




    public AdapterTumRaporlar(Context context, List<GunlukRapor> raporlar) {
        this.context = context;
        this.raporlar = raporlar;
    }




    @Override
    public int getCount() {
        return raporlar.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.hucre_tum_raporlar, parent, false);
        }

        Date tarih = raporlar.get(position).getTarih();
        String tarihString = new SimpleDateFormat("dd.MM.yyyy - hh:mm:ss a").format(tarih);

        TextView textViewRaporTarih = convertView.findViewById(R.id.textViewHucreTumRaporlar);
        textViewRaporTarih.setText(tarihString);

        return convertView;
    }
}
