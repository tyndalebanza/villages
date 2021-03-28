package com.tyndaleb.villages;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyTabAdapter extends FragmentPagerAdapter {

    private String search_term;

    private final List<Fragment> mFragmentList = new ArrayList<>();

    Context context;
    int totalTabs;
    public MyTabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        context = c;
        this.totalTabs = totalTabs;

    }

    public void addFragment(Fragment fragment, String search_term) {
        mFragmentList.add(fragment);
        this.search_term = search_term;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return Village.newInstance(this.search_term);
        }
        else {
            Country country = Country.newInstance(this.search_term);
            return country;
        }
    }



    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
