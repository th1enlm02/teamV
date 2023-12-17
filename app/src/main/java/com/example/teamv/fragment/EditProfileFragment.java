package com.example.teamv.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.teamv.R;

public class EditProfileFragment extends Fragment {
    private View view;
    private EditText etFullname, etEmail, etDecription;
    private Button btnUpdateProfile;
    private ImageView ivCameraAvatar, ivCameraCover;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        etFullname = (EditText) view.findViewById(R.id.et_fullname);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        etDecription = (EditText) view.findViewById(R.id.et_description);
        btnUpdateProfile = (Button) view.findViewById(R.id.btn_update_profile);
        ivCameraAvatar = (ImageView) view.findViewById(R.id.iv_camera_avatar);
        ivCameraCover = (ImageView) view.findViewById(R.id.iv_camera_cover);

        ivCameraAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
        ivCameraCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void showBottomDialog() {
        final Dialog bottom_dialog = new Dialog(getActivity());
        bottom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottom_dialog.setContentView(R.layout.bottom_dialog_layout_account);

        LinearLayout llLibrarySource = bottom_dialog.findViewById(R.id.ll_library_source);
        LinearLayout llDeleteImage = bottom_dialog.findViewById(R.id.ll_delete_image);
        LinearLayout llCancel = bottom_dialog.findViewById(R.id.ll_cancel);

        llLibrarySource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bottom_dialog.show();
        bottom_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottom_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottom_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        bottom_dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Hàm này chưa hoàn thiện
        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_dialog.dismiss();
            }
        });
    }
}