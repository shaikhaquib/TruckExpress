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

import com.truckexpress.Activity.BookingActivity;
import com.truckexpress.Adapter.Rv_BookingInProcessAdapt;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelCurrentBooking;
import com.truckexpress.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_TripIn_Process extends Fragment {
    private static final String TAG = "Fragment_ADHOC";
    RecyclerView rvLOT;
    List<ModelCurrentBooking> currentBookings = new ArrayList<>();
    Rv_BookingInProcessAdapt rv_currentBookingsAdapt;
    Progress progress;
    BookingActivity mainActivity;
    SearchView searchView;
    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (mainActivity.currentBookings.size() > 0) {
                currentBookings.addAll(mainActivity.currentBookings);
                rvLOT.getAdapter().notifyDataSetChanged();
            }

            //  updateUi();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trpin_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLOT = view.findViewById(R.id.rvLOT);
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("TripInProcess"));
        mainActivity = (BookingActivity) getActivity();
        rvLOT.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLOT.hasFixedSize();
        rv_currentBookingsAdapt = new Rv_BookingInProcessAdapt(getActivity(), currentBookings);
        rvLOT.setAdapter(rv_currentBookingsAdapt);
        progress = new Progress(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                rv_currentBookingsAdapt.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                rv_currentBookingsAdapt.getFilter().filter(query);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("TripInProcess"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mNotificationReceiver);
    }

}