/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settingslib.deviceinfo;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;

import com.android.settingslib.R;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.telephony.TelephonyManager;

/**
 * Preference controller for bluetooth address
 */
public abstract class AbstractBluetoothAddressPreferenceController
        extends AbstractConnectivityPreferenceController {

    @VisibleForTesting
    static final String KEY_BT_ADDRESS = "bt_address";

    private static final String[] CONNECTIVITY_INTENTS = {
            BluetoothAdapter.ACTION_STATE_CHANGED
    };

    private Preference mBtAddress;

    private Context mContext;
    public AbstractBluetoothAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        mContext=context;
    }

    @Override
    public boolean isAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_BT_ADDRESS;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mBtAddress = screen.findPreference(KEY_BT_ADDRESS);
        updateConnectivity();
    }

    @Override
    protected String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    @SuppressLint("HardwareIds")
    @Override
    protected void updateConnectivity() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null && mBtAddress != null) {
            //String address = bluetooth.isEnabled() ? bluetooth.getAddress() : null;
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String address = telephonyManager.getBtAddr();
            if (!TextUtils.isEmpty(address)) {
                // Convert the address to lowercase for consistency with the wifi MAC address.
                mBtAddress.setSummary(address.toLowerCase());
            } else {
                mBtAddress.setSummary(R.string.status_unavailable);
            }
        }
    }
}
