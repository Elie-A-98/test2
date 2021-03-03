package com.carista.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.view.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.carista.R;
import com.carista.utils.FirestoreData;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;



import de.hdodenhof.circleimageview.CircleImageView;

public class StickerDetails extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 100;

    private TextView stickerNickname;
    private EditText stickerNameEdit;
    private Button stickerNameChangeButton;
    private Button deleteStickerButton;

    private CircleImageView stickerImage;

    private Intent chooser;

    public int stickerPosition;

    public String packId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);

        stickerNickname = findViewById(R.id.sticker_nickname);
        stickerImage = findViewById(R.id.sticker_image);
        stickerNameEdit = findViewById(R.id.stickerNameEdit);
        stickerNameChangeButton = findViewById(R.id.stickerNameChangeBtn);
        deleteStickerButton=findViewById(R.id.stickerDeleteBtn);

        if (getIntent().getStringExtra("packId") != null || !getIntent().getStringExtra("packId").isEmpty()) {
            packId = getIntent().getStringExtra("packId");
        }
        stickerPosition = getIntent().getIntExtra("stickerPosition", -1);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("stickers").whereEqualTo("id",packId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                    List<Map<String, String>> items = new ArrayList<Map<String, String>>();
                    items = (List<Map<String, String>>)documentSnapshot.get("items");
                    String image= String.valueOf(items.get(stickerPosition).get("image"));
                    String stickerName = String.valueOf(items.get(stickerPosition).get("name"));
                    stickerNickname.setText(stickerName);
                    Picasso.get().load(image).into(stickerImage);
                }
            }
        });
        initChooser();

        stickerImage.setOnClickListener(view1 -> {
            startActivityForResult(chooser, RESULT_LOAD_IMAGE);
        });

        stickerNickname.setOnClickListener(view1 -> {
            stickerNameEdit.setVisibility(View.VISIBLE);
            stickerNameChangeButton.setVisibility(View.VISIBLE);
        });

        stickerNameChangeButton.setOnClickListener(view1 -> {
            String newPackNickname = stickerNameEdit.getText().toString();
            newPackNickname = newPackNickname.trim();
            if (newPackNickname == null || newPackNickname.isEmpty())
                return;
            stickerNameEdit.setText("");
            FirestoreData.uploadStickerNickname(newPackNickname, packId, stickerPosition);
            Snackbar.make(this.getCurrentFocus(), "Sticker Name changed!", Snackbar.LENGTH_SHORT).show();
            stickerNickname.setText(newPackNickname);
            stickerNameEdit.setVisibility(View.GONE);
            stickerNameChangeButton.setVisibility(View.GONE);
        });

        deleteStickerButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_sticker_confirm_message);
            builder.setNeutralButton(R.string.doNotDeletePack, (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton(R.string.deleteStickerPack, (dialog, which) -> {
                FirestoreData.removeSticker(packId, stickerPosition);
                finish();
            });
            builder.create().show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Bitmap bitmap;
            if (data.getExtras() != null && data.getExtras().get("data") instanceof Bitmap) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                try {
                    bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    Snackbar.make(this.getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
            stickerImage.setImageBitmap(bitmap);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("packs");
            String imageName = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            StorageReference imageRef = storageRef.child(imageName);

            stickerImage.setDrawingCacheEnabled(true);
            stickerImage.buildDrawingCache();
            Bitmap bitmapim = ((BitmapDrawable) stickerImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapim.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imdata = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imdata);
            uploadTask.addOnFailureListener(exception -> Snackbar.make(this.getCurrentFocus(), R.string.failed_to_upload_pack, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> FirestoreData.uploadStickerIconLink(uri.toString(), packId, stickerPosition));
             });
        }
    }

}