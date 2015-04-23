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

import android.content.Context;
import android.support.annotation.NonNull;

import com.bullmobi.message.notifications.NotificationPresenter;
import com.bullmobi.message.notifications.OpenNotification;
import com.bullmobi.message.services.Switch;
import com.bullmobi.base.content.ConfigBase;

/**
 * Prevents {@link com.bullmobi.message.services.SwitchService} from working
 * while the notification list is empty (if corresponding option is enabled.)
 *
 * @author Artem Chepurnoy
 * @see com.bullmobi.message.ui.fragments.settings.ActiveModeSettings
 * @see com.bullmobi.message.ui.fragments.settings.KeyguardSettings
 */
public final class NoNotifiesSwitch extends Switch.Optional implements
        NotificationPresenter.OnNotificationListChangedListener {

    private NotificationPresenter mNotificationPresenter;

    public NoNotifiesSwitch(
            @NonNull Context context,
            @NonNull Callback callback,
            @NonNull ConfigBase.Option option, boolean isOptionInverted) {
        super(context, callback, option, isOptionInverted);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationPresenter = NotificationPresenter.getInstance();
        mNotificationPresenter.registerListener(this);
    }

    @Override
    public void onDestroy() {
        mNotificationPresenter.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public boolean isActiveInternal() {
        return !mNotificationPresenter.isEmpty();
    }

    @Override
    public void onNotificationListChanged(@NonNull NotificationPresenter np,
                                          OpenNotification osbn,
                                          int event, boolean isLastEventInSequence) {
        switch (event) {
            case NotificationPresenter.EVENT_BATH:
            case NotificationPresenter.EVENT_POSTED:
            case NotificationPresenter.EVENT_REMOVED:
                if (isLastEventInSequence) {
                    if (isActiveInternal()) {
                        requestActiveInternal();
                    } else {
                        requestInactiveInternal();
                    }
                }
                break;
        }
    }

}
