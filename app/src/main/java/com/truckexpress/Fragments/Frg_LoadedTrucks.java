package com.truckexpress.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Adapter.Rv_LoadedTrucklistAdapter;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelCurrentBooking;
import com.truckexpress.Models.ModelLoadedTrcukList;
import com.truckexpress.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Network.API.LoadedTruck;

public class Frg_LoadedTrucks extends DialogFragment {
    private static final String TAG = "Frg_BookingDetails";
    Bundle bundle;
    RecyclerView rvDetails;
    List<ModelLoadedTrcukList> loadingTrucklists = new ArrayList<>();
    ModelCurrentBooking booking;
    TextView noData;
    private View root_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.frg_bookingdetails, container, false);
        bundle = getArguments();
        booking = (ModelCurrentBooking) bundle.getSerializable("itemBooking");

        ((ImageButton) root_view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rvDetails = root_view.findViewById(R.id.rvDetails);
        noData = root_view.findViewById(R.id.noData);
        rvDetails.setHasFixedSize(true);
        rvDetails.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDetails.setAdapter(new Rv_LoadedTrucklistAdapter(getActivity(), loadingTrucklists));
        getDriverList();
        return root_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    private void getDriverList() {
        final Progress progress = new Progress(getActivity());
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("transporterid", USERINFO.getId());
            jsonParams.put("bookingid", booking.getBookingid());
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getActivity(), LoadedTruck, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelLoadedTrcukList trucklist = gson.fromJson(object.toString(), ModelLoadedTrcukList.class);
                            loadingTrucklists.add(trucklist);
                        }
                        if (loadingTrucklists.size() > 0) {
                            rvDetails.getAdapter().notifyDataSetChanged();
                        } else {
                            noData.setVisibility(View.VISIBLE);
                        }
                    }

                    if (loadingTrucklists == null) {
                        noData.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                String result = new String(bytes);
                Log.d(TAG, "onSuccess: " + result);
                noData.setText("Please Check Your Connection");
                noData.setVisibility(View.VISIBLE);
                progress.dismiss();

            }

        });
    }

}
