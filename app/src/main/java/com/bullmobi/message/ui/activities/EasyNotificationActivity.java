/*
 * Copyright (C) 2014 AChep@xda <ynkr.wang@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package com.bullmobi.message.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.bullmobi.message.Config;
import com.bullmobi.message.Presenter;
import com.bullmobi.message.R;
import com.bullmobi.message.Timeout;
import com.bullmobi.message.ui.fragments.PocketFragment;
import com.bullmobi.base.Device;

/**
 * Created by Artem on 25.01.14.
 */
public class EasyNotificationActivity extends KeyguardActivity implements
        Timeout.OnTimeoutEventListener,
        PocketFragment.OnSleepRequestListener {

    private static final String TAG = "EasyNotificationActivity";

    private final Config mConfig = Config.getInstance();
    private final Presenter mPresenter = Presenter.getInstance();


    private PocketFragment mPocketFragment;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        populateFlags(hasFocus);
    }

    @SuppressLint("NewApi")
    private void populateFlags(boolean windowHasFocus) {
        final View decorView = getWindow().getDecorView();

        if (windowHasFocus) {
            int visibilityUi = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE;

            if (mConfig.isFullScreen()) {
                // Hide status bar if fullscreen mode is enabled.
                visibilityUi = visibilityUi
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;

                if (Device.hasKitKatApi()) {
                    // Hide navigation bar and flag sticky.
                    visibilityUi = visibilityUi
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                }
            }

            decorView.setSystemUiVisibility(visibilityUi);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mConfig.isWallpaperShown()) setTheme(R.style.MaterialTheme_EasyNotification_Wallpaper);
        super.onCreate(savedInstanceState);
        mPresenter.onCreate(this);

        setContentView(R.layout.easynotification);

        // Initialize non-UI fragments.
        if (savedInstanceState == null) {
            initInternalFragments();
        } else {

            // Find fragments.
            FragmentManager fm = getSupportFragmentManager();
            mPocketFragment = (PocketFragment) fm.findFragmentByTag(PocketFragment.TAG);
            // TODO: Maybe remove PocketFragment if active mode is disabled?
        }

        // Setup fragments.
        if (mPocketFragment != null) {
            mPocketFragment.setListener(this);
        }

        //   mPulsingThread = new PulsingThread(getContentResolver());
        //   mPulsingThread.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();

        mConfig.getTriggers().incrementLaunchCount(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();

        hideHeadsUpApp(true);
        populateFlags(true);
    }

    @Override
    protected void onPause() {
        hideHeadsUpApp(false);
        populateFlags(false);

        mPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    /**
     * Asks HeadsUp (https://play.google.com/store/apps/details?id=com.bullmobi.headsup) to
     * pause or continue showing its floating notifications.
     *
     * @param hideHeadsUp {@code true} to disallow showing floating notifications,
     *                    {@code false} to allow.
     */
    private void hideHeadsUpApp(boolean hideHeadsUp) {
        Intent intent = new Intent(hideHeadsUp
                ? "com.bullmobi.headsup.ACTION_DISALLOW_HEADSUP"
                : "com.bullmobi.headsup.ACTION_ALLOW_HEADSUP");
        sendBroadcast(intent);
    }

    /**
     * Initializes non-UI fragments such as {@link com.bullmobi.message.ui.fragments.PocketFragment}.
     */
    private void initInternalFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Turns screen off inside of your pocket.
        if (mConfig.isActiveModeEnabled()) {
            mPocketFragment = PocketFragment.newInstance();
            ft.add(mPocketFragment, PocketFragment.TAG);
        }

        ft.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSleepRequest() {
        // Probably it's not the best solution, but not worst too.
        // Check if user does not interact with app before locking.
        if (!getTimeout().isPaused()) {

            return lock();
        }
        return false;
    }

    public Config getConfig() {
        return mConfig;
    }

}