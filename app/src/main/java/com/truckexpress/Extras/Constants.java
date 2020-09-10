package com.truckexpress.Extras;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.truckexpress.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Constants {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public static final String DATABASE_NAME = "UserDB";

    public static void Alert(Context context,String message) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
                 dialogBuilder.setTitle("Alert");
                 dialogBuilder.setMessage(message);
                 dialogBuilder.setPositiveButton("Ok",null);
                 dialogBuilder.show();

    }
    public static void AlertAutoLink (Context context,String strMessage ,String strTitle) {
        final Dialog dialog;
        dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.clickable_link_dialoge);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        TextView title = dialog.findViewById(R.id.title);
        title.setText(strTitle);

        TextView message = dialog.findViewById(R.id.message);
        message.setText(strMessage);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    public static String longToDate(String format,Long rawDate){
        Date date=new Date(rawDate);
        SimpleDateFormat df2 = new SimpleDateFormat(format);
        return df2.format(date);
    }
    public static String Date(String time)  {
        if (time != null) {
            String df = "yyyy-MM-dd hh:mm:ss";
            String resultFormat = "dd/MM/yyyy";
            Date date = null;
            try {
                date = new SimpleDateFormat(df).parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new SimpleDateFormat("dd MMM yyyy").format(date);
        }else {
            return "unknown";
        }
    }

    public static String capitalize(String str)
    {
        try {
            if(str == null) return str;
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }



    public static void showFormValidationError(Activity activity , EditText editText ,String errorMessage){
        Alert(activity, errorMessage);
        editText.setError(errorMessage);
    }

    public static boolean validate(Activity activity,List<TextInputEditText> validationList) {
        for(TextInputEditText e : validationList) {
            if(!isValid(e)) {
                //display your message here
                showFormValidationError(activity, e, e.getTag().toString() + " Field Required");
                return false;
            }
        }
        return true;
        //Of course if you want to display message for all EditTexts validation faults just wait with returning "false".
    }

    public static boolean isValid(TextInputEditText et) {
        if (et.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }


            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
