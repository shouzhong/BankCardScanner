package com.shouzhong.bankcardscanner;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.wintone.bankcard.BankCardAPI;

public class BankCardUtils {
    /**
     * 图片识别，识别率很低
     *
     * @param bmp
     * @return
     */
    public static String decode(Bitmap bmp) {
        if (bmp == null) return null;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        BankCardAPI api = new BankCardAPI();
        api.WTInitCardKernal("", 0);
        api.WTSetROI(new int[]{0, 0, width, height}, width, height);
        byte[] data = Utils.bitmapToNv21(bmp, width, height);
        int[] borders = new int[4];
        char[] resultData = new char[30];
        int[] picture = new int[32000];
        int result = api.RecognizeNV21(data, width, height, borders, resultData, resultData.length, new int[1], picture);
        if (result != 0) {
            Matrix m = new Matrix();
            m.setRotate(90, width / 2, height / 2);
            bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
            data = Utils.bitmapToNv21(bmp, height, width);
            api.WTSetROI(new int[]{0, 0, height, width}, height, width);
            result = api.RecognizeNV21(data, height, width, borders, resultData, resultData.length, new int[1], picture);
            if (result != 0) {
                api.WTUnInitCardKernal();
                return null;
            }
        }
        api.WTUnInitCardKernal();
        final StringBuffer sb = new StringBuffer();
        for (char c : resultData) {
            if (c >= '0' && c <= '9') sb.append(c);
        }
        return sb.toString();
    }
}
