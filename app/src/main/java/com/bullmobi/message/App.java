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
package com.bullmobi.message;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.bullmobi.message.blacklist.Blacklist;
import com.bullmobi.message.notifications.NotificationPresenter;
import com.bullmobi.message.permissions.AccessManager;
import com.bullmobi.message.services.KeyguardService;
import com.bullmobi.message.services.SensorsDumpService;
import com.bullmobi.message.services.activemode.ActiveModeService;
import com.bullmobi.message.ui.activities.MainActivity;
import com.bullmobi.base.AppHeap;
import com.bullmobi.base.content.ConfigBase;
import com.bullmobi.base.permissions.Permission;
import com.bullmobi.base.permissions.PermissionGroup;
import com.bullmobi.base.utils.smiley.SmileyParser;

/**
 * Created by Artem on 22.02.14.
 */
public class App extends Application {

    private static final String TAG = "App";

    public static final int ACCENT_COLOR = 0xFF607D8B;

    public static final int ID_NOTIFY_INIT = 30;
    public static final int ID_NOTIFY_TEST = 40;
    public static final int ID_NOTIFY_BATH = 50;
    public static final int ID_NOTIFY_APP_AUTO_DISABLED = 60;

    public static final String ACTION_BIND_MEDIA_CONTROL_SERVICE = "com.bullmobi.message.BIND_MEDIA_CONTROL_SERVICE";

    public static final String ACTION_ENABLE = "com.bullmobi.message.ENABLE";
    public static final String ACTION_DISABLE = "com.bullmobi.message.DISABLE";
    public static final String ACTION_TOGGLE = "com.bullmobi.message.TOGGLE";

    public static final String ACTION_EAT_HOME_PRESS_START = "com.bullmobi.message.EAT_HOME_PRESS_START";
    public static final String ACTION_EAT_HOME_PRESS_STOP = "com.bullmobi.message.EAT_HOME_PRESS_STOP";

    public static final String ACTION_INTERNAL_TIMEOUT = "TIMEOUT";
    public static final String ACTION_INTERNAL_PING_SENSORS = "PING_SENSORS";

    @NonNull
    private AccessManager mAccessManager;

    @NonNull
    private static App instance;

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        mAccessManager = new AccessManager(this);

        AppHeap.getInstance().init(this);
        Config.getInstance().init(this);
        Blacklist.getInstance().init(this);
        SmileyParser.init(this);

        // Init the main notification listener.
        NotificationPresenter.getInstance().setOnNotificationPostedListener(
                Config.getInstance().isEnabled()
                        ? Presenter.getInstance()
                        : null);

        super.onCreate();

        // Check the main switch.
        String divider = getString(R.string.settings_multi_list_divider);
        Config config = Config.getInstance();
        if (config.isEnabled()) {
            StringBuilder sb = new StringBuilder();
            boolean foundAny = false;

            PermissionGroup pg = getAccessManager().getMasterPermissions();
            for (Permission permission : pg.permissions) {
                if (!permission.isActive()) {
                    if (foundAny) {
                        sb.append(divider);
                    } else foundAny = true;
                    sb.append(getString(permission.getTitleResource()));
                }
            }

            if (foundAny) {
                String list = sb.toString();
                list = list.charAt(0) + list.substring(1).toLowerCase();

                ConfigBase.Option option = config.getOption(Config.KEY_ENABLED);
                option.write(config, this, false, null);

                final int id = App.ID_NOTIFY_APP_AUTO_DISABLED;
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        id, new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                NotificationCompat.BigTextStyle bts = new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.permissions_auto_disabled))
                        .setSummaryText(list);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.permissions_auto_disabled))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.stat_easynotification)
                        .setAutoCancel(true)
                        .setStyle(bts)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setColor(App.ACCENT_COLOR);

                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(id, builder.build());
            }
        }
        // Check the keyguard (without the notification).
        if (config.isKeyguardEnabled() && !getAccessManager().getKeyguardPermissions().isActive()) {
            ConfigBase.Option option = config.getOption(Config.KEY_KEYGUARD);
            option.write(config, this, false, null);
        }

        // Launch keyguard and (or) active mode on
        // app launch.
        KeyguardService.handleState(this);
        ActiveModeService.handleState(this);
        SensorsDumpService.handleState(this);
    }

    @Override
    public void onLowMemory() {
        Config.getInstance().onLowMemory();
        Blacklist.getInstance().onLowMemory();
        NotificationPresenter.getInstance().onLowMemory();
        mAccessManager.onLowMemory();
        super.onLowMemory();
    }

    @NonNull
    public static AccessManager getAccessManager() {
        return instance.mAccessManager;
    }

}
