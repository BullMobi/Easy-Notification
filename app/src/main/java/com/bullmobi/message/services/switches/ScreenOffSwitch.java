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
package com.bullmobi.message.services.switches;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.bullmobi.message.services.Switch;
import com.bullmobi.base.utils.power.PowerUtils;

/**
 * Prevents {@link com.bullmobi.message.services.SwitchService} from working
 * while the screen is turned on.
 *
 * @author Artem Chepurnoy
 */
public final class ScreenOffSwitch extends Switch {

    private PowerManager mPowerManager;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    requestInactive();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    requestActive();
                    break;
            }
        }

    };

    public ScreenOffSwitch(@NonNull Context context, @NonNull Callback callback) {
        super(context, callback);
    }

    @Override
    public void onCreate() {
        mPowerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mReceiver);
        mPowerManager = null;
    }

    @Override
    public boolean isActive() {
        return !PowerUtils.isScreenOn(mPowerManager);
    }

}
