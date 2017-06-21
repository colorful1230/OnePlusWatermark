package com.test.onepluswatermark.data;

import android.net.Uri;

/**
 * Created by zhaolin on 17-6-11.
 */

public class ImageInfo {

    private Uri mUri;

    public ImageInfo(Uri mUri) {
        this.mUri = mUri;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }
}
