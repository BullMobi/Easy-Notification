/*
 * Copyright (C) 2015 AChep@xda <ynkr.wang@gmail.com>
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
package com.bullmobi.base.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bullmobi.base.AppHeap;
import com.bullmobi.base.interfaces.IActivityBase;
import com.bullmobi.base.tests.Check;
import com.bullmobi.base.utils.power.PowerSaveDetector;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;

/**
 * Created by Artem Chepurnoy on 08.03.2015.
 */
final class ActivityBaseInternal implements IActivityBase {

    private Activity mActivity;
    private ActivityCheckout mCheckout;
    private PowerSaveDetector mPowerSaveDetector;

    private boolean mCheckoutRequest;

    void onCreate(Activity activity, Bundle savedInstanceState) {
        if (mCheckoutRequest) mCheckout = Checkout.forActivity(activity, AppHeap.getCheckout());
        mPowerSaveDetector = PowerSaveDetector.newInstance(activity);
        mActivity = activity;
    }

    void onStart() {
        if (mCheckout != null) {
            AppHeap.getCheckoutInternal().requestConnect();
            mCheckout.start();
        }
        mPowerSaveDetector.start();
    }

    void onStop() {
        if (mCheckout != null) {
            mCheckout.stop();
            AppHeap.getCheckoutInternal().requestDisconnect();
        }
        mPowerSaveDetector.stop();
    }

    void onDestroy() {
        mCheckout = null;
    }

    boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return mCheckout != null && mCheckout.onActivityResult(requestCode, resultCode, data);
    }

    //-- IActivityBase --------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestCheckout() {
        Check.getInstance().isFalse(mCheckoutRequest);
        Check.getInstance().isNull(mPowerSaveDetector); // not created yet.
        mCheckoutRequest = true;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Activity getActivity() {
        return mActivity;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ActivityCheckout getCheckout() {
        return mCheckout;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PowerSaveDetector getPowerSaveDetector() {
        return mPowerSaveDetector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPowerSaveMode() {
        return mPowerSaveDetector.isPowerSaveMode();
    }
}
