package com.test.onepluswatermark.edit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tencent.bugly.crashreport.CrashReport;
import com.test.onepluswatermark.R;
import com.test.onepluswatermark.about.AboutActivity;
import com.test.onepluswatermark.data.ImageInfo;
import com.test.onepluswatermark.utils.ActivityUtils;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class EditActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;

    private EditContract.Presenter mPresenter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashReport.initCrashReport(this);

        setContentView(R.layout.activity_edit);
        initToolbar();

        EditFragment editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentById(R.id.edit_content_frame);
        if (editFragment == null) {
            editFragment = EditFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), editFragment,
                    R.id.edit_content_frame);
        }

        mPresenter = new EditPresenter(editFragment);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.edit_tool_bar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.edit_save_item:
                mPresenter.saveImage(getApplicationContext());
                break;
            case R.id.edit_open_item:
                EditActivityPermissionsDispatcher.gotoMatisseWithCheck(this);
                break;
            case R.id.edit_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);

            Uri uri = data.getData();
            if (mPresenter != null) {
                ImageInfo imageInfo = new ImageInfo(uri);
                mPresenter.showImage(imageInfo);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void gotoMatisse() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

}
