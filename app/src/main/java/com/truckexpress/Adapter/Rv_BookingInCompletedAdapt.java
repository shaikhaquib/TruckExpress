package com.truckexpress.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Fragments.Frg_UnLoadedTrucks;
import com.truckexpress.Models.ModelCurrentBooking;
import com.truckexpress.R;
import com.truckexpress.databinding.ItemCurrentBookingsBinding;

import java.util.ArrayList;
import java.util.List;

import static com.truckexpress.Adapter.RV_ADHOCAdapter.displayExpense;
import static com.truckexpress.Adapter.Rv_CurrentBookingsAdapt.sumWeight;
import static com.truckexpress.Adapter.Rv_CurrentBookingsAdapt.truckCount;
import static com.truckexpress.Extras.Constants.AlertAutoLink;

public class Rv_BookingInCompletedAdapt extends RecyclerView.Adapter<Rv_BookingInCompletedAdapt.ViewHolder> implements Filterable {

    private static final String TAG = "Rv_CurrentBookingsAdapt";
    List<ModelCurrentBooking> currentBookings;
    Context context;
    Progress progress;
    List<Rv_BookingInCompletedAdapt.TruckListJsonModel> truckListJsonModels = new ArrayList<>();
    List<ModelCurrentBooking> tempList;


    public Rv_BookingInCompletedAdapt(Context context, List<ModelCurrentBooking> currentBookings) {
        this.currentBookings = currentBookings;
        this.tempList = this.currentBookings;
        this.context = context;
        progress = new Progress(context);
    }

