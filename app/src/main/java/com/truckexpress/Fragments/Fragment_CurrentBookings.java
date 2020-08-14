package com.truckexpress.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.truckexpress.Activity.BookingActivity;
import com.truckexpress.Activity.EnquiresActivity;
import com.truckexpress.Adapter.RV_ADHOCAdapter;
import com.truckexpress.Adapter.Rv_CurrentBookingsAdapt;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelCurrentBooking;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_CurrentBookings extends Fragment {
    private static final String TAG = "Fragment_ADHOC";
    RecyclerView rvLOT;
    List<ModelCurrentBooking> currentBookings = new ArrayList<>();
    Progress progress;
    BookingActivity mainActivity ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLOT = view.findViewById(R.id.rvLOT);
        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("BOOKINGDATA"));
        mainActivity = (BookingActivity) getActivity();
        rvLOT.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLOT.hasFixedSize();
        rvLOT.setAdapter(new Rv_CurrentBookingsAdapt(getActivity(),currentBookings));
        progress = new Progress(getActivity());
    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (mainActivity.currentBookings.size() > 0){
                currentBookings.addAll(mainActivity.currentBookings);
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