package com.truckexpress.Activity.Payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAdavancePaymentsBinding;
import com.truckexpress.databinding.ItemAdvancePaymentBinding;

public class AdavancePayments extends AppCompatActivity {

    ActivityAdavancePaymentsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_adavance_payments);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Payments");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        binding.rvAdvancePayment.setHasFixedSize(true);
        binding.rvAdvancePayment.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdvancePayment.addItemDecoration(new MyItemDecoration());
        binding.rvAdvancePayment.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewHolder viewHolder = new ViewHolder((ItemAdvancePaymentBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_advance_payment, parent, false));
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 10;
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                public ViewHolder(ItemAdvancePaymentBinding binding) {
                    super(binding.getRoot());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}