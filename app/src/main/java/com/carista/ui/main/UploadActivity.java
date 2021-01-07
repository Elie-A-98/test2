package com.carista.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.carista.R;
import com.carista.utils.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE=100;
    private Button ChooseButton,UploadButton;
    private EditText TitleTextField;
    private ImageView imageView;
    private Intent chooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        ChooseButton=findViewById(R.id.new_post_choose);
        UploadButton=findViewById(R.id.new_post_upload);
        TitleTextField=findViewById(R.id.new_post_title);
        imageView=findViewById(R.id.new_post_image);
        initChooser();

        ChooseButton.setOnClickListener(view -> {
            startActivityForResult(chooser,RESULT_LOAD_IMAGE);
        });

        UploadButton.setOnClickListener(view -> {
            if(imageView.getDrawable()==null){
                Snackbar.make(findViewById(R.id.upload_layout),R.string.select_image, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(TitleTextField.getText()==null || TitleTextField.getText().toString().isEmpty()){
                Snackbar.make(findViewById(R.id.upload_layout),R.string.insert_title, Snackbar.LENGTH_SHORT).show();
                return;
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("posts");
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
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Snackbar.make(findViewById(R.id.upload_layout), R.string.failed_to_upload, Snackbar.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> Data.uploadPost(TitleTextField.getText().toString(), id, uri.toString()));
                    finish();
                }
            });


        });
    }

    private void initChooser() {
        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent gallIntent = new Intent(Intent.ACTION_PICK);
        gallIntent.setType("image/*");
        chooser = Intent.createChooser(gallIntent, getResources().getString(R.string.select_image));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camIntent});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Bitmap bitmap;
            if (data.getExtras() != null && data.getExtras().get("data") instanceof Bitmap) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                try {
                    bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    Snackbar.make(getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            imageView.setImageBitmap(bitmap);
        }
    }
}