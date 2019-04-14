/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.amber.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.naman14.amber.R;
import com.naman14.amber.activities.MainActivity;
import com.naman14.amber.utils.ATEUtils;
import com.naman14.amber.utils.Helpers;
import com.naman14.amber.utils.NavigationUtils;
import com.naman14.amber.utils.PreferencesUtility;
import com.naman14.amber.widgets.SingleTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/7
 **/

public class MainFragment extends Fragment implements View.OnClickListener {

    private PreferencesUtility mPreferences;
    private ViewPager viewPager;
    private View searchBar;
    private ImageView settingIc;
    private ImageView navigationIc;
    private SingleTabLayout tabLayout;
    private ImageView searchIc;
    private TextView searchText;
    private ImageView sortIc;
    private Adapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_main, container, false);

//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//
//        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
        }

        searchBar = rootView.findViewById(R.id.title_search_bar);
        settingIc = rootView.findViewById(R.id.title_right_setting);
        navigationIc = rootView.findViewById(R.id.title_left_menu);
        searchIc = rootView.findViewById(R.id.title_search_ic);
        searchText = rootView.findViewById(R.id.title_search_text);
        sortIc = rootView.findViewById(R.id.title_sort_ic);

        searchBar.setOnClickListener(this);
        settingIc.setOnClickListener(this);
        navigationIc.setOnClickListener(this);
        sortIc.setOnClickListener(this);

        Resources resources = getResources();
        tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.initTabs(Arrays.asList(resources.getString(R.string.songs), resources.getString(R.string.albums), resources.getString(R.string.artists)));
        tabLayout.bindViewPager(viewPager);

//        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
            tabLayout.setTheme(true);
            navigationIc.setImageResource(R.drawable.ic_menu);
            settingIc.setImageResource(R.drawable.ic_setting);
            searchBar.setBackgroundResource(R.drawable.search_bar_bg_dark);
            searchIc.setImageResource(R.drawable.ic_search);
            searchText.setTextColor(getResources().getColor(R.color.C0_test));
            sortIc.setImageResource(R.drawable.ic_sort);
        } else {
            ATE.apply(this, "light_theme");
            tabLayout.setTheme(false);
            navigationIc.setImageResource(R.drawable.ic_menu_dark);
            settingIc.setImageResource(R.drawable.ic_setting_dark);
            searchBar.setBackgroundResource(R.drawable.search_bar_bg);
            searchIc.setImageResource(R.drawable.ic_search_dark);
            searchText.setTextColor(getResources().getColor(R.color.C3_test));
            sortIc.setImageResource(R.drawable.ic_sort_dark);
        }
        //启动页默认page
        viewPager.setCurrentItem(mPreferences.getStartPageIndex());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_left_menu) {
            ((MainActivity) getActivity()).openDrawer();
        } else if (v.getId() == R.id.title_right_setting) {
            NavigationUtils.navigateToSettings(getActivity());
        } else if (v.getId() == R.id.title_search_bar) {
            NavigationUtils.navigateToSearch(getActivity());
        } else if (v.getId() == R.id.title_sort_ic) {
            final AbsListFragment f = adapter.getItem(viewPager.getCurrentItem());
            final PopupMenu menu = new PopupMenu(getContext(), v);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    f.handleSortMenuClick(item);
                    return false;
                }
            });
            menu.inflate(f.getSortMenuLayout());
            menu.show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new SongsFragment(), this.getString(R.string.songs));
        adapter.addFragment(new AlbumFragment(), this.getString(R.string.albums));
        adapter.addFragment(new ArtistFragment(), this.getString(R.string.artists));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPreferences.lastOpenedIsStartPagePreference()) {
            mPreferences.setStartPageIndex(viewPager.getCurrentItem());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String ateKey = Helpers.getATEKey(getActivity());
        ATEUtils.setStatusBarColor(getActivity(), ateKey, Config.primaryColor(getActivity(), ateKey));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<AbsListFragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(AbsListFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public AbsListFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public static abstract class AbsListFragment extends Fragment {
        public abstract int getSortMenuLayout();
        public abstract void handleSortMenuClick(MenuItem item);
    }
}
