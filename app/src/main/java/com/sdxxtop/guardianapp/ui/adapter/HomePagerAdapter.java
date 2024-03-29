package com.sdxxtop.guardianapp.ui.adapter;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private List<String> mTitleList;
    private List<Fragment> mFragmentList;

    public HomePagerAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        mTitleList = titleList;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    public void replaceData(List<String> titleList, List<Fragment> fragmentList){
        mTitleList = titleList;
        mFragmentList = fragmentList;
        notifyDataSetChanged();
    }
}
