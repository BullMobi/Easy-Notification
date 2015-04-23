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

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.bullmobi.message.notifications.NotificationPresenter;
import com.bullmobi.message.plugins.powertoggles.ToggleReceiver;
import com.bullmobi.message.services.KeyguardService;
import com.bullmobi.message.services.SensorsDumpService;
import com.bullmobi.message.services.activemode.ActiveModeService;
import com.bullmobi.base.content.ConfigBase;

import java.util.Map;

/**
 * Saves all the configurations for the app.
 *
 * @author Artem Chepurnoy
 * @since 21.01.14
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public final class Config extends ConfigBase {

    private static final String TAG = "Config";

    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_ONLY_WHILE_CHARGING = "only_while_charging";

    // notifications
    public static final String KEY_NOTIFY_MIN_PRIORITY = "notify_min_priority";
    public static final String KEY_NOTIFY_MAX_PRIORITY = "notify_max_priority";
    public static final String KEY_NOTIFY_WAKE_UP_ON = "notify_wake_up_on";

    // inactive time
    public static final String KEY_INACTIVE_TIME_FROM = "inactive_time_from";
    public static final String KEY_INACTIVE_TIME_TO = "inactive_time_to";
    public static final String KEY_INACTIVE_TIME_ENABLED = "inactive_time_enabled";

    // timeouts
    public static final String KEY_TIMEOUT_NORMAL = "timeout_normal";
    public static final String KEY_TIMEOUT_SHORT = "timeout_short";

    // keyguard
    public static final String KEY_KEYGUARD = "keyguard";
    public static final String KEY_KEYGUARD_RESPECT_INACTIVE_TIME = "keyguard_respect_inactive_time";
    public static final String KEY_KEYGUARD_WITHOUT_NOTIFICATIONS = "keyguard_without_notifications";

    // active mode
    public static final String KEY_ACTIVE_MODE = "active_mode";
    public static final String KEY_ACTIVE_MODE_RESPECT_INACTIVE_TIME = "active_mode_respect_inactive_time";
    public static final String KEY_ACTIVE_MODE_WITHOUT_NOTIFICATIONS = "active_mode_without_notifications";
    public static final String KEY_ACTIVE_MODE_ACTIVE_CHARGING = "active_mode_active_charging";
    public static final String KEY_ACTIVE_MODE_DISABLE_ON_LOW_BATTERY = "active_mode_disable_on_low_battery";

    // interface
    public static final String KEY_UI_FULLSCREEN = "ui_fullscreen";
    public static final String KEY_UI_WALLPAPER_SHOWN = "wallpaper_shown";
    public static final String KEY_UI_DYNAMIC_BACKGROUND_MODE = "dynamic_background_mode";
    public static final int DYNAMIC_BG_ARTWORK_MASK = 1;
    public static final int DYNAMIC_BG_NOTIFICATION_MASK = 2;
    public static final String KEY_UI_STATUS_BATTERY_STICKY = "ui_status_battery_sticky";
    public static final String KEY_UI_ICON_SIZE = "ui_condensed_view_size";
    public static final String ICON_SIZE_PX = "px";
    public static final String ICON_SIZE_DP = "dp";
    public static final String KEY_UI_UNLOCK_ANIMATION = "unlock_animation";
    public static final String KEY_UI_CIRCLE_COLOR_INNER = "ui_circle_color_inner";
    public static final String KEY_UI_CIRCLE_COLOR_OUTER = "ui_circle_color_outer";
    public static final String KEY_UI_OVERRIDE_FONTS = "ui_override_fonts";
    public static final String KEY_UI_EMOTICONS = "ui_emoticons";

    // behavior
    public static final String KEY_FEEL_SCREEN_OFF_AFTER_LAST_NOTIFY = "feel_widget_screen_off_after_last_notify";
    public static final String KEY_FEEL_WIDGET_PINNABLE = "feel_widget_pinnable";
    public static final String KEY_FEEL_WIDGET_READABLE = "feel_widget_readable";
    public static final String KEY_PRIVACY = "privacy_mode";
    public static final int PRIVACY_HIDE_CONTENT_MASK = 1;
    public static final int PRIVACY_HIDE_ACTIONS_MASK = 2;
    public static final String KEY_DOUBLE_TAP_TO_SLEEP = "double_tap_to_sleep";

    // development
    public static final String KEY_DEV_SENSORS_DUMP = "dev_sensors_dump";

    // triggers
    public static final String KEY_TRIG_PREVIOUS_VERSION = "trigger_previous_version";
    public static final String KEY_TRIG_HELP_READ = "trigger_dialog_help";
    public static final String KEY_TRIG_TRANSLATED = "trigger_translated";
    public static final String KEY_TRIG_LAUNCH_COUNT = "trigger_launch_count";
    public static final String KEY_TRIG_DONATION_ASKED = "trigger_donation_asked";

    private static Config sConfig;

    private boolean mEnabled;
    private boolean mKeyguardEnabled;
    private boolean mKeyguardRespectInactiveTime;
    private boolean mKeyguardWithoutNotifies;
    private boolean mActiveMode;
    private boolean mActiveModeRespectInactiveTime;
    private boolean mActiveModeWithoutNotifies;
    private boolean mActiveModeActiveCharging;
    private boolean mActiveModeDisableOnLowBattery;
    private boolean mEnabledOnlyWhileCharging;
    private boolean mScreenOffAfterLastNotify;
    private boolean mDoubleTapToSleep;
    private boolean mFeelWidgetPinnable;
    private boolean mFeelWidgetReadable;
    private boolean mNotifyWakeUpOn;
    private int mNotifyMinPriority;
    private int mNotifyMaxPriority;
    private int mTimeoutNormal;
    private int mTimeoutShort;
    private int mInactiveTimeFrom;
    private int mInactiveTimeTo;
    private int mUiDynamicBackground;
    private int mUiIconSize; // dp.
    private int mUiCircleColorInner;
    private int mUiCircleColorOuter;
    private int mPrivacyMode;
    private boolean mInactiveTimeEnabled;
    private boolean mUiFullScreen;
    private boolean mUiOverrideFonts;
    private boolean mUiEmoticons;
    private boolean mUiWallpaper;
    private boolean mUiBatterySticky;
    private boolean mUiUnlockAnimation;

    private boolean mDevSensorsDump;

    private final Triggers mTriggers;
    private int mTrigPreviousVersion;
    private int mTrigLaunchCount;
    private boolean mTrigTranslated;
    private boolean mTrigHelpRead;
    private boolean mTrigDonationAsked;

    @NonNull
    public static synchronized Config getInstance() {
        if (sConfig == null) {
            sConfig = new Config();
        }
        return sConfig;
    }

    private Config() {
        mTriggers = new Triggers();
    }

    /**
     * Loads saved values from shared preferences.
     * This is called on {@link App app's} create.
     */
    void init(@NonNull Context context) {
        initInternal(context);
    }

    public void reset(@NonNull Context context) {
        resetInternal(context);
    }

    @Override
    protected void onCreateMap(@NonNull Map<String, Option> map) {
        map.put(KEY_ENABLED, new ConfigBase.Option(
                "mEnabled", "setEnabled", "isEnabled", boolean.class)
                .setDefaultRes(R.bool.config_default_enabled));
        map.put(KEY_KEYGUARD, new ConfigBase.Option(
                "mKeyguardEnabled", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_keyguard_enabled));
        map.put(KEY_KEYGUARD_RESPECT_INACTIVE_TIME, new ConfigBase.Option(
                "mKeyguardRespectInactiveTime", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_keyguard_respect_inactive_time));
        map.put(KEY_KEYGUARD_WITHOUT_NOTIFICATIONS, new ConfigBase.Option(
                "mKeyguardWithoutNotifies", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_keyguard_without_notifies_enabled));
        map.put(KEY_ACTIVE_MODE, new ConfigBase.Option(
                "mActiveMode", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_active_mode_enabled));
        map.put(KEY_ACTIVE_MODE_RESPECT_INACTIVE_TIME, new ConfigBase.Option(
                "mActiveModeRespectInactiveTime", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_active_mode_respect_inactive_time));
        map.put(KEY_ACTIVE_MODE_WITHOUT_NOTIFICATIONS, new ConfigBase.Option(
                "mActiveModeWithoutNotifies", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_active_mode_without_notifies_enabled));
        map.put(KEY_ACTIVE_MODE_ACTIVE_CHARGING, new ConfigBase.Option(
                "mActiveModeActiveCharging", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_active_mode_active_charging));
        map.put(KEY_ACTIVE_MODE_DISABLE_ON_LOW_BATTERY, new ConfigBase.Option(
                "mActiveModeDisableOnLowBattery", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_active_mode_disable_on_low_battery));

        // notifications
        map.put(KEY_NOTIFY_WAKE_UP_ON, new ConfigBase.Option(
                "mNotifyWakeUpOn", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_notify_wake_up_on));
        map.put(KEY_NOTIFY_MIN_PRIORITY, new ConfigBase.Option(
                "mNotifyMinPriority", null, null, int.class)
                .setDefaultRes(R.integer.config_default_notify_min_priority));
        map.put(KEY_NOTIFY_MAX_PRIORITY, new ConfigBase.Option(
                "mNotifyMaxPriority", null, null, int.class)
                .setDefaultRes(R.integer.config_default_notify_max_priority));

        // timeout
        map.put(KEY_TIMEOUT_NORMAL, new ConfigBase.Option(
                "mTimeoutNormal", null, null, int.class)
                .setDefaultRes(R.integer.config_default_timeout_normal));
        map.put(KEY_TIMEOUT_SHORT, new ConfigBase.Option(
                "mTimeoutShort", null, null, int.class)
                .setDefaultRes(R.integer.config_default_timeout_short));

        // inactive time
        map.put(KEY_INACTIVE_TIME_ENABLED, new ConfigBase.Option(
                "mInactiveTimeEnabled", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_inactive_time_enabled));
        map.put(KEY_INACTIVE_TIME_FROM, new ConfigBase.Option(
                "mInactiveTimeFrom", null, null, int.class)
                .setDefaultRes(R.integer.config_default_inactive_time_from));
        map.put(KEY_INACTIVE_TIME_TO, new ConfigBase.Option(
                "mInactiveTimeTo", null, null, int.class)
                .setDefaultRes(R.integer.config_default_inactive_time_to));

        // interface
        map.put(KEY_UI_FULLSCREEN, new ConfigBase.Option(
                "mUiFullScreen", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_ui_full_screen));
        map.put(KEY_UI_WALLPAPER_SHOWN, new ConfigBase.Option(
                "mUiWallpaper", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_ui_show_wallpaper));
        map.put(KEY_UI_STATUS_BATTERY_STICKY, new ConfigBase.Option(
                "mUiBatterySticky", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_ui_status_battery_sticky));
        map.put(KEY_UI_DYNAMIC_BACKGROUND_MODE, new ConfigBase.Option(
                "mUiDynamicBackground", null, null, int.class)
                .setDefaultRes(R.integer.config_default_ui_show_shadow_dynamic_bg));
        map.put(KEY_UI_CIRCLE_COLOR_INNER, new ConfigBase.Option(
                "mUiCircleColorInner", null, null, int.class)
                .setDefault(0xFFF0F0F0));
        map.put(KEY_UI_CIRCLE_COLOR_OUTER, new ConfigBase.Option(
                "mUiCircleColorOuter", null, null, int.class)
                .setDefault(0xFF303030));
        map.put(KEY_UI_UNLOCK_ANIMATION, new ConfigBase.Option(
                "mUiUnlockAnimation", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_ui_unlock_animation));
        map.put(KEY_UI_EMOTICONS, new ConfigBase.Option(
                "mUiEmoticons", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_ui_emoticons));
        map.put(KEY_UI_OVERRIDE_FONTS, new ConfigBase.Option(
                "mUiOverrideFonts", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_ui_override_fonts));
        map.put(KEY_UI_ICON_SIZE, new ConfigBase.Option(
                "mUiIconSize", null, null, int.class)
                .setDefaultRes(R.integer.config_default_ui_icon_size_dp));

        // development
        map.put(KEY_DEV_SENSORS_DUMP, new ConfigBase.Option(
                "mDevSensorsDump", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_dev_sensors_dump));

        // other
        map.put(KEY_ONLY_WHILE_CHARGING, new ConfigBase.Option(
                "mEnabledOnlyWhileCharging", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_enabled_only_while_charging));
        map.put(KEY_FEEL_SCREEN_OFF_AFTER_LAST_NOTIFY, new ConfigBase.Option(
                "mScreenOffAfterLastNotify", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_feel_screen_off_after_last_notify));
        map.put(KEY_FEEL_WIDGET_PINNABLE, new ConfigBase.Option(
                "mFeelWidgetPinnable", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_feel_widget_pinnable));
        map.put(KEY_FEEL_WIDGET_READABLE, new ConfigBase.Option(
                "mFeelWidgetReadable", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_feel_widget_readable));
        map.put(KEY_PRIVACY, new ConfigBase.Option(
                "mPrivacyMode", null, null, int.class)
                .setDefaultRes(R.integer.config_default_privacy_mode));
        map.put(KEY_DOUBLE_TAP_TO_SLEEP, new ConfigBase.Option(
                "mDoubleTapToSleep", null, null, boolean.class)
                .setDefaultRes(R.bool.config_default_double_tap_to_sleep));

        // triggers
        map.put(KEY_TRIG_DONATION_ASKED, new ConfigBase.Option(
                "mTrigDonationAsked", null, null, boolean.class)
                .setDefault(false));
        map.put(KEY_TRIG_HELP_READ, new ConfigBase.Option(
                "mTrigHelpRead", null, null, boolean.class)
                .setDefault(false));
        map.put(KEY_TRIG_LAUNCH_COUNT, new ConfigBase.Option(
                "mTrigLaunchCount", null, null, int.class)
                .setDefault(0));
        map.put(KEY_TRIG_PREVIOUS_VERSION, new ConfigBase.Option(
                "mTrigPreviousVersion", null, null, int.class)
                .setDefault(0));
        map.put(KEY_TRIG_TRANSLATED, new ConfigBase.Option(
                "mTrigTranslated", null, null, boolean.class)
                .setDefault(false));
    }

    @Override
    protected void onOptionChanged(@NonNull Option option, @NonNull String key) {
        switch (key) {
            case KEY_ACTIVE_MODE:
                ActiveModeService.handleState(getContext());
                break;
            case KEY_KEYGUARD:
                KeyguardService.handleState(getContext());
                break;
            case KEY_ENABLED:
                ToggleReceiver.sendStateUpdate(ToggleReceiver.class, mEnabled, getContext());
                NotificationPresenter.getInstance().setOnNotificationPostedListener(isEnabled()
                        ? Presenter.getInstance()
                        : null);
            case KEY_ONLY_WHILE_CHARGING:
                ActiveModeService.handleState(getContext());
                KeyguardService.handleState(getContext());
                break;
            case KEY_DEV_SENSORS_DUMP:
                SensorsDumpService.handleState(getContext());
                break;
        }
    }

    /**
     * Separated group of different internal triggers.
     */
    @NonNull
    public Triggers getTriggers() {
        return mTriggers;
    }

    // //////////////////////////////////////////
    // ///////////// -- OPTIONS -- //////////////
    // //////////////////////////////////////////

    /**
     * Setter for the entire app enabler.
     */
    public void setEnabled(@NonNull Context context, boolean enabled,
                           @Nullable OnConfigChangedListener listener) {
        writeFromMain(context, getOption(KEY_ENABLED), enabled, listener);
    }

    /**
     * Setter to enable "night mode".
     */
    public void setInactiveTimeEnabled(@NonNull Context context, boolean enabled,
                                       @Nullable OnConfigChangedListener listener) {
        writeFromMain(context, getOption(KEY_INACTIVE_TIME_ENABLED), enabled, listener);
    }

    /**
     * Setter for the time "night mode" should start
     */
    public void setInactiveTimeFrom(@NonNull Context context, int minutes,
                                    @Nullable OnConfigChangedListener listener) {
        writeFromMain(context, getOption(KEY_INACTIVE_TIME_FROM), minutes, listener);
    }

    /**
     * Setter for the time "night mode" should end.
     */
    public void setInactiveTimeTo(@NonNull Context context, int minutes,
                                  @Nullable OnConfigChangedListener listener) {
        writeFromMain(context, getOption(KEY_INACTIVE_TIME_TO), minutes, listener);
    }

    /**
     * Sets the size (or height only) of collapsed views.
     *
     * @param size preferred size in dip.
     * @see #getIconSizePx()
     * @see #getIconSize(String)
     */
    public void setIconSizeDp(@NonNull Context context, int size,
                              @Nullable OnConfigChangedListener listener) {
        writeFromMain(context, getOption(KEY_UI_ICON_SIZE), size, listener);
    }

    /**
     * @return minimal {@link android.app.Notification#priority} of notification to be shown.
     * @see #getNotifyMaxPriority()
     * @see android.app.Notification#priority
     */
    public int getNotifyMinPriority() {
        return mNotifyMinPriority;
    }

    /**
     * @return maximum {@link android.app.Notification#priority} of notification to be shown.
     * @see #getNotifyMinPriority()
     * @see android.app.Notification#priority
     */
    public int getNotifyMaxPriority() {
        return mNotifyMaxPriority;
    }

    /**
     * @return the color of unlock circle
     * @see #getCircleOuterColor()
     */
    public int getCircleInnerColor() {
        return mUiCircleColorInner;
    }

    /**
     * @return the background color of the unlock circle
     * @see #getCircleInnerColor()
     */
    public int getCircleOuterColor() {
        return mUiCircleColorOuter;
    }

    public int getTimeoutNormal() {
        return mTimeoutNormal;
    }

    public int getTimeoutShort() {
        return mTimeoutShort;
    }

    public int getInactiveTimeFrom() {
        return mInactiveTimeFrom;
    }

    public int getInactiveTimeTo() {
        return mInactiveTimeTo;
    }

    public int getDynamicBackgroundMode() {
        return mUiDynamicBackground;
    }

    /**
     * Gets the current privacy mode.
     *
     * @return the bit-mask containing different privacy options.
     * @see #PRIVACY_HIDE_ACTIONS_MASK
     * @see #PRIVACY_HIDE_CONTENT_MASK
     */
    public int getPrivacyMode() {
        return mPrivacyMode;
    }

    /**
     * @return the size (or height only) of collapsed views in pixels.
     * @see #getIconSize(String)
     */
    public int getIconSizePx() {
        return getIconSize(ICON_SIZE_PX);
    }

    /**
     * @return the size (or height only) of collapsed views.
     * @see #getIconSizePx()
     * @see #ICON_SIZE_DP
     * @see #ICON_SIZE_PX
     */
    public int getIconSize(@NonNull String type) {
        switch (type) {
            case ICON_SIZE_PX:
                DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUiIconSize, dm);
            case ICON_SIZE_DP:
                return mUiIconSize;
            default:
                throw new IllegalArgumentException(type + " is not a valid icon size type.");
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean isKeyguardEnabled() {
        return mKeyguardEnabled;
    }

    public boolean isKeyguardWithoutNotifiesEnabled() {
        return mKeyguardWithoutNotifies;
    }

    public boolean isActiveModeEnabled() {
        return mActiveMode;
    }

    public boolean isActiveModeWithoutNotifiesEnabled() {
        return mActiveModeWithoutNotifies;
    }

    /**
     * @return {@code true} if you may always listen to every device's sensors while
     * it's charging, {@code false} otherwise.
     */
    public boolean isActiveModeActiveChargingEnabled() {
        return mActiveModeActiveCharging;
    }

    public boolean isEnabledOnlyWhileCharging() {
        return mEnabledOnlyWhileCharging;
    }

    public boolean isNotifyWakingUp() {
        return mNotifyWakeUpOn;
    }

    public boolean isWallpaperShown() {
        return mUiWallpaper;
    }

    public boolean isStatusBatterySticky() {
        return mUiBatterySticky;
    }

    public boolean isWidgetPinnable() {
        return mFeelWidgetPinnable;
    }

    public boolean isWidgetReadable() {
        return mFeelWidgetReadable;
    }

    public boolean isInactiveTimeEnabled() {
        return mInactiveTimeEnabled;
    }

    public boolean isFullScreen() {
        return mUiFullScreen;
    }

    public boolean isOverridingFontsEnabled() {
        return mUiOverrideFonts;
    }

    public boolean isEmoticonsEnabled() {
        return mUiEmoticons;
    }

    public boolean isScreenOffAfterLastWidget() {
        return mScreenOffAfterLastNotify;
    }

    public boolean isDoubleTapToSleepEnabled() {
        return mDoubleTapToSleep;
    }

    public boolean isUnlockAnimationEnabled() {
        return mUiUnlockAnimation;
    }

    public boolean isDevSensorsDumpEnabled() {
        return mDevSensorsDump;
    }

    // //////////////////////////////////////////
    // //////////// -- TRIGGERS -- //////////////
    // //////////////////////////////////////////

    /**
     * Contains
     *
     * @author Artem Chepurnoy
     */
    public class Triggers {

        public void setPreviousVersion(@NonNull Context context, int versionCode,
                                       @Nullable OnConfigChangedListener listener) {
            writeFromMain(context, getOption(KEY_TRIG_PREVIOUS_VERSION), versionCode, listener);
        }

        public void setHelpRead(@NonNull Context context, boolean isRead,
                                @Nullable OnConfigChangedListener listener) {
            writeFromMain(context, getOption(KEY_TRIG_HELP_READ), isRead, listener);
        }

        public void setDonationAsked(@NonNull Context context, boolean isAsked,
                                     @Nullable OnConfigChangedListener listener) {
            writeFromMain(context, getOption(KEY_TRIG_DONATION_ASKED), isAsked, listener);
        }

        public void setTranslated(@NonNull Context context, boolean translated,
                                  @Nullable OnConfigChangedListener listener) {
            writeFromMain(context, getOption(KEY_TRIG_TRANSLATED), translated, listener);
        }

        /**
         * @see #setLaunchCount(android.content.Context, int, com.bullmobi.base.content.ConfigBase.OnConfigChangedListener)
         * @see #getLaunchCount()
         */
        public void incrementLaunchCount(@NonNull Context context,
                                         @Nullable OnConfigChangedListener listener) {
            setLaunchCount(context, getLaunchCount() + 1, listener);
        }

        /**
         * @see #incrementLaunchCount(android.content.Context, com.bullmobi.base.content.ConfigBase.OnConfigChangedListener)
         * @see #getLaunchCount()
         */
        public void setLaunchCount(@NonNull Context context, int launchCount,
                                   @Nullable OnConfigChangedListener listener) {
            writeFromMain(context, getOption(KEY_TRIG_LAUNCH_COUNT), launchCount, listener);
        }

        /**
         * As set by {@link com.bullmobi.message.ui.activities.MainActivity}, it returns version
         * code of previously installed EasyNotification, {@code 0} if first install.
         *
         * @return version code of previously installed EasyNotification, {@code 0} on first installation.
         * @see #setPreviousVersion(android.content.Context, int, Config.OnConfigChangedListener)
         */
        public int getPreviousVersion() {
            return mTrigPreviousVersion;
        }

        /**
         * @return the number of {@link com.bullmobi.message.ui.activities.EasyNotificationActivity}'s creations.
         * @see #incrementLaunchCount(android.content.Context, com.bullmobi.base.content.ConfigBase.OnConfigChangedListener)
         * @see #setLaunchCount(android.content.Context, int, com.bullmobi.base.content.ConfigBase.OnConfigChangedListener)
         */
        public int getLaunchCount() {
            return mTrigLaunchCount;
        }

        /**
         * @return {@code true} if {@link com.bullmobi.base.ui.fragments.dialogs.HelpDialog} been read,
         * {@code false} otherwise
         * @see #setHelpRead(android.content.Context, boolean, Config.OnConfigChangedListener)
         */
        public boolean isHelpRead() {
            return mTrigHelpRead;
        }

        /**
         * @return {@code true} if the app is fully translated to currently used locale,
         * {@code false} otherwise.
         * @see #setDonationAsked(android.content.Context, boolean, com.bullmobi.base.content.ConfigBase.OnConfigChangedListener)
         */
        public boolean isTranslated() {
            return mTrigTranslated;
        }

        public boolean isDonationAsked() {
            return mTrigDonationAsked;
        }

    }

}
