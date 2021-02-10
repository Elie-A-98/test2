package com.carista.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

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
}