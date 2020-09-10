package com.truckexpress.Fragments.Createbooking;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Activity.AddCheckList;
import com.truckexpress.Activity.AddCompany;
import com.truckexpress.Activity.AddExpense;
import com.truckexpress.Activity.AddTruckDetails;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelChecklist;
import com.truckexpress.Models.ModelDestination;
import com.truckexpress.Models.ModelDriver;
import com.truckexpress.Models.ModelGoodType;
import com.truckexpress.Models.ModelSource;
import com.truckexpress.Models.ModelTruckType;
import com.truckexpress.Models.ModelTyre;
import com.truckexpress.Models.ModelUnit;
import com.truckexpress.Models.ModelWeight;
import com.truckexpress.R;
import com.truckexpress.databinding.FrgAdhocbookingBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Network.API.Checklistshow;
import static com.truckexpress.Network.API.DestinationList;
import static com.truckexpress.Network.API.GoodsTypeList;
import static com.truckexpress.Network.API.SourceList;
import static com.truckexpress.Network.API.TruckTypeList;
import static com.truckexpress.Network.API.TyresList;
import static com.truckexpress.Network.API.UnitsList;
import static com.truckexpress.Network.API.WeightList;

public class Frg_ADHOCBooking extends Fragment {
    Progress progress;
    private static final String TAG = "Frg_ADHOCBooking";
    List<ModelSource> modelSources = new ArrayList<>();
    List<ModelDestination> modelDestinations = new ArrayList<>();
    List<ModelGoodType> modelGoodTypes = new ArrayList<>();
    List<ModelTruckType> truckTypeList = new ArrayList<>();
    List<ModelTyre> modelTyres= new ArrayList<>();
    List<ModelWeight> weightList= new ArrayList<>();
    List<ModelUnit> modelUnits = new ArrayList<>();
    List<ModelChecklist> modelChecklist = new ArrayList<>();

    FrgAdhocbookingBinding binding;
    private int lastCheckedPosition = -1;

    List<String> stringBuilder = new ArrayList<>();
    List<String> checklistID = new ArrayList<>();
    String stringIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.frg_adhocbooking, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progress = new Progress(getActivity());


