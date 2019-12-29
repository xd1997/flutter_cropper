package com.yalantis.ucrop.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.yalantis.ucrop.callback.BitmapFileCallback;
import com.yalantis.ucrop.model.CropParameters;
import com.yalantis.ucrop.task.BitmapCropTask;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.ImageHeaderParser;

import java.io.File;
import java.io.IOException;

public class BitmapFileTask extends AsyncTask<Void, Void, Throwable> {
    private final String mImageInputPath;
    private final String mImageOutputPath;
    private final Bitmap.CompressFormat mCompressFormat;
    private final BitmapFileCallback mCropCallback;

    public BitmapFileTask(String mImageInputPath, String mImageOutputPath, @NonNull Bitmap.CompressFormat cropParameters, BitmapFileCallback mCropCallback) {
        this.mImageInputPath = mImageInputPath;
        this.mImageOutputPath = mImageOutputPath;
        mCompressFormat = cropParameters;
        this.mCropCallback = mCropCallback;
    }

    @Override
    protected Throwable doInBackground(Void... voids) {
        int[] imageSize = resize();
        try {
            crop(imageSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean crop(int[] imageSize) throws IOException {
        if (imageSize[0] != imageSize[1]) {
            ExifInterface originalExif = new ExifInterface(mImageInputPath);
            int cropOffsetX = 0;
            int cropOffsetY = 0;
            int mCroppedImageWidth = 0;
            if (imageSize[0] > imageSize[1]) {
                cropOffsetX = (imageSize[0] - imageSize[1]) / 2;
                mCroppedImageWidth = imageSize[1];
            } else {
                cropOffsetY = (imageSize[1] - imageSize[0]) / 2;
                mCroppedImageWidth = imageSize[0];
            }
            boolean cropped = BitmapCropTask.cropCImg(mImageInputPath, mImageOutputPath,
                    cropOffsetX, cropOffsetY, mCroppedImageWidth, mCroppedImageWidth,
                    0, 1, mCompressFormat.ordinal(), 90,
                    0, 1);
            if (cropped && mCompressFormat.equals(Bitmap.CompressFormat.JPEG)) {
                ImageHeaderParser.copyExif(originalExif, mCroppedImageWidth, mCroppedImageWidth, mImageOutputPath);
            }
            return cropped;
        } else {
            FileUtils.copyFile(mImageInputPath, mImageOutputPath);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Throwable t) {
        if (mCropCallback != null) {
            if (t == null) {
                Uri uri = Uri.fromFile(new File(mImageOutputPath));
                mCropCallback.onBitmapCropped(uri);
            } else {
                mCropCallback.onCropFailure(t);
            }
        }
    }

    private int[] resize() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageInputPath, options);
        return new int[]{options.outWidth, options.outHeight};
    }
}
