/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.server.lights;

import android.hardware.light.V2_0.Constants.Type;
import android.content.Context;

public class LightsManager {

    public static final int LIGHT_ID_BACKLIGHT = Type.BACKLIGHT;
    public static final int LIGHT_ID_KEYBOARD = Type.KEYBOARD;
    public static final int LIGHT_ID_BUTTONS = Type.BUTTONS;
    public static final int LIGHT_ID_BATTERY = Type.BATTERY;
    public static final int LIGHT_ID_NOTIFICATIONS = Type.NOTIFICATIONS;
    public static final int LIGHT_ID_ATTENTION = Type.ATTENTION;
    public static final int LIGHT_ID_BLUETOOTH = Type.BLUETOOTH;
    public static final int LIGHT_ID_WIFI = Type.WIFI;
    public static final int LIGHT_ID_COUNT = Type.COUNT;
    
    final Light mLights[] = new Light[LIGHT_ID_COUNT];
    private final Context mContext;

    public LightsManager(Context context) {
        mContext = context;
        for (int i = 0; i < LIGHT_ID_COUNT; i++) {
            mLights[i] = new Light(i, context);
        }
    }
    
    public Light getLight(int id) {
            if (0 <= id && id < LIGHT_ID_COUNT) {
                return mLights[id];
            } else {
                return null;
            }
        }
}
