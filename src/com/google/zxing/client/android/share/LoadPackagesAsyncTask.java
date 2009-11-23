/*
 * Copyright (C) 2009 ZXing authors
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

package com.google.zxing.client.android.share;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class LoadPackagesAsyncTask extends AsyncTask<List<String[]>,Void,List<String[]>> {

  private final AppPickerActivity appPickerActivity;

  LoadPackagesAsyncTask(AppPickerActivity appPickerActivity) {
    this.appPickerActivity = appPickerActivity;
  }

  @Override
  protected List<String[]> doInBackground(List<String[]>... objects) {
    List<String[]> labelsPackages = objects[0];
    PackageManager packageManager = appPickerActivity.getPackageManager();
    List<ApplicationInfo> appInfos = packageManager.getInstalledApplications(0);
    for (ApplicationInfo appInfo : appInfos) {
      CharSequence label = appInfo.loadLabel(packageManager);
      if (label != null) {
        String packageName = appInfo.packageName;
        if (!isHidden(packageName)) {
          labelsPackages.add(new String[]{label.toString(), packageName});
        }
      }
    }
    Collections.sort(labelsPackages, new Comparator<String[]>() {
      public int compare(String[] o1, String[] o2) {
        return o1[0].compareTo(o2[0]);
      }
    });
    return labelsPackages;
  }

  private static boolean isHidden(String packageName) {
    return packageName == null ||
        packageName.startsWith("com.android.") ||
        (packageName.startsWith("com.google.android.") &&
         !packageName.startsWith("com.google.android.apps."));
  }

  @Override
  protected void onPostExecute(List<String[]> results) {
    List<String> labels = new ArrayList<String>(results.size());
    for (String[] result : results) {
      labels.add(result[0]);
    }
    ListAdapter listAdapter = new ArrayAdapter<String>(
        appPickerActivity, android.R.layout.simple_list_item_1, labels);
    appPickerActivity.setListAdapter(listAdapter);
    appPickerActivity.getProgressDialog().dismiss();
  }

}
