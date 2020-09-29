package com.truckexpress.Fragments;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.truckexpress.Activity.EnquiresActivity;
import com.truckexpress.Adapter.RV_LotAdapter;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_LOT extends Fragment {
    private static final String TAG = "Fragment_LOT";
    RecyclerView rvLOT;
    List<ModelLOT> modelADHOC = new ArrayList<>();
    Progress progress;
    EnquiresActivity mainActivity ;
    RV_LotAdapter rv_lotAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_lot, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                rv_lotAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                rv_lotAdapter.getFilter().filter(query);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLOT = view.findViewById(R.id.rvLOT);
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("BOOKINGDATA"));
        mainActivity = (EnquiresActivity) getActivity();
        rvLOT.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLOT.hasFixedSize();
        rv_lotAdapter = new RV_LotAdapter(getActivity(),modelADHOC);
        rvLOT.setAdapter(rv_lotAdapter);
        progress = new Progress(getActivity());

    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");

            if (mainActivity.modelLOTS.size() > 0){
                modelADHOC.addAll(mainActivity.modelLOTS);
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
