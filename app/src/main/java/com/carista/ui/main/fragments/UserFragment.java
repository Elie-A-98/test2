package com.carista.ui.main.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.carista.R;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.utils.Data;
import com.carista.utils.FirestoreData;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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

import static com.carista.photoeditor.photoeditor.TextEditorDialogFragment.TAG;


public class UserFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 100;
    private Button usernameChangeButton;
    private TextView userNickname;
    private EditText usernameEdit;
    private CircleImageView userAvatar;
    private Intent chooser;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    int[] drawableIds = {R.drawable.ic_img_view, R.drawable.ic_heart, R.drawable.ic_settings};

    public UserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setIcon(drawableIds[position])).attach();

        return view;

    }

    private UserViewPagerAdapter createCardAdapter() {
        return new UserViewPagerAdapter(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userNickname = view.findViewById(R.id.user_nickname);
        userAvatar = view.findViewById(R.id.user_avatar);
        usernameEdit = view.findViewById(R.id.username_change_edit);
        usernameChangeButton = view.findViewById(R.id.username_change_btn);


        UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("id",FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                    String avatar= String.valueOf(documentSnapshot.get("avatar"));
                    String nickname = String.valueOf(documentSnapshot.get("nickname"));

                    userNickname.setText("Welcome, " + nickname);

                    if(!avatar.isEmpty())
                        Picasso.get().load(avatar).into(userAvatar);

                }
            }
        });

        initChooser();


        userAvatar.setOnClickListener(view1 -> {
            startActivityForResult(chooser, RESULT_LOAD_IMAGE);
        });

        userNickname.setOnClickListener(view1 -> {
            usernameEdit.setVisibility(View.VISIBLE);
            usernameChangeButton.setVisibility(View.VISIBLE);
        });

        usernameChangeButton.setOnClickListener(view1 -> {
            String newNickname = usernameEdit.getText().toString();
            newNickname = newNickname.trim();
            if (newNickname == null || newNickname.isEmpty())
                return;
            usernameEdit.setText("");
            FirestoreData.uploadNickname(newNickname);
            Snackbar.make(getActivity().getCurrentFocus(), "Username changed!", Snackbar.LENGTH_SHORT).show();
            userNickname.setText("Welcome, " + newNickname);
            usernameEdit.setVisibility(View.GONE);
            usernameChangeButton.setVisibility(View.GONE);
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
                    bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
            userAvatar.setImageBitmap(bitmap);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("avatars");
            String imageName = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            StorageReference imageRef = storageRef.child(imageName);

            userAvatar.setDrawingCacheEnabled(true);
            userAvatar.buildDrawingCache();
            Bitmap bitmapim = ((BitmapDrawable) userAvatar.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapim.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imdata = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imdata);
            uploadTask.addOnFailureListener(exception -> Snackbar.make(getActivity().getCurrentFocus(), R.string.failed_to_upload, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> FirestoreData.uploadAvatarLink(uri.toString()));
                    });
        }
    }
}