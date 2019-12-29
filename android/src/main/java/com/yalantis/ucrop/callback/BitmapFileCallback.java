package com.yalantis.ucrop.callback;

import android.net.Uri;

public interface BitmapFileCallback {
    void onBitmapCropped(Uri uri);

    void onCropFailure(Throwable t);
}
