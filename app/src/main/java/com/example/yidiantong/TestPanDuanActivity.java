package com.example.yidiantong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TestPanDuanActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pan_duan);

        TextView ftv_panduan_tihao = findViewById(R.id.ftv_panduan_tihao);
        TextView ftv_panduan_timu = findViewById(R.id.ftv_panduan_timu);
        ImageView fiv_panduan_left = findViewById(R.id.fiv_panduan_left);
        ImageView fiv_panduan_right = findViewById(R.id.fiv_panduan_right);

        RadioGroup frg_button = findViewById(R.id.frg_button);
        RadioButton frb_pd_r = findViewById(R.id.frb_pd_r);
        RadioButton frb_pd_e = findViewById(R.id.frb_pd_e);


        frb_pd_r.setOnClickListener(this);
        frb_pd_e.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frb_pd_r:
                Toast.makeText(TestPanDuanActivity.this, "选择了对", Toast.LENGTH_SHORT).show();
                break;
            case R.id.frb_pd_e:
                Toast.makeText(TestPanDuanActivity.this, "选择了错", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}