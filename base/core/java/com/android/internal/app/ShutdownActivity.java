/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.internal.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;

public class ShutdownActivity extends Activity {

    private static final String TAG = "ShutdownActivity";
    private boolean mReboot;
    private boolean mConfirm;
    private boolean mUserRequested;
	//add by xxf
    private String mReason;
	//add by xxf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mReboot = Intent.ACTION_REBOOT.equals(intent.getAction());
		//add by xxf
        mReason = (String)intent.getExtra(Intent.EXTRA_REASON,null);
		//add by xxf
        mConfirm = intent.getBooleanExtra(Intent.EXTRA_KEY_CONFIRM, false);
        mUserRequested = intent.getBooleanExtra(Intent.EXTRA_USER_REQUESTED_SHUTDOWN, false);
        Slog.i(TAG, "onCreate(): confirm=" + mConfirm);

        Thread thr = new Thread("ShutdownActivity") {
            @Override
            public void run() {
                IPowerManager pm = IPowerManager.Stub.asInterface(
                        ServiceManager.getService(Context.POWER_SERVICE));
                try {
                    if (mReboot) {
					//add by xxf
                        pm.reboot(mConfirm, mReason, false);
						//add by xxf
                    } else {
                        pm.shutdown(mConfirm,
                                    mUserRequested ? PowerManager.SHUTDOWN_USER_REQUESTED : null,
                                    false);
                    }
                } catch (RemoteException e) {
                }
            }
        };
        thr.start();
        finish();
        // Wait for us to tell the power manager to shutdown.
        try {
            thr.join();
        } catch (InterruptedException e) {
        }
    }
}
