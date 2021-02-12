package com.carista.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.carista.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Device {
    public static final int CAMERA_PERMISSION_REQUEST = 200;
    public static final String IS_FIRST_RUN = "IS_FIRST_RUN";

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

    public static boolean checkCameraPermission(Activity activity, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA},
                    requestCode);
            return false;
        }

        return true;
    }

    public static File createCapturedImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public static Intent initChooser(Context context, File image) {


        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri photoURI = FileProvider.getUriForFile(context,
                "com.burhanrashid52.photoeditor.fileprovider",
                image);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        Intent gallIntent = new Intent(Intent.ACTION_PICK);
        gallIntent.setType("image/*");

        Intent chooser = Intent.createChooser(gallIntent, context.getResources().getString(R.string.select_image));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camIntent});

        return chooser;

    }

    public static void broadcastGallery(Context context, File capturedImage) {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(capturedImage);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFirstRun(Context context) {
        return context.getSharedPreferences(IS_FIRST_RUN, Context.MODE_PRIVATE).getBoolean(IS_FIRST_RUN, true);
    }

    public static void setFirstRun(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_FIRST_RUN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_RUN, false);
        editor.apply();
    }
}
