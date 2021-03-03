package com.carista.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.carista.R;
import com.carista.ui.main.fragments.AdminFragment;
import com.carista.ui.main.fragments.PostFragment;
import com.carista.ui.main.fragments.UploadFragment;
import com.carista.ui.main.fragments.UserFragment;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;

    private Boolean isAdmin;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Boolean isAdmin) {
        super(fm);
        mContext = context;
        this.isAdmin = isAdmin;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PostFragment();
            case 1:
                return new UploadFragment();
            case 2:
                return new UserFragment();
            case 3:
                return new AdminFragment();
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        if(isAdmin == true){
            return 4;
        }
        return 3;
    }

}