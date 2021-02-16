package com.carista.utils;

import android.text.Html;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carista.R;
import com.carista.data.StickerItem;
import com.carista.data.StickerPack;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.photoeditor.OnStickersPackLoad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirestoreData {

    public static void uploadPost(String title, long id, String imageURL, String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> post = new HashMap<>();

        post.put("id", String.valueOf(id));
        post.put("title", title);
        post.put("image", imageURL);
        post.put("userId", userId);
        post.put("likes", new ArrayList<String>());
        post.put("comments", new ArrayList<HashMap<String, Object>>());
        post.put("timestamp", new java.util.Date().getTime());

        firestore.collection("posts").add(post);
    }

    public static void uploadAvatarLink(String avatarURL) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("avatar", avatarURL);
                    firestore.collection("users").document(documentSnapshot.getId()).update(updates);
                    break;
                }
            }
        });
    }

    public static void uploadNickname(String nickname) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("nickname", nickname);
                    firestore.collection("users").document(documentSnapshot.getId()).update(updates);
                    break;
                }
            }
        });
    }

    public static void addLike(String postId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("likes");
                    likes.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("likes", likes);
                    firestore.collection("posts").document(documentSnapshot.getId()).update(updates);
                    break;
                }
            }
        });
    }

    public static void addComment(String postId, CommentModel commentModel) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    ArrayList<HashMap<String, Object>> comments = (ArrayList<HashMap<String, Object>>) documentSnapshot.get("comments");

                    HashMap<String, Object> newComment = new HashMap<>();
                    newComment.put("userId", commentModel.user);
                    newComment.put("comment", commentModel.comment);
                    comments.add(newComment);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("comments", comments);
                    firestore.collection("posts").document(documentSnapshot.getId()).update(updates);
                    break;
                }
            }
        });
    }

    public static void getLikesCount(String postId, TextView view) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("likes");
                    if (likes.size() > 1) {
                        if (likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            view.setText("You and " + (likes.size() - 1) + " others like this");
                        else
                            view.setText(likes.size() + " likes");
                    } else {
                        if (likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            view.setText("Only you like this");
                        else
                            view.setText(likes.size() + " likes");
                    }
                    break;
                }
            }
        });
    }

    public static void setCommentAvatarNickname(CommentModel commentModel, CircleImageView userAvatar, TextView userNicknameText) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("id", commentModel.user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    String nickname = (String) documentSnapshot.get("nickname");
                    String avatar = (String) documentSnapshot.get("avatar");
                    if (!avatar.isEmpty())
                        Picasso.get().load(avatar).into(userAvatar);
                    userNicknameText.setText(Html.fromHtml("<b>" + nickname + "</b> " + commentModel.comment));
                }
            }
        });
    }

    public static void setPostUserIconUsername(String userId, CircleImageView userAvatar, TextView userNicknameText) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("id", userId).addSnapshotListener((value, error) -> {
            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                String nickname = (String) documentSnapshot.get("nickname");
                String avatar = (String) documentSnapshot.get("avatar");
                if (!avatar.isEmpty())
                    Picasso.get().load(avatar).into(userAvatar);
                userNicknameText.setText(nickname);
            }
        });
    }

    public static void isLikedByUser(String postId, CheckBox view, TextView view1) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("likes");
                    if (likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        view.setChecked(true);
                        if (likes.size() > 1)
                            view1.setText("You and " + (likes.size() - 1) + " others like this");
                        else
                            view1.setText(R.string.only_you_like_this);
                    } else
                        view.setChecked(false);

                    break;
                }
            }
        });
    }

    public static void removeLike(String postId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", postId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("likes");
                        likes.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("likes", likes);
                        firestore.collection("posts").document(documentSnapshot.getId()).update(updates);
                        break;
                    }
                }
            }
        });
    }

    public static void removePost(String id) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String userId = String.valueOf(documentSnapshot.get("userId"));
                        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            firestore.collection("posts").document(documentSnapshot.getId()).delete();
                        break;
                    }
                }
            }
        });
    }

    public static void getStickersPacks(OnStickersPackLoad onStickersPackLoad) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<StickerPack> stickerPacks = new ArrayList<>();
        firestore.collection("stickers").addSnapshotListener((value, error) -> {
            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                String title = documentSnapshot.getString("title");
                String icon = documentSnapshot.getString("icon");
                ArrayList<StickerItem> items = new ArrayList<>();

                ArrayList<HashMap<String, String>> _items = (ArrayList<HashMap<String, String>>) documentSnapshot.get("items");
                for (HashMap<String, String> item : _items) {
                    items.add(new StickerItem(item.get("name"), item.get("image")));
                }
                stickerPacks.add(new StickerPack(title, icon, items));
            }
            onStickersPackLoad.onStickersPacksFetching(stickerPacks);
        });
    }
}
