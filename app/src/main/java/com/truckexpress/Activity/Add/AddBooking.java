package com.truckexpress.Activity.Add;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.truckexpress.Adapter.ViewPagerAdapter;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Fragments.Createbooking.Frg_ADHOCBooking;
import com.truckexpress.Fragments.Createbooking.Frg_LOTBooking;
import com.truckexpress.R;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class AddBooking extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    Frg_ADHOCBooking fragment_adHoc = new Frg_ADHOCBooking();
    Frg_LOTBooking fragment_lot = new Frg_LOTBooking();
    ActionBar actionBar;
    private static final String TAG = "CreateBooking";
    Progress progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Create Booking");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager);
        progress = new Progress(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(fragment_adHoc, "ADHOC");
        adapter.addFragment(fragment_lot, "LOT");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}