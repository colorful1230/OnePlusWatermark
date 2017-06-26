package com.test.onepluswatermark.edit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.onepluswatermark.R;
import com.test.onepluswatermark.utils.Constants;
import com.test.onepluswatermark.utils.FileUtils;

/**
 * Created by zhaolin on 17-6-11.
 */

public class EditFragment extends Fragment implements EditContract.View {

    private static final String TAG = "EditFragment";

    private static final String KEY_EDIT_TAG = "tag";

    private static final int BASE_TEXT_SIZE = 340;

    private ImageView mEditImageView;

    private EditContract.Presenter mPresenter;

    private Toast mSavingToast;

    private Bitmap mEditBitmap;

    private Bitmap mCanvasBitmap;

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

        mContext = getContext();
        mDeviceMode = FileUtils.readSharePreference(mContext, KEY_EDIT_TAG);
        if (TextUtils.isEmpty(mDeviceMode)) {
            mDeviceMode = Build.DEVICE;
        }

        return root;
    }

    @Override
    public void setPresenter(EditContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showImage(Uri uri) {
        mEditImageView.setVisibility(View.VISIBLE);
        String path = FileUtils.getRealFilePath(getContext(), uri);
        showImage(path);
    }

    @Override
    public void showImage(String path) {
        mEditImageView.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inSampleSize = 1;
        try {
            mEditBitmap = BitmapFactory.decodeFile(path, options);
            mCanvasBitmap = Bitmap.createBitmap(mEditBitmap, 0, 0, mEditBitmap.getWidth(),
                    mEditBitmap.getHeight());
            addWatermark();
        } catch (Throwable ignore) {
            if (mContext != null) {
                Toast.makeText(mContext, getString(R.string.edit_load_file_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getSetting();
        addWatermark();
    }

    @Override
    public void showSaveTip() {
        if (mSavingToast == null && mContext != null) {
            mSavingToast = Toast.makeText(mContext, getString(R.string.edit_saving),
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
            Toast.makeText(mContext, getString(R.string.edit_saved) + path,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showNotEditTip() {
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.edit_not_edit),
                Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public Bitmap getBitmap() {
        return mCanvasBitmap;
    }

    private void addWatermark() {
        if (mCanvasBitmap == null || mEditBitmap == null || mPaint == null) {
            return;
        }
        Canvas canvas = new Canvas(mCanvasBitmap);
        Bitmap watermark = null;
        try {
            watermark = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_icon);
        } catch (Throwable ignore) {

        }

        if (watermark != null) {

            int width = mCanvasBitmap.getWidth() / 16;
            int margin = width / 3;
            float scale = width * 1.0f / watermark.getWidth();

            Bitmap scaleBitmap = ThumbnailUtils.extractThumbnail(watermark, (int) (watermark.getWidth() * scale),
                    (int) (watermark.getHeight() * scale));
            mPaint.setTextSize(BASE_TEXT_SIZE * scale);
            Rect rect = new Rect();
            mPaint.getTextBounds(mDeviceMode, 0, mDeviceMode.length(), rect);
            mPaint.setTextAlign(Paint.Align.LEFT);

            canvas.drawBitmap(mEditBitmap, 0, 0, mPaint);
            if (mDoubleLine) {
                canvas.drawBitmap(scaleBitmap, margin, mCanvasBitmap.getHeight() - scaleBitmap.getHeight() - margin, mPaint);
                canvas.drawText(mTitle, margin * 2 + scaleBitmap.getWidth(),
                        mCanvasBitmap.getHeight() - margin - scaleBitmap.getHeight(), mPaint);
                canvas.drawText(mSubtitle, margin * 2 + scaleBitmap.getWidth(),
                        mCanvasBitmap.getHeight() - scaleBitmap.getHeight() / 2 + rect.height() / 2, mPaint);
            } else {
                canvas.drawBitmap(scaleBitmap, margin, mCanvasBitmap.getHeight() - scaleBitmap.getHeight() - margin, mPaint);
                canvas.drawText(mTitle, margin * 2 + scaleBitmap.getWidth(),
                        mCanvasBitmap.getHeight() - margin - scaleBitmap.getHeight() / 2 + rect.height() / 2, mPaint);
            }
            mEditImageView.setImageBitmap(mCanvasBitmap);
        }
    }

    private boolean mDoubleLine;

    private String mTitle;

    private String mSubtitle;

    private void getSetting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDoubleLine = sp.getBoolean(Constants.KEY_DOUBLE_LINE, false);
        mTitle = sp.getString(Constants.KEY_TITLE, Build.DEVICE);
        mSubtitle = sp.getString(Constants.KEY_SUBTITLE, "");
    }

}
