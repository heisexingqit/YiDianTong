package com.example.yidiantong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TestDanXuanActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup f_rg_button;
    private RadioButton frb_dx_a;
    private RadioButton frb_dx_b;
    private RadioButton frb_dx_c;
    private RadioButton frb_dx_d;
    private TextView ftv_danxuan_tihao;
    private TextView ftv_danxuan_timu;
    private ImageView fiv_danxuan_left;
    private ImageView fiv_danxuan_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dan_xuan);

        ftv_danxuan_tihao = findViewById(R.id.ftv_danxuan_tihao);
        ftv_danxuan_timu = findViewById(R.id.ftv_danxuan_timu);
        fiv_danxuan_left = findViewById(R.id.fiv_danxuan_left);
        fiv_danxuan_right = findViewById(R.id.fiv_danxuan_right);

        f_rg_button = findViewById(R.id.f_rg_button);
        frb_dx_a = findViewById(R.id.frb_dx_a);
        frb_dx_b = findViewById(R.id.frb_dx_b);
        frb_dx_c = findViewById(R.id.frb_dx_c);
        frb_dx_d = findViewById(R.id.frb_dx_d);

        frb_dx_a.setOnClickListener(this);
        frb_dx_b.setOnClickListener(this);
        frb_dx_c.setOnClickListener(this);
        frb_dx_d.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.frb_dx_a:
                Toast.makeText(TestDanXuanActivity.this,"选择了A", Toast.LENGTH_SHORT).show();
                break;
            case R.id.frb_dx_b:
                Toast.makeText(TestDanXuanActivity.this,"选择了B", Toast.LENGTH_SHORT).show();
                break;
            case R.id.frb_dx_c:
                Toast.makeText(TestDanXuanActivity.this,"选择了C", Toast.LENGTH_SHORT).show();
                break;
            case R.id.frb_dx_d:
                Toast.makeText(TestDanXuanActivity.this,"选择了D", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}