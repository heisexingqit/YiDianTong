package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Random;

public class CourseScannerActivity extends AppCompatActivity implements View.OnClickListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private ViewfinderView viewfinderView;
    private TextView ftv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_course_scanner);
            barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });
        ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("扫码");

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        // 点击按钮打开闪光灯
        switchFlashlightButton = findViewById(R.id.switch_flashlight);
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        switchFlashlightButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(color);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_flashlight:
                if(getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())){
                    barcodeScannerView.setTorchOn();
                    switchFlashlightButton.setText(R.string.turn_off_flashlight);
                } else {
                    barcodeScannerView.setTorchOff();
                    switchFlashlightButton.setText(R.string.turn_on_flashlight);
                }
        }
    }
}