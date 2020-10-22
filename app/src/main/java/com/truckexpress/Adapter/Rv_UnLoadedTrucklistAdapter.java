package com.truckexpress.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelLoadedTrcukList;
import com.truckexpress.R;
import com.truckexpress.databinding.UnloadingViewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Network.API.saveunloading;

public class Rv_UnLoadedTrucklistAdapter extends RecyclerView.Adapter<Rv_UnLoadedTrucklistAdapter.ViewHolder> {

    private static final String TAG = "Rv_LoadingTrucklistAdap";
    List<ModelLoadedTrcukList> loadingTrucklists;
    Context context;
    Progress progress;

    public Rv_UnLoadedTrucklistAdapter(Context context, List<ModelLoadedTrcukList> modelLOTS) {
        this.loadingTrucklists = modelLOTS;
        this.context = context;
        progress = new Progress(context);
    }

    @NonNull
    @Override
    public Rv_UnLoadedTrucklistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Rv_UnLoadedTrucklistAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Rv_UnLoadedTrucklistAdapter.ViewHolder holder, int position) {
        ModelLoadedTrcukList trucklist = loadingTrucklists.get(position);
        Rv_UnLoadedTrucklistAdapter.ViewHolder viewHolder = holder;
        viewHolder.truckNumber.setText("Vehicle Number : " + trucklist.getTruckname().toUpperCase());
        viewHolder.status.setText("Status : " + trucklist.getStatusname());
        viewHolder.cancelAssignedTruck.setVisibility(View.GONE);


        viewHolder.btnloading.setText("Un Loading");

        viewHolder.btnloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(trucklist);
            }
        });
    }

    private void loading(ModelLoadedTrcukList trucklist) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        UnloadingViewBinding loadingVeiwBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.unloading_view, null, false);
        dialog.setContentView(loadingVeiwBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        loadingVeiwBinding.advance.setText(trucklist.getAmountpaid());
        loadingVeiwBinding.weightLoading.setText(trucklist.getLoadedweight());
        loadingVeiwBinding.noofBags.setText(trucklist.getNoofbags());
        loadingVeiwBinding.freight.setText("" + trucklist.getFreight());
        loadingVeiwBinding.TruckWeight.setText(trucklist.getWeight() + " " + trucklist.getUnitname());
        loadingVeiwBinding.trucknumber.setText(trucklist.getTruckname());
        loadingVeiwBinding.noofBags.setText("" + trucklist.getNoofbags());

        loadingVeiwBinding.closeDiloge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        loadingVeiwBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingVeiwBinding.weightLoading.getText().toString().isEmpty()) {
                    loadingVeiwBinding.weightLoading.setError("Field required");
                    Toast.makeText(context, "Loaded Weight Field Required", Toast.LENGTH_SHORT).show();
                } else if (loadingVeiwBinding.noofBags.getText().toString().isEmpty()) {
                    loadingVeiwBinding.noofBags.setError("Field required");
                    Toast.makeText(context, "No of Bags Field Required", Toast.LENGTH_SHORT).show();
                } else if (loadingVeiwBinding.unloadingno.getText().toString().isEmpty()) {
                    loadingVeiwBinding.noofBags.setError("Field required");
                    Toast.makeText(context, "Unloading No Required", Toast.LENGTH_SHORT).show();
                } else {
                    addLoading(trucklist, dialog, loadingVeiwBinding.weightLoading.getText().toString(), loadingVeiwBinding.noofBags.getText().toString(), loadingVeiwBinding.unloadingno.getText().toString());
                }
            }
        });


    }

    private void addLoading(ModelLoadedTrcukList trucklist, Dialog dialog, String loadedweight, String noofbags, String unloadingno) {
        final Progress progress = new Progress(context);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {


            JSONObject jsonParams = new JSONObject();
            jsonParams.put("bookingid", trucklist.getBookingid());
            jsonParams.put("transporterid", USERINFO.getId());
            jsonParams.put("trucknumber", trucklist.getTruckname());
            jsonParams.put("unloadedweight", loadedweight);
            jsonParams.put("noofbags", noofbags);
            jsonParams.put("unloadingno", unloadingno);


            entity = new StringEntity(jsonParams.toString());

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, saveunloading, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

                if (result.equals("\"success\"")) {
                    Toast.makeText(context, "Detail added Successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Alert(context, result);
                }

                Object json = null;

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                String result = new String(bytes);
                Log.d(TAG, "onSuccess: " + result);
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loadingTrucklists.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView truckNumber, status, cancelAssignedTruck;
        MaterialButton btnloading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            truckNumber = itemView.findViewById(R.id.truckNumber);
            status = itemView.findViewById(R.id.status);
            btnloading = itemView.findViewById(R.id.btnloading);
            cancelAssignedTruck = itemView.findViewById(R.id.cancelAssignedTruck);
            btnloading.setVisibility(View.GONE);
        }
    }

}
