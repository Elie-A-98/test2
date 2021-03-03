package com.carista.ui.main.fragments;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 2;

    public AdminViewPagerAdapter(@NonNull AdminFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PacksListingFragment();
            case 1:
                return new AddPackFragment();
        }
        return new PacksListingFragment();
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