        binding.rvTruck.setHasFixedSize(true);
        binding.rvTruck.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvTruck.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_truck, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder) holder;
                Glide.with(getActivity()).load(truckTypeList.get(position).getPhoto()).placeholder(R.drawable.ic_truck).into(viewHolder.photo);
                viewHolder.name.setText(truckTypeList.get(position).getTrucktype());
                if (position == lastCheckedPosition){
                    viewHolder.materialCardView.setCardBackgroundColor(Color.parseColor("#2F0091EA"));
                }else {
                    viewHolder.materialCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                viewHolder.materialCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastCheckedPosition = position;
                        notifyDataSetChanged();
                        getTyres(   truckTypeList.get(position).getId());
                        binding.TruckTyre.clearListSelection();
                        modelTyres.clear();
                        binding.TruckTyre.setText("");
                        binding.selectWeight.clearListSelection();
                        weightList.clear();
                        binding.TruckTyre.setText("");
                    }
                });

            }

            @Override
            public int getItemCount() {
                return truckTypeList.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                TextView name;
                ImageView photo;
                MaterialCardView materialCardView;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    name = itemView.findViewById(R.id.txtTruckName);
                    photo = itemView.findViewById(R.id.imgLogo);
                    materialCardView = itemView.findViewById(R.id.materialCardView);
                }
            }
        });

        binding.TruckTyre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getWeight(modelTyres.get(position).getId());
                binding.selectWeight.clearListSelection();
                weightList.clear();
                binding.selectWeight.setText("");
            }
        });
        binding.DOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(getActivity())
                        .bottomSheet()
                        .curved()
                        .displayMinutes(false)
                        .displayHours(false)
                        .displayDays(false)
                        .displayMonth(true)
                        .displayYears(true)
                        .title("Select Date Of Joining")
                        .displayDaysOfMonth(true)
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                binding.DOP.setText(new SimpleDateFormat("dd MMM yyyy").format(date));
                            }
                        })
                        .display();

            }
        });

        binding.checkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MultiSelectModel> driversName = new ArrayList<>();
                for (int i = 0; i < modelChecklist.size(); i++) {
                    ModelChecklist checklist = modelChecklist.get(i);
                    driversName.add(new MultiSelectModel(checklist.getId(),checklist.getCheklist()));
                }

                new MultiSelectDialog()
                        .title("Select Driver")
                        .positiveText("Done")
                        .multiSelectList(driversName)
                        .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                            @Override
                            public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String s) {
                                for (int i = 0; i < selectedIds.size(); i++) {
                                    stringBuilder.add(selectedNames.get(i));
                                    checklistID.add(String.valueOf(selectedIds.get(i)));
                                }
                                binding.checkList.setText(TextUtils.join(",",stringBuilder.toArray()));
                                stringIds = TextUtils.join(",",checklistID.toArray());
                                stringBuilder.clear();
                                checklistID.clear();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show(getActivity().getSupportFragmentManager(),TAG);
            }
        });

        getTuckType();
        getSource();
        getDestination();
        getGoodsType();
        getUnits();
        getCheckList();
    }

    private void getSource() {
        if (!progress.isShowing()) {
            progress.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getActivity(), SourceList, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
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
                            ModelSource modelSource = gson.fromJson(object.toString(), ModelSource.class);
                            modelSources.add(modelSource);
                            binding.rvTruck.getAdapter().notifyDataSetChanged();
                        }
                    }
                    List<String> states = new ArrayList<>();
                    for (int j = 0; j < modelSources.size(); j++) {
                        ModelSource modelSource = modelSources.get(j);
                        states.add(modelSource.getSorcename());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    binding.sourceDrop.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    private void getDestination() {
        if (!progress.isShowing()) {
            progress.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getActivity(), DestinationList, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
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
                            ModelDestination destination = gson.fromJson(object.toString(), ModelDestination.class);
                            modelDestinations.add(destination);
                        }
                    }
                    List<String> states = new ArrayList<>();
                    for (int j = 0; j < modelDestinations.size(); j++) {
                        ModelDestination destination = modelDestinations.get(j);
                        states.add(destination.getDestination());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    binding.destinationDrop.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    private void getGoodsType() {
        if (!progress.isShowing()) {
            progress.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("dash", "dash");
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(getContext(), GoodsTypeList, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
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
                            ModelGoodType modelGoodType = gson.fromJson(object.toString(), ModelGoodType.class);
                            modelGoodTypes.add(modelGoodType);
                        }
                    }
                    List<String> goods = new ArrayList<>();
                    for (int j = 0; j < modelGoodTypes.size(); j++) {
                        ModelGoodType destination = modelGoodTypes.get(j);
                        goods.add(destination.getGoodsname());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            R.layout.textview,
                            goods.toArray(new String[goods.size()]));
                    binding.GoodsTypeDrop.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    private void getTuckType() {
        {
            if (!progress.isShowing()) {
                progress.show();
            }
            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(20 * 1000);
            client.post(TruckTypeList, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
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
                                ModelTruckType modelState = gson.fromJson(object.toString(), ModelTruckType.class);
                                truckTypeList.add(modelState);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("xml", "Sending failed");
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.i("xml", "Progress : " + bytesWritten);
                }
            });
        }
    }
    private void getTyres(int id) {
        {
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();

            StringEntity entity = null;
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("truckid", id);
                entity = new StringEntity(jsonParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(getActivity(),TyresList,entity,"application/json" ,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    progress.dismiss();
                    String result = new String(responseBody);
                    Log.d(TAG, "onSuccess: "+result);

                    Object json = null;
                    try {
                        json = new JSONTokener(result).nextValue();

                        if (json instanceof JSONArray) {

                            JSONArray jsonArray = new JSONArray(result);
                            modelTyres.clear();

                            for (int y = 0; y < jsonArray.length(); y++) {
                                JSONObject object = jsonArray.getJSONObject(y);
                                Gson gson = new Gson();
                                ModelTyre tyre = gson.fromJson(object.toString(), ModelTyre.class);
                                modelTyres.add(tyre);
                            }
                        }
                        List<String> states =new ArrayList<>();
                        for (int j = 0; j < modelTyres.size(); j++) {
                            ModelTyre tyre = modelTyres.get(j);
                            states.add(tyre.getNooftyres());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( getActivity(),
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        binding.TruckTyre.setAdapter(adapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("xml","Sending failed");
                    progress.dismiss();
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.i("xml","Progress : "+bytesWritten);
                }
            });
        }
    }
    private void getWeight(int id) {
        {
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(20 * 1000);

            StringEntity entity = null;
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("tyresid", id);
                entity = new StringEntity(jsonParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(getActivity(),WeightList,entity,"application/json" ,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    progress.dismiss();
                    String result = new String(responseBody);
                    Log.d(TAG, "onSuccess: "+result);

                    Object json = null;
                    try {
                        json = new JSONTokener(result).nextValue();

                        if (json instanceof JSONArray) {

                            JSONArray jsonArray = new JSONArray(result);
                            weightList.clear();

                            for (int y = 0; y < jsonArray.length(); y++) {
                                JSONObject object = jsonArray.getJSONObject(y);
                                Gson gson = new Gson();
                                ModelWeight tyre = gson.fromJson(object.toString(), ModelWeight.class);
                                weightList.add(tyre);
                            }
                        }
                        List<String> states =new ArrayList<>();
                        for (int j = 0; j < weightList.size(); j++) {
                            ModelWeight tyre = weightList.get(j);
                            states.add(tyre.getWeight()+" Ton");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( getActivity(),
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        binding.selectWeight.setAdapter(adapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("xml","Sending failed");
                    progress.dismiss();
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.i("xml","Progress : "+bytesWritten);
                }
            });
        }
    }
    private void getUnits() {
        Progress progress = new Progress(getActivity());
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        client.post(UnitsList, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);

                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelUnit modelState = gson.fromJson(object.toString(), ModelUnit.class);
                            modelUnits.add(modelState);
                        }
                    }
                    List<String> states =new ArrayList<>();
                    for (int j = 0; j < modelUnits.size(); j++) {
                        ModelUnit modelState = modelUnits.get(j);
                        states.add(modelState.getUnit());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( getActivity(),
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    binding.dropUnit.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml","Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml","Progress : "+bytesWritten);
            }
        });
    }
    private void getCheckList() {
        Progress progress = new Progress(getActivity());
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(getActivity(),Checklistshow,entity,"application/json" ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);

                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(result);
                        modelChecklist.clear();

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelChecklist modelState = gson.fromJson(object.toString(), ModelChecklist.class);
                            modelChecklist.add(modelState);
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml","Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml","Progress : "+bytesWritten);
            }
        });
    }



}
