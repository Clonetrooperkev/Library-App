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
import com.example.android.uamp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawerMenuContents {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_ICON = "icon";

    private final ArrayList<Map<String, ?>> items;
    private final Class[] activities;

    public DrawerMenuContents() {
        activities = new Class[3];
        items = new ArrayList<>(3);

        activities[0] = MainCatalogActivity.class;
        items.add(populateDrawerItem("Catalog",
            R.drawable.ic_search_category_default));
        activities[1] = NewCalendarActivity.class;
        items.add(populateDrawerItem("Calendar",
            R.drawable.ic_menu_my_calendar));
        activities[2] = MainContactsActivity.class;
        items.add(populateDrawerItem("Contacts",
                R.drawable.ic_menu_info_details));
    }

    public List<Map<String, ?>> getItems() {
        return items;
    }

    public Class getActivity(int position) {
        return activities[position];
    }

    public int getPosition(Class activityClass) {
        for (int i=0; i<activities.length; i++) {
            if (activities[i].equals(activityClass)) {
                return i;
            }
        }
        return -1;
    }

    private Map<String, ?> populateDrawerItem(String title, int icon) {
        HashMap<String, Object> item = new HashMap<>();
        item.put(FIELD_TITLE, title);
        item.put(FIELD_ICON, icon);
        return item;
    }
}
