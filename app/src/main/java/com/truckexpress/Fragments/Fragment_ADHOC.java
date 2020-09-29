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
import com.truckexpress.Adapter.RV_ADHOCAdapter;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_ADHOC extends Fragment {
    private static final String TAG = "Fragment_ADHOC";
    RecyclerView rvLOT;
    List<ModelLOT> modelLOTS = new ArrayList<>();
    Progress progress;
    EnquiresActivity mainActivity ;
    RV_ADHOCAdapter rv_adhocAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_adhoc, container, false);
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
                rv_adhocAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                rv_adhocAdapter.getFilter().filter(query);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("BOOKINGDATA"));
        mainActivity = (EnquiresActivity) getActivity();
        rvLOT = view.findViewById(R.id.rvLOT);
        rvLOT.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLOT.hasFixedSize();
        rv_adhocAdapter=new RV_ADHOCAdapter(getActivity(),modelLOTS);
        rvLOT.setAdapter(rv_adhocAdapter);
        progress = new Progress(getActivity());
    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (mainActivity.modelADHOC.size() > 0){
                modelLOTS.addAll(mainActivity.modelADHOC);
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