package com.truckexpress.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Fragments.Frg_Share;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;
import com.truckexpress.databinding.AddBidLotBinding;
import com.truckexpress.databinding.ItemLotBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Adapter.RV_ADHOCAdapter.displayExpense;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.AlertAutoLink;
import static com.truckexpress.Network.API.BookingReasonInsert;

public class RV_LotAdapter extends RecyclerView.Adapter<RV_LotAdapter.ViewHolder> implements Filterable {

    List<ModelLOT> modelLOTS;
    List<ModelLOT> tempList;
    Context context;
    Progress progress;
    private static final String TAG = "RV_LotAdapter";


    public RV_LotAdapter(Context context, List<ModelLOT> modelLOTS) {
        this.modelLOTS = modelLOTS;
        this.tempList = modelLOTS;
        this.context = context;
        progress = new Progress(context);
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    modelLOTS = tempList;
                } else {
                    List<ModelLOT> filteredList = new ArrayList<>();
                    for (ModelLOT row : tempList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (String.valueOf(row.getBookingid()).toLowerCase().contains(charString.toLowerCase()) || row.getCompanyName().toLowerCase().contains(charString.toLowerCase()) || row.getTrucktype().toLowerCase().contains(charString.toLowerCase()) || row.getSource().toLowerCase().contains(charString.toLowerCase()) || row.getDestination().toLowerCase().contains(charString.toLowerCase()) || row.getGoodstype().toLowerCase().contains(charString.toLowerCase())|| row.getPaymentname().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    modelLOTS = filteredList;
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
        return modelLOTS.size();
    }

    private void addBIDDialoge(final ModelLOT modelLOT) {
        final AddBidLotBinding bidBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.add_bid_lot, null, false);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(bidBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        bidBinding.closeDiloge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bidBinding.bookingID.setText("Booking ID :" + modelLOT.getId());
        bidBinding.noofTruck.setText(+modelLOT.getLotweight() + "" + modelLOT.getUnitname());
        bidBinding.trckType.setText(modelLOT.getTrucktype() + " / " + modelLOT.getTyre() + "tyre");
        bidBinding.rate.setText(String.valueOf(modelLOT.getRate()).trim());
        bidBinding.unit.setText(modelLOT.getUnitid());

        bidBinding.saveBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidBinding.rate.getText().toString().isEmpty()) {
                    bidBinding.rate.setError("Field Required...");
                    Alert(context, "Please set Rate");
                } else if (bidBinding.edtNoofTruck.getText().toString().isEmpty()) {
                    bidBinding.edtNoofTruck.setError("Field Required...");
                    Alert(context, "Please set Number of Truck");
                } else if (Integer.parseInt(modelLOT.getNooftrucks()) < Integer.parseInt(bidBinding.edtNoofTruck.getText().toString().trim())) {
                    bidBinding.edtNoofTruck.setError("Please Enter Valid data ...");
                    Alert(context, "Please Enter Valid data ...");
                } else {
                    new RV_ADHOCAdapter.SaveBID(modelLOT, dialog, progress, context).execute(bidBinding.rate.getText().toString(), bidBinding.edtNoofTruck.getText().toString());
                }
            }
        });
    }

    private void removeTruck(Dialog dialog, int position, String bookingId, String Remark) {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("bookingid", bookingId);
            jsonParams.put("reason", Remark);
            jsonParams.put("createdby", "0");
            entity = new StringEntity(jsonParams.toString());

            Log.d(TAG, "getTrucks: " + jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, BookingReasonInsert, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);
                Object json = null;
                if (result.equals("\"success\"")) {
                    dialog.dismiss();
                    Toast.makeText(context, "Booking Removed Successfully", Toast.LENGTH_SHORT).show();
                    modelLOTS.remove(position);
                    notifyDataSetChanged();
                } else {
                    Alert(context, "Some thing went wrong..");
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
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
            itemLotBinding.source.setText(modelLOT.getSource());
            itemLotBinding.destination.setText(modelLOT.getDestination());
            itemLotBinding.pickuplocation.setText(modelLOT.getPickupaddress());
            itemLotBinding.dropLocation.setText(modelLOT.getDropaddress());
            itemLotBinding.goodsType.setText(modelLOT.getGoodstype()+" "+modelLOT.getShortageallowance());
            itemLotBinding.paymentmode.setText(modelLOT.getPaymentname());
            itemLotBinding.totalfreight.setText("₹ "+modelLOT.getTotalfreight());
            itemLotBinding.expense.setText("₹ " + modelLOT.getTotalexpenses());
            itemLotBinding.checkList.setText("Checklist : " + modelLOT.getChecklistcount());
            itemLotBinding.Amount.setText(modelLOT.getRate() + " " + modelLOT.getUnitid());
            itemLotBinding.weight.setText(modelLOT.getLotweight()+ " " + modelLOT.getLotunitname());

            itemLotBinding.note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( modelLOT.getNote()==null || modelLOT.getNote().isEmpty()){
                        Alert(context,"No Data Available");
                    }else {
                        Alert(context,modelLOT.getNote());
                    }
                }
            });
            itemLotBinding.expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayExpense(context, String.valueOf(modelLOT.getId()));
                }
            });


            if (!modelLOT.getTyre().isEmpty())
                itemLotBinding.trckType.setText(modelLOT.getTrucktype() + "\n/" + modelLOT.getTyre() + " tyre");
            else
                itemLotBinding.trckType.setText("" + modelLOT.getTrucktype());


            if (modelLOT.getBidcount() == 0) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#F44336"));
                itemLotBinding.bid.setFocusable(false);
                itemLotBinding.bid.setClickable(false);

                itemLotBinding.accept.setFocusable(false);
                itemLotBinding.accept.setClickable(false);

                itemLotBinding.Amount.setFocusable(false);
                itemLotBinding.Amount.setClickable(false);

            } else if (modelLOT.getBidcount() >= 2) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#2196F3"));
            }


            if (modelLOT.getAcceptedbycorporate() == 1) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#FFFFE974"));
            }
            if (modelLOT.getBiddingtype() == 2) {
                itemLotBinding.bid.setVisibility(View.GONE);
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#E66AA2"));
            }

            if (modelLOT.getBiddingtype() == 2) {
                itemLotBinding.bid.setVisibility(View.GONE);
            }

            if (modelLOT.getBiddingtype() == 2) {
                itemLotBinding.bid.setVisibility(View.GONE);
            }

            itemLotBinding.Amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.BidHistory(progress, context).execute(String.valueOf(modelLOT.getBookingid()),String.valueOf(modelLOT.getCorporateid()));
                }
            });
            itemLotBinding.checkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.GetChecklist(new Progress(context),context).execute(String.valueOf(modelLOT.getId()));
                }
            });

            itemLotBinding.paymentmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelLOT.getPaymentmode().equals("1")){
                        String msg = "Advance : "+modelLOT.getAdvance()+ " %"+"\n"+
                                "Balance : "+modelLOT.getBalance();
                        AlertAutoLink(context,msg,"Payments Details");
                    } else if (modelLOT.getPaymentmode().equals("4")) {
                        String msg = "Advance : "+modelLOT.getAdvance()+ " %"+"\n"+
                                "Balance : "+modelLOT.getBalance()+"\n"+
                                "No Of Days : "+modelLOT.getNoofdays();
                        AlertAutoLink(context,msg,"Payments Details");
                    } else if (modelLOT.getPaymentmode().equals("3")) {
                        String msg =
                                "No Of Days : "+modelLOT.getNoofdays();
                        AlertAutoLink(context,msg,"Payments Details");
                    }
                }
            });

            itemLotBinding.corporateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String msg = "Name : "+modelLOT.getName()+"\n"+
                            "Email ID : "+modelLOT.getCorporateContactPersoneEmail()+"\n"+
                            "Mobile No : "+modelLOT.getCorporateContactPerson();

                    AlertAutoLink(context,msg,"Corporate Details");
                }
            });
            itemLotBinding.bid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBIDDialoge(modelLOT);
                }
            });
            itemLotBinding.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.BidDetails(progress,modelLOT,context,2,itemLotBinding.bookingID).execute(String.valueOf(modelLOT.getBookingid()),String.valueOf(modelLOT.getCorporateid()));
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

            itemLotBinding.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog  = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_remark);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                    TextInputEditText remark = dialog.findViewById(R.id.remark);
                    dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (remark.getText().toString().isEmpty()){
                                remark.setError("Remark required");
                                Toast.makeText(context, "Enter Remark", Toast.LENGTH_SHORT).show();
                            }else {
                                removeTruck(dialog,getAdapterPosition(), String.valueOf(modelLOT.getBookingid()),remark.getText().toString());
                            }
                        }
                    });
                    dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            });

            itemLotBinding.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putSerializable("itemBooking", modelLOT);
                    Frg_Share newFragment = new Frg_Share();
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    newFragment.setArguments(args);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
/*
                    newFragment.setOnCallbackResult(new AnswerFragment.CallbackResult() {
                        @Override
                        public void sendResult(int requestCode, Object obj) {
                            if (requestCode == 9003) {
                            }
                        }
                    });
*/
                }
            });


        }

    }

}
