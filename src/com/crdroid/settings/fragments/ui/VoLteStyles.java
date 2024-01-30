/*
 * Copyright (C) 2023 crDroid Android Project
 * Copyright (C) 2023 AlphaDroid
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

package com.crdroid.settings.fragments.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.text.TextUtils;
import androidx.preference.PreferenceViewHolder;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.bumptech.glide.Glide;

import com.android.internal.util.crdroid.ThemeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.json.JSONObject;
import org.json.JSONException;

public class VoLteStyles extends SettingsPreferenceFragment {

    private static final int ICON_COUNT =25;

    private RecyclerView mRecyclerView;
    private List<String> mIconImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.volte_icon_style_title);
        mIconImages = loadIconList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_view, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        Adapter mAdapter = new Adapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CRDROID_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {
        Context context;
        String mSelectedImage;
        String mAppliedImage;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_image_option, parent, false);
            CustomViewHolder vh = new CustomViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {

            int currentIconNumber = getCurrentIconNumber();

            String loadedImage = mIconImages.get(position);
            holder.iconImage.setBackgroundDrawable(getDrawable(holder.iconImage.getContext(), loadedImage));

            if (currentIconNumber == (position)) {
                mAppliedImage = loadedImage;
                if (mSelectedImage == null) {
                    mSelectedImage = loadedImage;
                }
            }

            holder.itemView.setActivated(loadedImage == mSelectedImage);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentResolver resolver = getContext().getContentResolver();
                    updateActivatedStatus(mSelectedImage, false);
                    updateActivatedStatus(loadedImage, true);
                    mSelectedImage = loadedImage;
                    Settings.System.putIntForUser(resolver,
                            Settings.System.VOLTE_ICON_STYLE, position,
                            UserHandle.USER_CURRENT);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ICON_COUNT;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImage;

            public CustomViewHolder(View itemView) {
                super(itemView);
                iconImage = (ImageView) itemView.findViewById(R.id.icon_image);
            }
        }

        private void updateActivatedStatus(String image, boolean isActivated) {
            int index = mIconImages.indexOf(image);
            if (index < 0) {
                return;
            }
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(index);
            if (holder != null && holder.itemView != null) {
                holder.itemView.setActivated(isActivated);
            }
        }
    }

    public Drawable getDrawable(Context context, String drawableName) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(drawableName, "drawable", "com.android.settings");
        return res.getDrawable(resId);
    }

    private int getCurrentIconNumber() {
        return Settings.System.getIntForUser(getContentResolver(),
                Settings.System.VOLTE_ICON_STYLE, 0, UserHandle.USER_CURRENT);
    }

    private List<String> loadIconList() {
        List<String> iconList = new ArrayList<String>(ICON_COUNT);
        for (int i = 1; i <= ICON_COUNT; i++) {
            iconList.add("ic_volte_" + i);
        }
        return iconList;
    }
}
