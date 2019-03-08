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
    private FeedPagerAdapter mFeedPagerAdapter;
    private ViewPager mViewPager;

    private HistoryTabFragment mFeedFriendsFragment;
    private HistoryTabFragment mFeedYouFragment;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mFeedPagerAdapter = new FeedPagerAdapter(getActivity().getSupportFragmentManager());

        mViewPager = rootView.findViewById(R.id.historyContainer);
        mViewPager.setAdapter(mFeedPagerAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.history_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        return rootView;
    }

    public class FeedPagerAdapter extends FragmentPagerAdapter {
        public FeedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mFeedFriendsFragment = new HistoryTabFragment();
                return mFeedFriendsFragment;
            } else {
                mFeedYouFragment = new HistoryTabFragment();
                return mFeedYouFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
