package com.put.miasi.main;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.put.miasi.R;
import com.put.miasi.main.history.HistoryTabFragment;

public class HistoryFragment extends Fragment {
    private HistoryPagerAdapter mHistoryPagerAdapter;
    private ViewPager mViewPager;

    private HistoryTabFragment mParticipatedFragment;
    private HistoryTabFragment mOfferedFragment;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mHistoryPagerAdapter = new HistoryPagerAdapter(getActivity().getSupportFragmentManager());

        mViewPager = rootView.findViewById(R.id.historyContainer);
        mViewPager.setAdapter(mHistoryPagerAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.history_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        return rootView;
    }

    public class HistoryPagerAdapter extends FragmentPagerAdapter {
        public HistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mParticipatedFragment = new HistoryTabFragment();
                return mParticipatedFragment;
            } else {
                mOfferedFragment = new HistoryTabFragment();
                return mOfferedFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
