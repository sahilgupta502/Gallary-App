package com.gallaryapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import com.gallaryapp.R;
import dmax.dialog.SpotsDialog;

public class Utils {
    private static AlertDialog spotDialog;

    public static void showDialog(Activity activity) {
        spotDialog = new SpotsDialog.Builder()
                .setContext(activity)
                .setTheme(R.style.Custom)
                .setMessage("Loading...")
                .build();
        spotDialog.show();
    }

    public static void hideDialog() {
        spotDialog.dismiss();

    }
}
