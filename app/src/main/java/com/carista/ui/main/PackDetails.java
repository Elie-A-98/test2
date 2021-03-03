package com.carista.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.graphics.drawable.BitmapDrawable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import com.carista.R;
import com.carista.utils.FirestoreData;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.material.tabs.TabLayout;

import com.google.android.material.tabs.TabLayoutMediator;

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

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PackDetails extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 100;


    private EditText packNameEdit;
    private TextView packNickname;
    private Button packnameChangeButton;
    private Button deletePackButton;

    private CircleImageView packImage;

    private Intent chooser;

    TabLayout tabLayout;
    ViewPager2 viewPager;
    int[] drawableIds = {R.drawable.ic_img_view, R.drawable.ic_add_pack};

    public String packId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        packNickname = findViewById(R.id.pack_nickname);
        packImage = findViewById(R.id.pack_image);
        packNameEdit = findViewById(R.id.packNameEdit);
        packnameChangeButton = findViewById(R.id.packNameChangeBtn);
        deletePackButton=findViewById(R.id.packDeleteBtn);

        viewPager.setAdapter(createCardAdapter());

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setIcon(drawableIds[position])).attach();


        if (getIntent().getStringExtra("packId") != null || !getIntent().getStringExtra("packId").isEmpty()) {
            packId = getIntent().getStringExtra("packId");
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("stickers").whereEqualTo("id",packId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                    String icon= String.valueOf(documentSnapshot.get("icon"));
                    String packName = String.valueOf(documentSnapshot.get("title"));

                    packNickname.setText(packName);
                    Picasso.get().load(icon).into(packImage);
                }
            }
        });
        initChooser();

        packImage.setOnClickListener(view1 -> {
            startActivityForResult(chooser, RESULT_LOAD_IMAGE);
        });
        packNickname.setOnClickListener(view1 -> {
            packNameEdit.setVisibility(View.VISIBLE);
            packnameChangeButton.setVisibility(View.VISIBLE);
        });

        packnameChangeButton.setOnClickListener(view1 -> {
            String newPackNickname = packNameEdit.getText().toString();
            newPackNickname = newPackNickname.trim();


            if (newPackNickname == null || newPackNickname.isEmpty())
                return;


            packNameEdit.setText("");
            FirestoreData.uploadPackNickname(newPackNickname, packId);
            Snackbar.make(this.getCurrentFocus(), "Pack Name changed!", Snackbar.LENGTH_SHORT).show();
            packNickname.setText(newPackNickname);
            packNameEdit.setVisibility(View.GONE);
            packnameChangeButton.setVisibility(View.GONE);
        });

        deletePackButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_pack_confirm_message);
            builder.setNeutralButton(R.string.doNotDeletePack, (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton(R.string.deleteStickerPack, (dialog, which) -> {
                FirestoreData.removePack(packId);
                finish();
            });


            builder.create().show();
        });


    }
    private PackViewPagerAdapter createCardAdapter() {
        return new PackViewPagerAdapter(this);
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


            packImage.setImageBitmap(bitmap);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("packs");
            String imageName = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            StorageReference imageRef = storageRef.child(imageName);

            packImage.setDrawingCacheEnabled(true);
            packImage.buildDrawingCache();

            Bitmap bitmapim = ((BitmapDrawable) packImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmapim.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imdata = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(imdata);
            uploadTask.addOnFailureListener(exception -> Snackbar.make(this.getCurrentFocus(), R.string.failed_to_upload_pack, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> FirestoreData.uploadPackIconLink(uri.toString(), packId));
                    });
        }
    }

}