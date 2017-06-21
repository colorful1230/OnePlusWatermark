package com.test.onepluswatermark.edit;

import android.graphics.Bitmap;
import android.net.Uri;

import com.test.onepluswatermark.BasePresenter;
import com.test.onepluswatermark.BaseView;
import com.test.onepluswatermark.data.ImageInfo;

/**
 * Created by zhaolin on 17-6-11.
 */

public interface EditContract {

    interface Presenter extends BasePresenter {

        void saveImage();

        void showImage(ImageInfo imageInfo);

    }

    interface View extends BaseView<Presenter> {

        void showImage(Uri uri);

        void showSaveTip();

        void showSaveSuccess(String path);

        void showNotEditTip();

        Bitmap getBitmap();
    }
}
