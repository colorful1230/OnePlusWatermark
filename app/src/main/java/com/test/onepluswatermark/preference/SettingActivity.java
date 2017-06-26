package com.test.onepluswatermark.preference;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.test.onepluswatermark.R;

/**
 * Created by zhaolin on 17-6-26.
 */

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SettingFragment settingFragment = SettingFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.setting_content, settingFragment).commit();
    }
}
