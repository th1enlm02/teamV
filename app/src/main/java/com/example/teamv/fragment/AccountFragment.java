package com.example.teamv.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.object.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AccountFragment extends Fragment {
    private ImageView ivAvatar, ivEditProfile;
    private TextView tvFullname, tvEmail, tvDecription;
    private View view;
    private FirebaseAuth firebaseAuth;

    // Chuc Thien
    private String PREFERENCE_KEY = "LogIn_SharePreferences";
    private String LOGIN_KEY = "LOGIN";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        // get view
        findViewByIds(view);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Đã xảy ra lỗi. Thông tin người dùng hiện không có sẵn",
                    Toast.LENGTH_LONG).show();
        } else {
            String userID = firebaseUser.getUid();

//            // Trích xuất thông tin người dùng từ CSDL cho "Users"
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

//            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User readUserDetails = snapshot.getValue(User.class);
//                    if (readUserDetails != null) {
//                        tvFullname.setText(readUserDetails.fullname);
//                        tvEmail.setText(firebaseUser.getEmail());
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(getActivity(), "Đã xảy ra lỗi",
//                            Toast.LENGTH_LONG).show();
//                }
//            });

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference userReference = firestore.collection("User").document(userID);
            userReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);

                                // need to edit here for security problems
                                tvFullname.setText(user.getFullname());
                                tvEmail.setText(user.getEmail());
                            } else {
                                Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new EditProfileFragment());
                fragmentTransaction.commit();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void findViewByIds(View vỉew) {
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvFullname = (TextView) view.findViewById(R.id.tv_fullname);
        tvEmail = (TextView) view.findViewById(R.id.tv_email);
        ivEditProfile = (ImageView) view.findViewById(R.id.iv_edit_profile);
        tvDecription = (TextView) view.findViewById(R.id.tv_decription);
    }
}