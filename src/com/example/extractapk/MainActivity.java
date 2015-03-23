/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.example.extractapk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * This Activity is intended to Extract the .apk files to sdcard folder
 * 
 * @author DIVA.
 * 
 */
public class MainActivity extends Activity {

    /* Tag used for logging */
    private static final String TAG = MainActivity.class.getSimpleName();

    /* List of packages installed on device */
    private List mPkgAppsList;

    /* path of all apks in sdcard */
    private final String PATH_TO_EXTRACT = "/EXTRACT_APK";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mPkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        // extract all apks in asynctask
        new ExtractApks().execute();
    }

    /**
     * 
     * AynsTask to extract apks to sdcard
     */
    class ExtractApks extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            extractApk();
            return null;
        }

    }

    /**
     * Extract Apk to "EXTRACT_APK" folder in sdcard
     */
    private void extractApk() {
        int z = 0;
        for (Object object : mPkgAppsList) {
            ResolveInfo info = (ResolveInfo) object;
            File f1 = new File(
                    info.activityInfo.applicationInfo.publicSourceDir);
            Log.d(TAG,
                    f1.getName().toString() + "----"
                            + info.loadLabel(getPackageManager()));
            try {
                String file_name = info.loadLabel(getPackageManager())
                        .toString();
                Log.d(TAG, file_name.toString());
                File f2 = new File(Environment.getExternalStorageDirectory()
                        .toString() + PATH_TO_EXTRACT);
                f2.mkdirs();
                f2 = new File(f2.getPath() + "/" + file_name + ".apk");
                f2.createNewFile();
                InputStream in = new FileInputStream(f1);
                OutputStream out = new FileOutputStream(f2);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.d(TAG, "File copied.");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
