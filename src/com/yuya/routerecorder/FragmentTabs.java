/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuya.routerecorder;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
import android.content.Intent;
import android.view.Window;
import android.widget.Toast;

import com.yuya.routerecorder.RouteMapFragment;


/**
 * This demonstrates the use of action bar tabs and how they interact
 * with other action bar features.
 */
public class FragmentTabs extends FragmentActivity {
	
	
	
	/****** Fragment Communication ******/
	RouteMapFragment fragmentA;
	AddRouteFragment fragmentB;
	MyListFragment fragmentC;
	/****** Fragment Communication ******/
	
	
	
	
	public static FragmentActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        
       
        instance = this;

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        //bar.setDisplayShowCustomEnabled(true);
        
        //bar.setDisplayShowTitleEnabled(false);
        //bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowCustomEnabled(false);
        
      
        
        bar.addTab(bar.newTab()
                .setIcon(R.drawable.world_white)
                .setTabListener(new TabListener<RouteMapFragment>(
                        this, "Show Map" ,  RouteMapFragment.class)));

        bar.addTab(bar.newTab()
        		.setIcon(R.drawable.pen_white_frame)
                .setTabListener(new TabListener<AddRouteFragment>(
                        this, "Add Trip", AddRouteFragment.class)));
              
        bar.addTab(bar.newTab()
        		.setIcon(R.drawable.list_new)
                .setTabListener(new TabListener<MyListFragment>(
                        this, "Show List", MyListFragment.class)));
        
      
       
        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
        
        //FragmentManager manager = getSupportFragmentManager();
        //fragmentA = (RouteMapFragment) manager.findFragmentById(R.id.map);
        //fragmentB = (AddRouteFragment) manager.findFragmentById(R.id.fragment_container);
        
    }// end of onCreate

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
              
                ft.commit();
            }
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
                //ft.remove(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            //Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }
    
    
}

