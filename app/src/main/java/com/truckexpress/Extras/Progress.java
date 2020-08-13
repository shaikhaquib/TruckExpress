package com.truckexpress.Extras;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.truckexpress.R;


public class Progress {

    Context activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public Progress(Context activity) {
        this.activity=activity;
    }

    public void show() {

            dialog  = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //...set cancelable false so that it's never get hidden
            dialog.setCancelable(false);
            //...that's the layout i told you will inflate later
             dialog.setContentView(R.layout.progress);
           // final android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void dismiss(){
        dialog.dismiss();
    }

}