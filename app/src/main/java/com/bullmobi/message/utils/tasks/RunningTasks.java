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
package com.bullmobi.message.utils.tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bullmobi.base.Device;

import java.lang.ref.SoftReference;

/**
 * @author Artem Chepurnoy
 */
public abstract class RunningTasks {

    @NonNull
    private static SoftReference<RunningTasks> sFactoryRef = new SoftReference<>(null);

    @NonNull
    public static RunningTasks getInstance() {
        RunningTasks factory = sFactoryRef.get();
        if (factory == null) {
            factory = newInstance();
            sFactoryRef = new SoftReference<>(factory);
            return factory;
        }
        return factory;
    }

    @NonNull
    private static RunningTasks newInstance() {
        if (Device.hasLollipopApi()) {
            return new RunningTasksLollipop();
        }

        return new RunningTasksJellyBean();
    }

    /**
     * Gets the package name of top running activity.
     */
    @Nullable
    public abstract String getRunningTasksTop(@NonNull Context context);

}
