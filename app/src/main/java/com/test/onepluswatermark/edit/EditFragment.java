package com.test.onepluswatermark.edit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.onepluswatermark.R;
import com.test.onepluswatermark.utils.FileUtils;

/**
 * Created by zhaolin on 17-6-11.
 */

public class EditFragment extends Fragment implements EditContract.View {

    private static final String TAG = "EditFragment";

    private ImageView mEditImageView;

    private EditContract.Presenter mPresenter;

    private Toast mSavingToast;

    private Bitmap mEditBitmap;

    public static EditFragment newInstance() {
        return new EditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmment_edit, container, false);
        mEditImageView = (ImageView) root.findViewById(R.id.edit_image_view);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setPresenter(EditContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showImage(Uri uri) {
        mEditImageView.setVisibility(View.VISIBLE);
        mEditImageView.setImageURI(uri);
        String path = FileUtils.getRealFilePath(getContext(), uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inSampleSize = 1;
        mEditBitmap = BitmapFactory.decodeFile(path, options);
        addWatermark();
    }


    @Override
    public void showSaveTip() {
        if (mSavingToast == null) {
            mSavingToast = Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.edit_saving),
                    Toast.LENGTH_SHORT);
        }
        mSavingToast.show();
    }

    @Override
    public void showSaveSuccess(String path) {
        if (mSavingToast != null) {
            mSavingToast.cancel();
        }
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.edit_saved) + path,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotEditTip() {
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.edit_not_edit),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public Bitmap getBitmap() {
        return mEditBitmap;
    }

    private void addWatermark() {
        Canvas canvas = new Canvas(mEditBitmap);
        Paint paint = new Paint();
        Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        int width = mEditBitmap.getWidth() / 3;
        float scale = width * 1.0f / mEditBitmap.getWidth();
        Bitmap scaleBitmap = ThumbnailUtils.extractThumbnail(watermark, (int)(watermark.getWidth() * scale),
                (int)(watermark.getHeight() * scale));
        canvas.drawBitmap(scaleBitmap, 0, mEditBitmap.getHeight() - scaleBitmap.getHeight(), paint);
        mEditImageView.setImageBitmap(mEditBitmap);
    }
}
