/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.uamp.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.uamp.R;
import com.example.android.uamp.utils.LogHelper;

/**
 * Placeholder activity for features that are not implemented in this sample, but
 * are in the navigation drawer.
 */
public class MainMenuActivity extends ActionBarCastActivity {
    private static final String TAG = LogHelper.makeLogTag(MainMenuActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogHelper.w(TAG, "Testing Joe");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initializeToolbar();
        setTitle("CMC Library");
        //setContentView(R.layout.activity_placeholder);

        String[] mainMenuArray = {"", "Catalog", "Calendar", "Contacts",
                };

        ListView lv = (ListView) findViewById(R.id.MainMenuList);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.main_menu_list_item, R.id.menu_item, mainMenuArray));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView< ?> parent, View view, int position, long id) {

                String actressName = ((TextView) view.findViewById(R.id.menu_item)).getText().toString();

                Toast.makeText(getApplicationContext(), actressName, Toast.LENGTH_SHORT).show();
                position += -1;
                if (position >= 0) {
                    Bundle extras = ActivityOptions.makeCustomAnimation(
                            MainMenuActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                    Class activityClass = mDrawerMenuContents.getActivity(position);
                    startActivity(new Intent(MainMenuActivity.this, activityClass), extras);
                    finish();
                }

            }
        });


    }

}

