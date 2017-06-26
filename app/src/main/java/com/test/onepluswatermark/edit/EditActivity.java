package com.test.onepluswatermark.edit;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tencent.bugly.crashreport.CrashReport;
import com.test.onepluswatermark.R;
import com.test.onepluswatermark.about.AboutActivity;
import com.test.onepluswatermark.data.ImageInfo;
import com.test.onepluswatermark.preference.SettingActivity;
import com.test.onepluswatermark.utils.ActivityUtils;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 1;

    private static final int REQUEST_CODE_CAPTURE = 2;

    private EditContract.Presenter mPresenter;

    private Toolbar mToolbar;

    private FloatingActionButton mCameraButton;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashReport.initCrashReport(this);

        setContentView(R.layout.activity_edit);
        initToolbar();

        mCameraButton = (FloatingActionButton) findViewById(R.id.edit_camera_button);
        mCameraButton.setOnClickListener(this);

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
        Intent intent = null;
        switch (itemId) {
            case R.id.edit_save_item:
                mPresenter.saveImage(getApplicationContext());
                break;
            case R.id.edit_open_item:
                EditActivityPermissionsDispatcher.gotoMatisseWithCheck(this);
                break;
            case R.id.edit_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);

            if (requestCode == REQUEST_CODE_CHOOSE) {
                Uri uri = data.getData();
                if (mPresenter != null) {
                    ImageInfo imageInfo = new ImageInfo(uri);
                    mPresenter.showImage(imageInfo);
                }
            } else if (requestCode == REQUEST_CODE_CAPTURE) {
                ImageInfo imageInfo = new ImageInfo(mImageUri);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit_camera_button) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (mImageUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE);
        }
    }
}
