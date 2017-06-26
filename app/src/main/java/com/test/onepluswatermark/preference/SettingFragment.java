package com.test.onepluswatermark.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.test.onepluswatermark.R;

/**
 * Created by zhaolin on 17-6-26.
 */

public class SettingFragment extends PreferenceFragment {

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
