package com.truckexpress.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;
import com.truckexpress.databinding.ItemLotBinding;

import java.util.List;

import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.AlertAutoLink;

public class RV_LotAdapter extends RecyclerView.Adapter<RV_LotAdapter.ViewHolder> {

    List<ModelLOT> modelLOTS;
    Context context;



    public RV_LotAdapter(Context context, List<ModelLOT> modelLOTS) {
        this.modelLOTS = modelLOTS;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder =  new ViewHolder((ItemLotBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_lot,parent,false));
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ModelLOT modelLOT = modelLOTS.get(position);
        holder.dataBind(modelLOT);

    }


    @Override
    public int getItemCount() {
        return modelLOTS.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemLotBinding itemLotBinding;
        public ViewHolder(ItemLotBinding binding) {
            super(binding.getRoot());
            this.itemLotBinding = binding;
        }
        private void dataBind(final ModelLOT modelLOT) {
            itemLotBinding.bookingID.setText("Booking ID :" + modelLOT.getId());
            itemLotBinding.corporateName.setText(Constants.capitalize(modelLOT.getCompanyName()));
            itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate());

            if (modelLOT.getWeight().isEmpty() || modelLOT.getWeight() == null) {
                itemLotBinding.weight.setText("No Data");
            } else {
                itemLotBinding.weight.setText(modelLOT.getWeight() + " " + modelLOT.getUnitid());
            }

            itemLotBinding.Amount.setText(modelLOT.getAmmountpaid());
            itemLotBinding.trckType.setText(modelLOT.getTrucktype() + "\n/" + modelLOT.getTyre() + " tyre");
            itemLotBinding.source.setText(modelLOT.getSource());
            itemLotBinding.destination.setText(modelLOT.getDestination());
            itemLotBinding.pickuplocation.setText(modelLOT.getPickupaddress());
            itemLotBinding.dropLocation.setText(modelLOT.getDropaddress());

            itemLotBinding.goodsType.setText(modelLOT.getGoodstype());
            itemLotBinding.paymentmode.setText(modelLOT.getPaymentmode());
            itemLotBinding.totalfreight.setText(modelLOT.getTotalfreight());
            itemLotBinding.expense.setText(String.valueOf(modelLOT.getTotalexpenses()));
            itemLotBinding.checkList.setText("Checklist : " + String.valueOf(modelLOT.getChecklistcount()));

            itemLotBinding.corporateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String msg = "Name : "+modelLOT.getName()+"\n"+
                            "Email ID : "+modelLOT.getCorporateContactPersoneEmail()+"\n"+
                            "Mobile No : "+modelLOT.getCorporateContactPerson();

                    AlertAutoLink(context,msg,"Corporate Details");
                }
            });


            itemLotBinding.checkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] s = modelLOT.getChecklist().split(",");

                    if (s.length > 0)
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Checklist")
                                .setItems(s, null).show();
                }
            });

            itemLotBinding.Showmore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        itemLotBinding.viewMore.setVisibility(View.VISIBLE);
                        itemLotBinding.Showmore.setText("Show Less");
                    } else {
                        itemLotBinding.viewMore.setVisibility(View.GONE);
                        itemLotBinding.Showmore.setText("Show More");
                    }
                }
            });
        }

    }
}
