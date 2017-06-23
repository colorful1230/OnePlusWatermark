package com.test.onepluswatermark.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private static final int BASE_TEXT_SIZE = 320;

    private ImageView mEditImageView;

    private EditContract.Presenter mPresenter;

    private Toast mSavingToast;

    private Bitmap mEditBitmap;

    private Paint mPaint;

    private String mDeviceMode;

    private Context mContext;

    public static EditFragment newInstance() {
        return new EditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmment_edit, container, false);
        mEditImageView = (ImageView) root.findViewById(R.id.edit_image_view);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setAlpha((int) (255 * 1.0f * 0.7));
        mPaint.setColor(Color.WHITE);

        mDeviceMode = Build.DEVICE;
        mContext = getContext();
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
        if (mEditBitmap != null) {
            addWatermark(mEditBitmap);
        }
    }


    @Override
    public void showSaveTip() {
        if (mSavingToast == null && mContext != null) {
            mSavingToast = Toast.makeText(mContext, mContext.getResources().getString(R.string.edit_saving),
                    Toast.LENGTH_SHORT);
            mSavingToast.show();
        }
    }

    @Override
    public void showSaveSuccess(String path) {
        if (mSavingToast != null) {
            mSavingToast.cancel();
        }
        if (mContext != null) {
            Toast.makeText(mContext, getActivity().getResources().getString(R.string.edit_saved) + path,
                    Toast.LENGTH_SHORT).show();
        }
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

    private void addWatermark(@NonNull Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_icon);

        int width = bitmap.getWidth() / 20;
        int margin = width / 3;
        float scale = width * 1.0f / watermark.getWidth();
        Bitmap scaleBitmap = ThumbnailUtils.extractThumbnail(watermark, (int)(watermark.getWidth() * scale),
                (int)(watermark.getHeight() * scale));
        canvas.drawBitmap(scaleBitmap, margin, bitmap.getHeight() - scaleBitmap.getHeight() - margin, mPaint);
        mPaint.setTextSize(BASE_TEXT_SIZE * scale);
        Rect rect = new Rect();
        mPaint.getTextBounds(mDeviceMode, 0, mDeviceMode.length(), rect);
        canvas.drawText(mDeviceMode, margin * 2 + scaleBitmap.getWidth(),
                bitmap.getHeight() - margin - scaleBitmap.getHeight() / 2 + rect.height() / 2, mPaint);
        mEditImageView.setImageBitmap(bitmap);
    }
}
