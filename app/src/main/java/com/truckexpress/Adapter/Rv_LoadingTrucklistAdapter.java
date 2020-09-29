package com.truckexpress.Adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelLoadingTrucklist;
import com.truckexpress.R;
import com.truckexpress.databinding.LoadingVeiwBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Network.API.addArrival;
import static com.truckexpress.Network.API.apploading;

public class Rv_LoadingTrucklistAdapter extends RecyclerView.Adapter<Rv_LoadingTrucklistAdapter.ViewHolder> {

    private static final String TAG = "Rv_LoadingTrucklistAdap";
    List<ModelLoadingTrucklist> loadingTrucklists;
    Context context;
    Progress progress;

    public Rv_LoadingTrucklistAdapter(Context context, List<ModelLoadingTrucklist> modelLOTS) {
        this.loadingTrucklists = modelLOTS;
        this.context = context;
        progress = new Progress(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelLoadingTrucklist trucklist = loadingTrucklists.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.truckNumber.setText("Vehicle Number : " + trucklist.getTruckname().toUpperCase());
        viewHolder.status.setText("Status : " + trucklist.getStatusname());
        if (trucklist.getPageloadingStatus() == 0) {
            viewHolder.btnloading.setText("Arrival");
        } else {
            viewHolder.btnloading.setText("Loading");
        }

        viewHolder.btnloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trucklist.getPageloadingStatus() == 0) {
                    displayArrival(trucklist);
                } else {
                    loading(trucklist);
                }
            }
        });
    }

    private void loading(ModelLoadingTrucklist trucklist) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LoadingVeiwBinding loadingVeiwBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.loading_veiw, null, false);
        dialog.setContentView(loadingVeiwBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        loadingVeiwBinding.advance.setText(trucklist.getAmountpaid());
        loadingVeiwBinding.arrivalDate.setText(trucklist.getTruckavailabilitydate());
        loadingVeiwBinding.arrivalTime.setText(trucklist.getTruckacailabilitytime());
        loadingVeiwBinding.freight.setText(trucklist.getFreight());
        loadingVeiwBinding.loadingNo.setText("" + trucklist.getLoadingno());
        loadingVeiwBinding.TruckWeight.setText(trucklist.getWeight());
        loadingVeiwBinding.trucknumber.setText(trucklist.getTruckname());
        loadingVeiwBinding.departureDate.setText(trucklist.getAmountpaid());
        loadingVeiwBinding.departureTime.setText(trucklist.getAmountpaid());
        loadingVeiwBinding.unit.setText(trucklist.getUnitname());
        loadingVeiwBinding.noofBags.setText(trucklist.getNoofbags());
        String[] strTodaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()).split(" ");
        loadingVeiwBinding.departureDate.setText(strTodaysDate[0]);
        loadingVeiwBinding.departureTime.setText(strTodaysDate[1]);
        loadingVeiwBinding.closeDiloge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        loadingVeiwBinding.departureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                loadingVeiwBinding.departureDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();

            }
        });
        loadingVeiwBinding.departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePicker(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR);
                int minutes = cldr.get(Calendar.MINUTE);
                TimePickerDialog picker = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                loadingVeiwBinding.departureTime.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
                picker.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
                } else {
                    addLoading(trucklist, dialog, loadingVeiwBinding.weightLoading.getText().toString(), loadingVeiwBinding.noofBags.getText().toString(), loadingVeiwBinding.departureDate.getText().toString(), loadingVeiwBinding.departureTime.getText().toString());
                }
            }
        });


    }

    private void displayArrival(ModelLoadingTrucklist trucklist) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_truckarrival);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();


        TextInputEditText date = dialog.findViewById(R.id.date);
        TextInputEditText time = dialog.findViewById(R.id.time);
        TextInputEditText lrno = dialog.findViewById(R.id.lrNo);

        Button btnDate = dialog.findViewById(R.id.btnDate);
        Button btnTime = dialog.findViewById(R.id.btnTime);

        String[] strTodaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()).split(" ");
        date.setText(strTodaysDate[0]);
        time.setText(strTodaysDate[1]);
        dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePicker(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR);
                int minutes = cldr.get(Calendar.MINUTE);
                TimePickerDialog picker = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                time.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
                picker.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            }
        });


        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lrno.getText().toString().isEmpty()) {
                    lrno.setError("Field required");
                    Toast.makeText(context, "LRNO Field Required", Toast.LENGTH_SHORT).show();
                } else {
                    addArrival(trucklist, dialog, date.getText().toString(), time.getText().toString(), lrno.getText().toString());
                }
            }
        });


    }

    private void addArrival(ModelLoadingTrucklist trucklist, Dialog dialog, String date, String time, String lrno) {
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
            jsonParams.put("loadingno", trucklist.getLoadingno());
            jsonParams.put("arrivaldate", date);
            jsonParams.put("arrivaltime", time);
            jsonParams.put("lrno", lrno);

            entity = new StringEntity(jsonParams.toString());

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, addArrival, entity, "application/json", new AsyncHttpResponseHandler() {
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

    private void addLoading(ModelLoadingTrucklist trucklist, Dialog dialog, String loadedweight, String noofbags, String date, String time) {
        final Progress progress = new Progress(context);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {


            JSONObject jsonParams = new JSONObject();
            jsonParams.put("bookingid", trucklist.getBookingid());
            jsonParams.put("transporterid", USERINFO.getId());
            jsonParams.put("loadedweight", loadedweight);
            jsonParams.put("trucknumber", trucklist.getTruckname());
            jsonParams.put("noofbags", noofbags);
            jsonParams.put("departuretime", time);
            jsonParams.put("departuredate", date);

            entity = new StringEntity(jsonParams.toString());

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, apploading, entity, "application/json", new AsyncHttpResponseHandler() {
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
        TextView truckNumber, status;
        MaterialButton btnloading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            truckNumber = itemView.findViewById(R.id.truckNumber);
            status = itemView.findViewById(R.id.status);
            btnloading = itemView.findViewById(R.id.btnloading);
        }
    }

}
