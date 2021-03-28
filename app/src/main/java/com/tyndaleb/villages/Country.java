package com.tyndaleb.villages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class Country extends Fragment {

    private static String search_term;
    //private String search_term;


    public Country() {
        // Required empty public constructor
    }

    public  static Country newInstance(String search_term) {
        Country fragment = new Country();
        Country.search_term = search_term ;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("STATIC",search_term);
        return inflater.inflate(R.layout.fragment_country, container, false);


    };

}
