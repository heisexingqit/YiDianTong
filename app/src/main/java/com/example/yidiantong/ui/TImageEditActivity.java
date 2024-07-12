package com.example.yidiantong.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.util.ImageUtils;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

import java.io.IOException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class TImageEditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TImageEditActivity";


    private ActivityResultLauncher<Intent> mResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timage_edit);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
    }
}