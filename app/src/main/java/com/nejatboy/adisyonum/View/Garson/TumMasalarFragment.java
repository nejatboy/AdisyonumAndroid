package com.nejatboy.adisyonum.View.Garson;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nejatboy.adisyonum.Adapter.Garson.AdapterTumMasalar;
import com.nejatboy.adisyonum.Model.Masa;
import com.nejatboy.adisyonum.Model.SingletonGarson;
import com.nejatboy.adisyonum.Model.SingletonRestoran;
import com.nejatboy.adisyonum.R;

import java.util.ArrayList;
import java.util.List;

public class TumMasalarFragment extends Fragment {

    private SingletonGarson singletonGarson;
    private RecyclerView recyclerView;
    private AdapterTumMasalar adapter;
    private List<Masa> tumMasalar = new ArrayList<>();
    private SingletonRestoran singletonRestoran;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tum_masalar, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTumMasalarFragment);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        singletonGarson = SingletonGarson.getInstance();
        singletonRestoran = SingletonRestoran.getInstance();

        for(int i=1; i<=singletonRestoran.getMasaSayisi(); i++) {
            tumMasalar.add(new Masa("", i, singletonRestoran.getRestoranId(), 0.0, "", false, false, null));
        }

        adapter = new AdapterTumMasalar(getContext(), tumMasalar);
        recyclerView.setAdapter(adapter);


        return view;
    }
}
