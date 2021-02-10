package com.carista.ui.main.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.carista.R;
import com.carista.utils.Device;
import com.carista.utils.FirestoreData;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UploadFragment extends Fragment {

    public static final int RESULT_LOAD_IMAGE = 100;
    private static final int CAMERA_PERMISSION_REQUEST = 200;
    private Intent chooser;
    private ImageView imageView;
    private Button chooseButton, uploadButton;
    private EditText titleEditText;
    File capturedImage;

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseButton = view.findViewById(R.id.new_post_choose);
        uploadButton = view.findViewById(R.id.new_post_upload);
        titleEditText = view.findViewById(R.id.new_post_title);
        imageView = view.findViewById(R.id.new_post_image);

        chooseButton.setOnClickListener(view1 -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
                return;
            }
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = view1.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                capturedImage = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                chooser = Device.initChooser(getContext(), capturedImage);

                startActivityForResult(chooser, RESULT_LOAD_IMAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        uploadButton.setOnClickListener(view1 -> {
            if (imageView.getDrawable() == null) {
                Snackbar.make(getActivity().getCurrentFocus(), R.string.select_image, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (titleEditText.getText() == null || titleEditText.getText().toString().isEmpty()) {
                Snackbar.make(getActivity().getCurrentFocus(), R.string.insert_title, Snackbar.LENGTH_SHORT).show();
                return;
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference("posts");
            // Create a reference to "mountains.jpg"
            long id = new Date().getTime();
            String name = id + ".jpg";
            StorageReference imageRef = storageRef.child(name);

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> Snackbar.make(getActivity().getCurrentFocus(), R.string.failed_to_upload, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                            FirestoreData.uploadPost(titleEditText.getText().toString().trim(), id, uri.toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                            imageView.setImageBitmap(null);
                            titleEditText.setText("");
                            Snackbar.make(getView().findViewById(R.id.upload_layout), R.string.success_upload, Snackbar.LENGTH_SHORT).show();
                        });
                    });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Bitmap bitmap;
            if (data.getData() == null && this.capturedImage != null) {
                bitmap = BitmapFactory.decodeFile(this.capturedImage.getPath());
            } else {
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
                    Device.broadcastGallery(getContext(), capturedImage);
                } catch (Exception e) {
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            imageView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.permission_granted, Snackbar.LENGTH_SHORT).show();
                    chooseButton.callOnClick();
                } else {
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

}