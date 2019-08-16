package com.shouzhong.bankcardscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

class Utils {

    /**
     * 旋转数据
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    static final byte[] rotateData(byte[] data, int width, int height) {
        byte[] bs = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bs[x * height + height - y - 1] = data[x + y * width];
            }
        }
        return bs;
    }

    /**
     * bitmpap转nv21
     *
     * @param bmp
     * @param width
     * @param height
     * @return
     */
    static final byte[] bitmapToNv21(Bitmap bmp, int width, int height) {
        int[] argb = new int[width * height];
        bmp.getPixels(argb, 0, width, 0, 0, width, height);
        byte[] yuv = encodeYUV420SP(argb, width, height);
        return yuv;
    }

    private static final byte[] encodeYUV420SP(int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        byte[] nv21 = new byte[width * height * 3 / 2];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                int R = (argb[index] & 0xFF0000) >> 16;
                int G = (argb[index] & 0x00FF00) >> 8;
                int B = argb[index] & 0x0000FF;
                int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                nv21[yIndex++] = (byte) (Y < 0 ? 0 : (Y > 255 ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.length - 2) {
                    nv21[uvIndex++] = (byte) (V < 0 ? 0 : (V > 255 ? 255 : V));
                    nv21[uvIndex++] = (byte) (U < 0 ? 0 : (U > 255 ? 255 : U));
                }

                ++index;
            }
        }
        return nv21;
    }

    /**
     * nv21转bitmap
     *
     * @param nv21
     * @param width
     * @param height
     * @return
     */
    static final Bitmap nv21ToBitmap(byte[] nv21, int width, int height){
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存图片
     *
     * @param context
     * @param bitmap
     */
    static final String saveBitmap(final Context context, Bitmap bitmap) {
        try {
            final String local = context.getExternalCacheDir().getAbsolutePath() + "/bank_" + System.currentTimeMillis() + ".jpg";
            final File file = new File(local);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (null != bitmap) {
                bitmap.recycle();
            }
            return local;
        } catch (Exception e) { }
        return null;
    }

}
