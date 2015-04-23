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
package com.bullmobi.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bullmobi.message.App;
import com.bullmobi.message.Config;
import com.bullmobi.message.R;
import com.bullmobi.base.utils.ToastUtils;

/**
 * Created by Artem on 11.03.14.
 */
public class ReceiverPublic extends BroadcastReceiver {

    private static final String TAG = "PublicReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Config config = Config.getInstance();
        String action = intent.getAction();
        switch (action) {
            case App.ACTION_ENABLE:
                Log.i(TAG, "Enabling EasyNotification by intent. " + intent);
                setEasyNotificationEnabled(context, config, true);
                break;
            case App.ACTION_DISABLE:
                Log.i(TAG, "Disabling EasyNotification by intent. " + intent);
                setEasyNotificationEnabled(context, config, false);
                break;
            case App.ACTION_TOGGLE:
                Log.i(TAG, "Toggling EasyNotification by intent. " + intent);
                setEasyNotificationEnabled(context, config, !config.isEnabled());
                break;
        }
    }

    /**
     * Tries to {@link com.bullmobi.message.Config#setEnabled(android.content.Context, boolean, com.bullmobi.message.Config.OnConfigChangedListener) enable / disable }
     * EasyNotification and shows toast message about the result.
     *
     * @param enable {@code true} to enable EasyNotification, {@code false} to disable.
     */
    private void setEasyNotificationEnabled(Context context, Config config, boolean enable) {
        enable &= App.getAccessManager().getMasterPermissions().isActive();
        config.setEnabled(context, enable, null);
        ToastUtils.showLong(context, enable
                ? R.string.remote_enable_easynotification
                : R.string.remote_disable_easynotification);
    }

}