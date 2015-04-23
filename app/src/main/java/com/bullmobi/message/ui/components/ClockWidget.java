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
package com.bullmobi.message.ui.components;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bullmobi.message.R;
import com.bullmobi.message.ui.fragments.EasyNotificationFragment;

/**
 * Very simple clock widget for {@link com.bullmobi.message.ui.fragments.EasyNotificationFragment}.
 *
 * @author Artem Chepurnoy
 */
public class ClockWidget extends Widget {

    private static final String TAG = "ClockWidget";

    public ClockWidget(@NonNull Callback callback, @NonNull EasyNotificationFragment fragment) {
        super(callback, fragment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHomeWidget() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasClock() {
        return true;
    }

    @Override
    protected ViewGroup onCreateView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup container,
            @Nullable ViewGroup sceneView) {
        boolean initialize = sceneView == null;
        if (initialize) {
            sceneView = (ViewGroup) inflater.inflate(R.layout.easynotification_scene_clock, container, false);
            assert sceneView != null;
        }

        if (!initialize) {
            return sceneView;
        }

        return sceneView;
    }

    @Nullable
    @Override
    public Bitmap getBackground() {
        return null;
    }

    @Override
    public int getBackgroundMask() {
        return 0;
    }

}
