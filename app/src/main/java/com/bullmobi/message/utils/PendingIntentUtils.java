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
package com.bullmobi.message.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Artem on 02.01.14.
 */
public class PendingIntentUtils {

    public static boolean sendPendingIntent(@Nullable PendingIntent contentIntent) {
        if (contentIntent != null)
            try {
                contentIntent.send();
                return true;
            } catch (PendingIntent.CanceledException e) { /* unused */ }
        return false;
    }

    public static boolean sendPendingIntent(@Nullable PendingIntent pi,
                                            @NonNull Context context,
                                            @Nullable Intent intent) {
        if (pi != null)
            try {
                pi.send(context, 0, intent);
                return true;
            } catch (PendingIntent.CanceledException e) { /* unused */ }
        return false;
    }

}
