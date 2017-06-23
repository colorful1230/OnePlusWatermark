package com.test.onepluswatermark.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaolin on 17-6-11.
 */

public class FileUtils {

    private static final String FILE_NAME_PREFIX = "watermark_";

    private static final String FILE_NAME_SUFFIX = ".jpeg";

    private static final String SP_FILE_NAME = "pref";

    @Nullable
    public static String saveBitmap(Context context, Bitmap bitmap) {
        String saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + File.separator + "Watermark";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String date = dateFormat.format(new Date());

        String name = saveDir + File.separator + FILE_NAME_PREFIX + date + FILE_NAME_SUFFIX;
        File file = new File(name);
        if (file != null) {
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                outputStream.flush();
                MediaStore.Images.Media.insertImage(context.getContentResolver(), name, "", "");
                return file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (Exception ignored) {

                }
            }

        }
        return null;
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static void writeSharePreference(Context context, String key, String value) {
        if (context == null) {
            return;
        }

        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Nullable
    public static String readSharePreference(Context context, String key) {
        if (context == null) {
            return null;
        }

        if (TextUtils.isEmpty(key)) {
            return null;
        }

        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }
}
