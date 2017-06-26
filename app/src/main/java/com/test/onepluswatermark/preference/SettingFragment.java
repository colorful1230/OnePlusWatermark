package com.test.onepluswatermark.preference;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.test.onepluswatermark.R;
import com.test.onepluswatermark.utils.Constants;

/**
 * Created by zhaolin on 17-6-26.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingFragment";

    private CheckBoxPreference mDoubleLineCheckbox;

    private EditTextPreference mTitleEditText;

    private EditTextPreference mSubtitleEditText;

    private SharedPreferences mSharedPreferences;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mDoubleLineCheckbox = (CheckBoxPreference) findPreference(Constants.KEY_DOUBLE_LINE);
        boolean check = mDoubleLineCheckbox.isChecked();
        mDoubleLineCheckbox.setChecked(check);
        mTitleEditText = (EditTextPreference) findPreference(Constants.KEY_TITLE);

        String title = mTitleEditText.getText();
        if (TextUtils.isEmpty(title)) {
            title = Build.DEVICE;
        }
        mTitleEditText.setSummary(title);
        mSubtitleEditText = (EditTextPreference) findPreference(Constants.KEY_SUBTITLE);
        mSubtitleEditText.setSummary(mSubtitleEditText.getText());

        if (!check) {
            mSubtitleEditText.setEnabled(false);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: " + key);
        if (key.equalsIgnoreCase(Constants.KEY_DOUBLE_LINE)) {
            boolean checked = mDoubleLineCheckbox.isChecked();
            mSubtitleEditText.setEnabled(checked);
        } else if (key.equalsIgnoreCase(Constants.KEY_TITLE)) {
            mTitleEditText.setSummary(mTitleEditText.getText());
        } else if (key.equalsIgnoreCase(Constants.KEY_SUBTITLE)) {
            mSubtitleEditText.setSummary(mSubtitleEditText.getText());
        }
    }
}
