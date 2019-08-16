package com.shouzhong.bankcardscanner.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shouzhong.bankcardscanner.BankCardUtils;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick1(View v) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivity(intent);
    }

    public void onClick2(View v) {
        Intent intent = new Intent(this, PortraitScannerActivity.class);
        startActivity(intent);
    }

    public void onClick3(View v) {
        try {
            Bitmap bmp = BitmapFactory.decodeStream(getAssets().open("img2.jpg"));
            String s = BankCardUtils.decode(bmp);
            Log.e("===============", "result=" + s);
        } catch (Exception e) {}
    }
}
