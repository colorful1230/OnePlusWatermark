package com.test.onepluswatermark.edit;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.test.onepluswatermark.data.ImageInfo;
import com.test.onepluswatermark.utils.FileUtils;

import permissions.dispatcher.NeedsPermission;

/**
 * Created by zhaolin on 17-6-11.
 */

public class EditPresenter implements EditContract.Presenter {

    private EditContract.View mView;

    public EditPresenter(EditContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Override
    public void saveImage(Context context) {
        new SaveImageTask(context).execute();
    }

    @Override
    public void showImage(ImageInfo imageInfo) {
        mView.showImage(imageInfo.getUri());
    }

    @Override
    public void showImage(String path) {
        mView.showImage(path);
    }

    private class SaveImageTask extends AsyncTask<Void, Void, String> {

        private Context context;

        public SaveImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (mView == null) {
                return null;
            }

            Bitmap bitmap = mView.getBitmap();
            if (bitmap == null) {
                return null;
            }

            String path = FileUtils.saveBitmap(context, bitmap);
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            return path;

        }

        @Override
        protected void onPostExecute(String s) {
            if (mView != null) {
                if (TextUtils.isEmpty(s)) {
                    mView.showNotEditTip();
                } else {
                    mView.showSaveSuccess(s);
                }
            }
        }
    }
}
