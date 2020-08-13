package com.truckexpress.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.truckexpress.Activity.EnquiresActivity;
import com.truckexpress.Activity.MainActivity;
import com.truckexpress.Adapter.RV_LotAdapter;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.BOOKINGLIST;
import static com.truckexpress.Network.API.LOGIN;

public class Fragment_LOT extends Fragment {
    private static final String TAG = "Fragment_LOT";
    RecyclerView rvLOT;
    List<ModelLOT> modelADHOC = new ArrayList<>();
    Progress progress;
    EnquiresActivity mainActivity ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLOT = view.findViewById(R.id.rvLOT);
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("BOOKINGDATA"));
        mainActivity = (EnquiresActivity) getActivity();
        rvLOT.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLOT.hasFixedSize();
        rvLOT.setAdapter(new RV_LotAdapter(getActivity(),modelADHOC));
        progress = new Progress(getActivity());

    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");

            if (mainActivity.modelLOTS.size() > 0){
                modelADHOC.addAll(mainActivity.modelADHOC);
                rvLOT.getAdapter().notifyDataSetChanged();
            }

            //  updateUi();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("BOOKINGDATA"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mNotificationReceiver);
    }

}
