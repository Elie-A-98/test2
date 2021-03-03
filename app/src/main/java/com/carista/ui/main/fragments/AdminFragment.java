package com.carista.ui.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

import com.carista.R;
import com.google.android.material.tabs.TabLayout;


import com.google.android.material.tabs.TabLayoutMediator;

public class AdminFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 100;

    ViewPager2 viewPager;
    TabLayout tabLayout;

    int[] drawableIds = {R.drawable.ic_img_view, R.drawable.ic_add_pack};

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        viewPager = view.findViewById(R.id.view_pager);

        tabLayout = view.findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setIcon(drawableIds[position])).attach();
        return view;

    }

    private AdminViewPagerAdapter createCardAdapter() {
        return new AdminViewPagerAdapter(this);
    }
}