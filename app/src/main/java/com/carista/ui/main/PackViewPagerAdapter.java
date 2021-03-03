package com.carista.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.carista.ui.main.fragments.AddStickerFragment;

import com.carista.ui.main.fragments.StickersListingFragment;

public class PackViewPagerAdapter  extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 2;

    public PackViewPagerAdapter(@NonNull PackDetails packDetailsActivity) {
        super(packDetailsActivity);
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StickersListingFragment();
            case 1:
                return new AddStickerFragment();
        }
        return new StickersListingFragment();
    }
}
