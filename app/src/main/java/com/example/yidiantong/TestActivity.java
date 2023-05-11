
package com.example.yidiantong;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yidiantong.View.DrawEditorDialog;
import com.example.yidiantong.View.EraserEditorDialog;
import com.example.yidiantong.View.TextEditorDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUIBasePopup;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;


public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_show;
    private Button btn_upload;

    private ActivityResultLauncher<Intent> mResultLauncher;

    private Uri picUri;

    // 权限组
    // 读写存储卡权限
    private static final String[] PERMISSIONS_STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // 标识码（与权限对应）
    private static final int REQUEST_CODE_STORAGE = 1;
    private PhotoEditor mPhotoEditor;
    private DrawEditorDialog drawDialog;
    private TextEditorDialog textDialog;
    private EraserEditorDialog eraserDialog;

    private int textColor = Color.WHITE;
    private View root_view;
    private boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        PhotoEditorView pev_content = findViewById(R.id.pev_content);
        pev_content.getSource().setImageResource(R.drawable.camera);

        mPhotoEditor = new PhotoEditor.Builder(this, pev_content)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.setBrushDrawingMode(true);
        mPhotoEditor.setBrushColor(Color.RED);

        // 取消
        findViewById(R.id.civ_cancel).setOnClickListener(this);

        // 保存
        findViewById(R.id.civ_save).setOnClickListener(this);

        // 笔刷
        findViewById(R.id.ll_draw).setOnClickListener(this);

        // 文字
        findViewById(R.id.ll_text).setOnClickListener(this);

        // 橡皮
        findViewById(R.id.ll_erase).setOnClickListener(this);

        // 清空
        findViewById(R.id.ll_clean).setOnClickListener(this);

        // 撤销
        findViewById(R.id.civ_undo).setOnClickListener(this);

        // 反撤销
        findViewById(R.id.civ_redo).setOnClickListener(this);



        /**
         * 画笔对话框 初始化
         */
        drawDialog = new DrawEditorDialog(this);
        drawDialog.getWindow().setGravity(Gravity.BOTTOM);
        // 监听选项进行设置
        drawDialog.setMyInterface(new DrawEditorDialog.MyInterface() {
            @Override
            public void changeBrush(int progress) {
                mPhotoEditor.setBrushSize(progress);
            }

            @Override
            public void changeColor(int colorInt) {
                mPhotoEditor.setBrushColor(colorInt);
                drawDialog.dismiss();
            }

        });

        // 尺寸设置
        Window window = drawDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.black);
        /**
         * 文本对话框 初始化
         */
        textDialog = new TextEditorDialog(this);
        textDialog.getWindow().setGravity(Gravity.BOTTOM);
        textDialog.setMyInterface(new TextEditorDialog.MyInterface() {
            @Override
            public void submit(String text) {
                if(isChange){
                    mPhotoEditor.editText(root_view, text, textColor);
                }else{
                    if(text.length() != 0){
                        mPhotoEditor.addText(text, textColor);
                    }
                }
                textDialog.dismiss();
            }

            @Override
            public void changeColor(int color) {
                textColor = color;
            }
        });

        window = textDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawableResource(android.R.color.black);


        /**
         * 文本修改
         */
        mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {

            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                root_view = rootView;
                isChange = true;
                textDialog.changeText(text, colorCode);
                textDialog.show();
            }

            @Override
            public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

            }

            @Override
            public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

            }

            @Override
            public void onStartViewChangeListener(ViewType viewType) {

            }

            @Override
            public void onStopViewChangeListener(ViewType viewType) {

            }

            @Override
            public void onTouchSourceImage(MotionEvent event) {

            }
        });

        /**
         * 橡皮
         */
        eraserDialog = new EraserEditorDialog(this);
        eraserDialog.getWindow().setGravity(Gravity.BOTTOM);
        // 监听选项进行设置
        eraserDialog.setMyInterface(new EraserEditorDialog.MyInterface() {
            @Override
            public void changeBrush(int progress) {
                mPhotoEditor.setBrushSize(progress);
            }
        });

        // 尺寸设置
        window = eraserDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.black);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_draw:
                drawDialog.show();
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setBrushSize(drawDialog.getProgress());
                break;
            case R.id.ll_text:
                isChange = false;
                textDialog.show();
                break;
            case R.id.ll_erase:
                eraserDialog.show();
                mPhotoEditor.setBrushSize(eraserDialog.getProgress());
                mPhotoEditor.brushEraser();
                break;

            case R.id.ll_clean:
                mPhotoEditor.clearAllViews();
                break;
            case R.id.civ_undo:
                mPhotoEditor.undo();
                break;
            case R.id.civ_redo:
                mPhotoEditor.redo();
                break;
            case R.id.civ_cancel:
                // 取消
                break;
            case R.id.civ_save:
                // 保存
                break;
        }
    }
}