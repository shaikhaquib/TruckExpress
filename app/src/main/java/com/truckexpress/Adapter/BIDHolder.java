package com.truckexpress.Adapter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.truckexpress.R;

class BIDHolder extends RecyclerView.ViewHolder {
    TextView title,desc,bidBY;
    TextView Transtitle,Transdesc,TransbidBY;
    MaterialCardView materialCardView;
    RelativeLayout CoorporatorView , tranView;
    public BIDHolder(View v) {
        super(v);
        title = v.findViewById(R.id.rate);
        desc = v.findViewById(R.id.truckCount);
        materialCardView = v.findViewById(R.id.materialCardView);
        bidBY = v.findViewById(R.id.bidBY);

        Transtitle = v.findViewById(R.id.transrate);
        Transdesc = v.findViewById(R.id.transtruckCount);
        TransbidBY = v.findViewById(R.id.transbidBY);
        CoorporatorView = v.findViewById(R.id.CoorporatorView);
        tranView = v.findViewById(R.id.tranView);
    }
}
