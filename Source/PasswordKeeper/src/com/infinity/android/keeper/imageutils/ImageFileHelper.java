/**
 * 
 */
package com.infinity.android.keeper.imageutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.base.Strings;
import com.infinity.android.keeper.BaseKeeperActivity;
import com.infinity.android.keeper.R;
import com.infinity.android.keeper.utils.KeeperUtils;

/**
 * @author joshiroh
 *
 */
public final class ImageFileHelper {

    private ImageFileHelper() {
        // private constructor
    }

    /**
     * Create jpeg file for storing image
     * @param appContext
     * @return imageFile
     * @throws IOException
     */
    private File createImageFile(final BaseKeeperActivity appContext) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = appContext.getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
                );
        // Save a file: path for use with ACTION_VIEW intents
        Log.d("PHOTO PATH", "Photo File Path : " + image.getAbsolutePath());
        return image;
    }

    /**
     * Convert given bitmap in to String.
     * 
     * @param bitmap
     * @return bitmapString
     */
    public static final String convertBitmapToString(final Bitmap bitmap) {
        if (null != bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String bitmapString = Base64.encodeToString(b, Base64.DEFAULT);
            return bitmapString;
        }
        return null;
    }

    /**
     * Convert given string to bitmap
     * 
     * @param bitmapString
     * @return bitmap
     */
    public static final Bitmap convertStringToBitmap(final String encodedString) {
        if (!Strings.isNullOrEmpty(encodedString)) {
            try {
                byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch (Exception e) {
                e.getMessage();
            }
        }
        return null;
    }

    /**
     * Create resized bitmap
     * 
     * @param bitmap
     * @param newHeight
     * @param newWidth
     * @return resizedBitmap
     */
    public static final Bitmap getResizedBitmap(final Bitmap bm, final int newHeight, final int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    /**
     * Load thumb nail image in the given image view with fixed dimensions
     * @param appContext
     * @param imageView
     * @param bitmap
     */
    public static final void loadThumbNail(final BaseKeeperActivity appContext, final ImageView imageView, final Bitmap bitmap) {
        if(null != bitmap && null != imageView) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(appContext.getResources(), R.drawable.app_icon, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            imageView.setImageBitmap(ImageFileHelper.getResizedBitmap(bitmap, photoW, photoH));
        } else {
            KeeperUtils.showErrorAlertMessage(appContext, "Error loadThumbNail : bitmap : "+bitmap);
        }
    }
}
