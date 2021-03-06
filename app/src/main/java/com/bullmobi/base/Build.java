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
package com.bullmobi.base;

import android.support.annotation.NonNull;

import com.bullmobi.message.BuildConfig;

/**
 * Contains a number of build constants mostly redirected from
 * the {@link BuildConfig build config}.
 *
 * @author Artem Chepurnoy
 */
/* The first class of the EasyNotification, dated by 30.12.2013 */
public final class Build {

    /**
     * Is the current build <b>debug</b> or not.
     */
    public static final boolean DEBUG =
            BuildConfig.MY_DEBUG;

    /**
     * The timestamp of build in {@code EEE MMMM dd HH:mm:ss zzz yyyy} format.
     */
    @NonNull
    public static final String TIME_STAMP =
            BuildConfig.MY_TIME_STAMP;

    /**
     * Uncrypted Google Play's public key.
     *
     * @see #GOOGLE_PLAY_PUBLIC_KEY_SALT
     */
    @NonNull
    public static final String GOOGLE_PLAY_PUBLIC_KEY_ENCRYPTED =
            BuildConfig.MY_GOOGLE_PLAY_PUBLIC_KEY;

    /**
     * Salt for {@link #GOOGLE_PLAY_PUBLIC_KEY_ENCRYPTED}
     *
     * @see #GOOGLE_PLAY_PUBLIC_KEY_ENCRYPTED
     */
    @NonNull
    public static final String GOOGLE_PLAY_PUBLIC_KEY_SALT =
            BuildConfig.MY_GOOGLE_PLAY_PUBLIC_KEY_SALT;

    /**
     * The oficial e-mail for tons of complains, billions of
     * "How to uninistall?" screams and one or two useful emails.
     */
    @NonNull
    public static final String SUPPORT_EMAIL =
            "ynkr.wang@gmail.com";

}
