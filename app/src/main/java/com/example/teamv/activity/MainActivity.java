package com.example.teamv.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.teamv.databinding.ActivityMainBinding;
import com.example.teamv.fragment.AccountFragment;
import com.example.teamv.fragment.HomeFragment;
import com.example.teamv.fragment.NotificationsFragment;
import com.example.teamv.R;
import com.example.teamv.fragment.SearchFragment;
//
//public class  extends AppCompatActivity {
//    ActivityMainBinding binding;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Khi mở ứng dụng sẽ vào ngay giao diện Home
//        replaceFragment(new HomeFragment());
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Điều hướng
//        binding.bottomNavigation.setBackground(null);
//        binding.bottomNavigation.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.home) {
//                replaceFragment(new HomeFragment());
//            } else if (itemId == R.id.search) {
//                replaceFragment(new SearchFragment());
//            } else if (itemId == R.id.notifications) {
//                replaceFragment(new NotificationsFragment());
//            } else if (itemId == R.id.account) {
//                replaceFragment(new AccountFragment());
//            }
//            return true;
//        });
//    }
//    public void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }
//}