    @NonNull
    @Override
    public Rv_BookingInCompletedAdapt.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Rv_BookingInCompletedAdapt.ViewHolder viewHolder = new Rv_BookingInCompletedAdapt.ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_current_bookings, parent, false));
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Rv_BookingInCompletedAdapt.ViewHolder holder, int position) {
        final ModelCurrentBooking modelLOT = currentBookings.get(position);
        holder.bindDATA(modelLOT);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    currentBookings = tempList;
                } else {
                    List<ModelCurrentBooking> filteredList = new ArrayList<>();
                    for (ModelCurrentBooking row : tempList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (String.valueOf(row.getBookingid()).toLowerCase().contains(charString.toLowerCase()) || row.getCompanyName().toLowerCase().contains(charString.toLowerCase()) || row.getTruckname().toLowerCase().contains(charString.toLowerCase()) || row.getSource().toLowerCase().contains(charString.toLowerCase()) || row.getDestination().toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getGoodstype()).toLowerCase().contains(charString.toLowerCase()) || row.getPaymentname().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                            currentBookings = filteredList;
                        }
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterResults;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //  filterLists = (ArrayList<AssignConsumerItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    @Override
    public int getItemCount() {
        return currentBookings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemCurrentBookingsBinding itemLotBinding;

        public ViewHolder(ItemCurrentBookingsBinding binding) {
            super(binding.getRoot());
            this.itemLotBinding = binding;
        }

        private void bindDATA(final ModelCurrentBooking modelLOT) {
            itemLotBinding.bookingID.setText("Booking ID :" + modelLOT.getBookingid());
            itemLotBinding.corporateName.setText(Constants.capitalize(modelLOT.getCompanyName()));
            itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate());

            if (modelLOT.getWeight().isEmpty() || modelLOT.getWeight() == null) {
                itemLotBinding.weight.setText("No Data");
            } else {
                itemLotBinding.weight.setText(modelLOT.getWeight() + "Ton");
            }

            itemLotBinding.Amount.setText(modelLOT.getRate() + " " + modelLOT.getUnitname());
            itemLotBinding.trckType.setText(modelLOT.getTrucktypename() + "\n/" + modelLOT.getNooftyres() + " tyre");
            itemLotBinding.source.setText(modelLOT.getSource());
            itemLotBinding.destination.setText(modelLOT.getDestination());
            itemLotBinding.pickuplocation.setText(modelLOT.getPickupaddress());
            itemLotBinding.dropLocation.setText(modelLOT.getDropaddress());

            itemLotBinding.expenseTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayExpense(context, String.valueOf(modelLOT.getBookingid()));
                }
            });
            itemLotBinding.Amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.BidHistory(progress, context).execute(String.valueOf(modelLOT.getBookingid()), String.valueOf(modelLOT.getCorporateid()));
                }
            });
            itemLotBinding.checkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.GetChecklist(new Progress(context), context).execute(String.valueOf(modelLOT.getBookingid()));
                }
            });

            itemLotBinding.paymentmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelLOT.getPaymentmode() == 1) {
                        String msg = "Advance : " + modelLOT.getAdvance() + " %" + "\n" +
                                "Balance : " + modelLOT.getBalance();
                        AlertAutoLink(context, msg, "Payments Details");
                    } else if (modelLOT.getPaymentmode() == 4) {
                        String msg = "Advance : " + modelLOT.getAdvance() + " %" + "\n" +
                                "Balance : " + modelLOT.getBalance() + "\n" +
                                "No Of Days : " + modelLOT.getNoofdays();
                        AlertAutoLink(context, msg, "Payments Details");
                    } else if (modelLOT.getPaymentmode() == 3) {
                        String msg =
                                "No Of Days : " + modelLOT.getNoofdays();
                        AlertAutoLink(context, msg, "Payments Details");
                    }
                }
            });


            itemLotBinding.goodsType.setText(modelLOT.getGoodsname());
            itemLotBinding.paymentmode.setText(modelLOT.getPaymentname());
            itemLotBinding.totalfreight.setVisibility(View.GONE);
            itemLotBinding.expense.setText(String.valueOf(modelLOT.getTotalfreight()));
            itemLotBinding.noofTruck.setText("Number : " + modelLOT.getNooftrucks());
            itemLotBinding.checkList.setText("Checklist : " + modelLOT.getChecklistname());
            itemLotBinding.expenseTotal.setText("Expense  : " + modelLOT.getTotalexpenses());
            itemLotBinding.Assign.setVisibility(View.GONE);
            itemLotBinding.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Frg_UnLoadedTrucks newFragment = new Frg_UnLoadedTrucks();
                    Bundle args = new Bundle();
                    args.putSerializable("itemBooking", modelLOT);
                    newFragment.setArguments(args);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();

                }
            });


            itemLotBinding.corporateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = "Name : " + modelLOT.getName() + "\n" + "Email : " + modelLOT.getCorporateContactEmail() + "\n" +
                            "Mobile No : " + modelLOT.getCorporateContactPerson();
                    AlertAutoLink(context, msg, "Corporate Details");
                }
            });
            if (modelLOT.getBookingtype().equals("1")) {
                itemLotBinding.weight.setVisibility(View.GONE);
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#009688"));
                itemLotBinding.noofTruck.setText("Weight : " + modelLOT.getNooftrucks());
                itemLotBinding.noofTruck.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_weighing_machine, 0, 0, 0);
                itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate() + " " + modelLOT.getTodate());

            } else {
                itemLotBinding.noofTruck.setText("Number : " + modelLOT.getNooftrucks());
                itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate());
            }

            itemLotBinding.noofTruck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelLOT.getBookingtype().equals("1")) {
                        sumWeight(context, progress, modelLOT);
                    } else {
                        truckCount(context, modelLOT.getBookingid(), modelLOT, new Progress(context), 1, null);
                    }
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

    class TruckListJsonModel {


        int TruckId;
        int BookingID;
        int TransPorterID;
        String trucknumber;
        int truckweight;
        String truckavailibilty;

        public TruckListJsonModel(int truckId, int bookingID, int transPorterID, String trucknumber, String truckweight, String truckavailibilty) {
            TruckId = truckId;
            BookingID = bookingID;
            TransPorterID = transPorterID;
            this.trucknumber = trucknumber;
            this.truckweight = Integer.parseInt(truckweight.trim());
            this.truckavailibilty = truckavailibilty;
        }

        public String getTrucknumber() {
            return trucknumber;
        }

        public int getTruckweight() {
            return truckweight;
        }

        public String getTruckavailibilty() {
            return truckavailibilty;
        }


        public int getTruckId() {
            return TruckId;
        }

        public int getBookingID() {
            return BookingID;
        }

        public int getTransPorterID() {
            return TransPorterID;
        }


    }


}
