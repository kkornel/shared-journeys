package com.put.miasi.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogUtils {
    public static void createDialog(Context context, String title, String posTitle, DialogInterface.OnClickListener posLis, String negTitle, DialogInterface.OnClickListener negLis) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton(posTitle, posLis)
                .setNegativeButton(negTitle, negLis)
                .create()
                .show();
    }
}
