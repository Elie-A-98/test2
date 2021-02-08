package com.carista.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.carista.R;

import java.io.File;

public class Device {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService
                        (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static String getAppPicturesPath(Context context) {
        String directory;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            directory = context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)[0].getPath();
        } else {
            directory = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        }

        File direct = new File(directory);
        if (!direct.exists()) {
            direct.mkdirs();
        }

        return directory;
    }

    public static Intent initChooser(Context context) {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, "/sdcard/");
        Intent gallIntent = new Intent(Intent.ACTION_PICK);
        gallIntent.setType("image/*");

        Intent chooser = Intent.createChooser(gallIntent, context.getResources().getString(R.string.select_image));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camIntent});
        return chooser;
    }
}
