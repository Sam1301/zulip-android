package com.zulip.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class contains helpers functions for photo uploads used by
 * {@link com.zulip.android.activities.PhotoEditActivity} and
 * {@link com.zulip.android.activities.PhotoSendActivity}
 */

public class PhotoHelper {

    /**
     * This function is used to decode a scaled Image. Based on {@param rotate}
     * it rotates the bitmap before setting it as source of {@param ImageView}
     *
     * @param imageView on which the bitmap formed is set
     * @param photoPath file path of captured image
     */
    public static void setPic(ImageView imageView, String photoPath, boolean rotate) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

        if (rotate) {
            // rotate bitmap by 90 degrees
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = null;
            if (bitmap != null) {
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }

            imageView.setImageBitmap(rotatedBitmap);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * From http://stackoverflow.com/a/26930938/5334314
     * Returns the actual bitmap position in an imageView.
     *
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH) / 2;
        int left = (int) (imgViewW - actW) / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

    /**
     * Function to delete the file at {@param photoPath} and store {@param bitmap}
     * at {@param photoPath}.
     *
     * @param photoPath
     * @param bitmap
     */
    public static void saveBitmapAsFile(String photoPath, Bitmap bitmap) {
        // delete old bitmap
        File file = new File(photoPath);
        file.delete();

        // store new bitmap at mPhotoPath file path
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(photoPath);
            // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            ZLog.logException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                ZLog.logException(e);
            }
        }
    }
}
