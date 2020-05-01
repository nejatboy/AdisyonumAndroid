package com.nejatboy.adisyonum.Adapter.Garson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nejatboy.adisyonum.Model.Urun;
import com.nejatboy.adisyonum.R;

import java.util.List;

public class AdapterDialogUrunler extends BaseAdapter {

    private Context context;
    private List<Urun> urunler;


    public AdapterDialogUrunler(Context context, List<Urun> urunler) {
        this.context = context;
        this.urunler = urunler;
    }




    @Override
    public int getCount() {
        return urunler.size();
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
        Urun urun = urunler.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.hucre_dialog_urunler, parent, false);
        }

        TextView textViewUrunAd = convertView.findViewById(R.id.textViewHucreDialogUrunlerUrunAd);
        textViewUrunAd.setText(urun.getUrunAd());

        TextView textViewUrunFiyat = convertView.findViewById(R.id.textViewHucreDialogUrunlerUrunFiyat);
        textViewUrunFiyat.setText(urun.getUrunFiyat() + " TL");

        return convertView;
    }
}
