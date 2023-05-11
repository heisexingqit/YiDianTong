package com.example.yidiantong.View;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.yidiantong.R;
import com.example.yidiantong.util.PxUtils;

public class TextEditorDialog extends Dialog implements View.OnClickListener{

    private final EditText et_text;
    private TextEditorDialog.MyInterface myInterface;

    private ClickableTextView ctv_submit;

    private Context mContext;

    public void setMyInterface(TextEditorDialog.MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    public TextEditorDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.text_editor_dialog);
        ctv_submit = findViewById(R.id.ctv_submit);
        ctv_submit.setOnClickListener(this);

        findViewById(R.id.civ_color1).setOnClickListener(this);
        findViewById(R.id.civ_color2).setOnClickListener(this);
        findViewById(R.id.civ_color3).setOnClickListener(this);
        findViewById(R.id.civ_color4).setOnClickListener(this);
        findViewById(R.id.civ_color5).setOnClickListener(this);
        findViewById(R.id.civ_color6).setOnClickListener(this);
        findViewById(R.id.civ_color7).setOnClickListener(this);
        findViewById(R.id.civ_color8).setOnClickListener(this);
        findViewById(R.id.civ_color9).setOnClickListener(this);
        findViewById(R.id.civ_color10).setOnClickListener(this);
        findViewById(R.id.civ_color11).setOnClickListener(this);
        findViewById(R.id.civ_color12).setOnClickListener(this);

        et_text = findViewById(R.id.et_text);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ctv_submit:
                myInterface.submit(et_text.getText().toString());
                et_text.setText("");
                break;
            default:
                Drawable drawable = view.getBackground();

                if (drawable instanceof ColorDrawable) {
                    et_text.setTextColor(((ColorDrawable) drawable).getColor());
                }
                myInterface.changeColor(((ColorDrawable) drawable).getColor());
        }
    }

    public void changeText(String text, int colorCode) {
        et_text.setText(text);
        et_text.setSelection(et_text.getText().length());
        et_text.setTextColor(colorCode);

    }

    public void addPadding() {
        LinearLayout ll_root = findViewById(R.id.ll_root);
        ll_root.setPadding(PxUtils.dip2px(mContext,20), PxUtils.dip2px(mContext,20), PxUtils.dip2px(mContext,20), PxUtils.dip2px(mContext,40));
    }

    public interface MyInterface{
        void submit(String text);
        void changeColor(int color);
    }
}
