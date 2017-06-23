package com.test.onepluswatermark.edit;

import android.content.Context;
import android.content.DialogInterface;
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
import com.test.onepluswatermark.utils.FileUtils;

/**
 * Created by zhaolin on 17-6-11.
 */

public class EditFragment extends Fragment implements EditContract.View, View.OnClickListener {

    private static final String TAG = "EditFragment";

    private static final String KEY_EDIT_TAG = "tag";

    private static final int BASE_TEXT_SIZE = 340;

    private ImageView mEditImageView;

    private FloatingActionButton mEditButton;

    private EditContract.Presenter mPresenter;

    private Toast mSavingToast;

    private Bitmap mEditBitmap;

    private Bitmap mCanvasBitmap;

    private Paint mPaint;

    private String mDeviceMode;

    private Context mContext;

    private EditText mTagEditText;

    public static EditFragment newInstance() {
        return new EditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmment_edit, container, false);
        mEditImageView = (ImageView) root.findViewById(R.id.edit_image_view);
        mEditButton = (FloatingActionButton) root.findViewById(R.id.edit_edit_button);
        mEditButton.setOnClickListener(this);

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
        mEditButton.setVisibility(View.VISIBLE);
        String path = FileUtils.getRealFilePath(getContext(), uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inSampleSize = 1;
        mEditBitmap = BitmapFactory.decodeFile(path, options);
        mCanvasBitmap = Bitmap.createBitmap(mEditBitmap, 0, 0, mEditBitmap.getWidth(),
                mEditBitmap.getHeight());
        addWatermark();
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
            Toast.makeText(mContext, mContext.getString(R.string.edit_saved) + path,
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
        Canvas canvas = new Canvas(mCanvasBitmap);
        Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_icon);
        canvas.drawBitmap(mEditBitmap, 0, 0, mPaint);
        int width = mCanvasBitmap.getWidth() / 16;
        int margin = width / 3;
        float scale = width * 1.0f / watermark.getWidth();
        Bitmap scaleBitmap = ThumbnailUtils.extractThumbnail(watermark, (int)(watermark.getWidth() * scale),
                (int)(watermark.getHeight() * scale));
        canvas.drawBitmap(scaleBitmap, margin, mCanvasBitmap.getHeight() - scaleBitmap.getHeight() - margin, mPaint);
        mPaint.setTextSize(BASE_TEXT_SIZE * scale);
        Rect rect = new Rect();
        mPaint.getTextBounds(mDeviceMode, 0, mDeviceMode.length(), rect);
        mPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(mDeviceMode, margin * 2 + scaleBitmap.getWidth(),
                mCanvasBitmap.getHeight() - margin - scaleBitmap.getHeight() / 2 + rect.height() / 2, mPaint);
        mEditImageView.setImageBitmap(mCanvasBitmap);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit_edit_button) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_edit_tag, null);
            mTagEditText = (EditText) view.findViewById(R.id.dialog_edit_text);
            mTagEditText.setHint(Build.DEVICE);
            if (!Build.DEVICE.equalsIgnoreCase(mDeviceMode)) {
                mTagEditText.setText(mDeviceMode);
            }
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_save_checkbox);

            new AlertDialog.Builder(mContext, R.style.Dialog).setView(view)
                    .setTitle(getResources().getString(R.string.edit_dialog_title))
                    .setNegativeButton(getResources().getString(R.string.edit_dialog_cancel),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setPositiveButton(getResources().getString(R.string.edit_dialog_save),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String tag = mTagEditText.getText().toString();
                            if (checkBox.isChecked() && !Build.DEVICE.equalsIgnoreCase(tag)) {
                                FileUtils.writeSharePreference(getContext(), KEY_EDIT_TAG, tag);
                            }
                            mDeviceMode = TextUtils.isEmpty(tag) ? Build.DEVICE : tag;
                            addWatermark();
                        }
                    }).create().show();
        }
    }

}
