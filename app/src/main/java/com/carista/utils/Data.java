package com.carista.utils;

import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carista.data.db.AppDatabase;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.data.realtimedb.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.carista.photoeditor.photoeditor.TextEditorDialogFragment.TAG;

public class Data {

    public static void uploadPost(String title, long id, String imageURL, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .add(new PostModel(title,imageURL,userId))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public static void uploadAvatarLink(String avatarURL) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("avatar",avatarURL);
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(data, SetOptions.merge());
    }

    public static void uploadNickname(String nickname) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("nickname",nickname);
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(data, SetOptions.merge());
    }

    public static void addLike(String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference posts = db.collection("posts");
        ArrayList<String> likes = new ArrayList<String>();
        likes.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("Likes",likes);
        posts.document(postId).set(data,SetOptions.merge());
    }

    public static void addComment(String postId, CommentModel commentModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference posts = db.collection("posts");
        ArrayList<CommentModel> comments = new ArrayList<CommentModel>();
        comments.add(commentModel);
        Map<String, Object> data = new HashMap<>();
        data.put("comments",comments);
        posts.document(postId).update("comments", FieldValue.arrayUnion(commentModel));
    }

    public static void getLikesCount(String postId, TextView view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference post = db.collection("posts").document(postId);
        post.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> likes=new ArrayList<String>();
                likes=(ArrayList<String>)value.get("Likes");
                if(likes!=null){
                    if (likes.size() == 1)
                        view.setText(likes.size() + " like");
                    else
                        view.setText(likes.size() + " likes");
                    return;
                }
            }
        });
    }

    public static void setCommentAvatarNickname(CommentModel commentModel, CircleImageView userAvatar, TextView userNicknameText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("users").document(commentModel.user);
        user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Object avatar = value.get("avatar");
                Object nickname = value.get("nickname");
                String nicknameText = null;
                if (nickname == null || nickname == null || nickname.toString().isEmpty())
                    nicknameText = "<b>Anonymous</b> " + commentModel.comment;
                else
                    nicknameText = "<b>" + nickname.toString() + "</b> " + commentModel.comment;
                userNicknameText.setText(Html.fromHtml(nicknameText));

                if (avatar != null)
                    Picasso.get().load(avatar.toString()).into(userAvatar);
            }
        });
    }

    public static void setPostNicknameTitle(String user, String title, TextView userNicknameTitle) {
        if (user.equals("Unknown"))
            return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user);
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Object nickname = value.get("nickname");
                String nicknameText = null;
                if (nickname == null || nickname == null || nickname.toString().isEmpty())
                    nicknameText = "<b>Anonymous</b> " + title;
                else
                    nicknameText = "<b>" + nickname.toString() + "</b> " + title;
                userNicknameTitle.setText(Html.fromHtml(nicknameText));
            }
        });
    }

    public static void isLikedByUser(String postId, CheckBox view, TextView view1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference post = db.collection("posts").document(postId);
        post.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> likes=new ArrayList<String>();
                likes=(ArrayList<String>)value.get("Likes");
                if(likes!=null){
                    for (String like : likes) {
                        if (like.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            view.setChecked(true);
                            int likesNb = Integer.parseInt(view1.getText().toString().split(" ")[0]);
                            if (likesNb > 1)
                                view1.setText("You and " + (likesNb - 1) + " others like this");
                            else view1.setText("Only you like this");
                            return;
                        }
                        else
                        {
                            view1.setText("");
                        }
                    }
                }
                view.setChecked(false);
            }
        });
    }

    public static void removeLike(String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).update("LikesArray", FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()));
    }


    public static void removePost(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(id).delete().addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Remove Post", "onCancelled", e);
            }
        });
    }
}
