package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BooksRecyclerAdapter;
import com.example.yidiantong.adapter.CourseQuestionAdapter;

public class CourseQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private String stuname;
    private String stunum;
    private String ip;
    private String imagePath;
    private String questionTypeName;
    private String imgSource;
    // 单选
    int[] unselectDan = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect,R.drawable.e_unselect, R.drawable.f_unselect};
    int[] selectDan = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select,R.drawable.e_select, R.drawable.f_select};
    ClickableImageView[] iv_answer_dan = new ClickableImageView[6];
    int[] answer_dan = {-1, -1, -1, -1, -1,-1};
    int questionId = 0;
    //String[] option = {"A","B","C","D"};
    // 多选
    int[] unselectDuo = {R.drawable.a_unselect2, R.drawable.b_unselect2, R.drawable.c_unselect2, R.drawable.d_unselect2,R.drawable.e_unselect2, R.drawable.f_unselect2};
    int[] selectDuo = {R.drawable.a_select2, R.drawable.b_select2, R.drawable.c_select2, R.drawable.d_select2,R.drawable.e_select2, R.drawable.f_select2};
    ClickableImageView[] iv_answer_duo = new ClickableImageView[6];
    int[] answer_duo = {0, 0, 0, 0,0,0};
    // 判断
    int[] unselectJudge = {R.drawable.right_unselect, R.drawable.error_unselect};
    int[] selectJudge = {R.drawable.right_select, R.drawable.error_select};
    ClickableImageView[] iv_answer_jud = new ClickableImageView[2];
    int answer_jud = -1;

    private ViewPager fvp_cq;
    private CourseQuestionAdapter adapter;
    private LinearLayout fll_cq;
    private LinearLayout fll_cq_danxuan;
    private LinearLayout fll_cq_duouan;
    private LinearLayout fll_cq_panduan;
    private LinearLayout fll_cq_tiankong;
    private String questionValueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_question);

        // 中间图片
        fvp_cq = findViewById(R.id.fvp_cq);
        adapter = new CourseQuestionAdapter(getSupportFragmentManager());
        fvp_cq.setAdapter(adapter);

        // 网页信息
        stuname = getIntent().getStringExtra("stuname");
        stunum = getIntent().getStringExtra("stunum");
        ip = getIntent().getStringExtra("ip");
        imagePath = getIntent().getStringExtra("imagePath");
        questionTypeName = getIntent().getStringExtra("questionTypeName");
        imgSource = getIntent().getStringExtra("imgSource");
        questionValueList = getIntent().getStringExtra("questionValueList");
        questionValueList.replace(",","");
        Log.e("questionValueList",questionValueList);
        fll_cq = findViewById(R.id.fll_cq);
        View v = LayoutInflater.from(this).inflate(R.layout.course_btn, fll_cq, false);
        fll_cq_danxuan = v.findViewById(R.id.fll_cq_danxuan);
        fll_cq_duouan = v.findViewById(R.id.fll_cq_duouan);
        fll_cq_panduan = v.findViewById(R.id.fll_cq_panduan);
        fll_cq_tiankong = v.findViewById(R.id.fll_cq_tiankong);
        if(questionTypeName.equals("单项选择题")){
            fll_cq_danxuan.setVisibility(View.VISIBLE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.GONE);
            //单选显示答案选项
            ClickableImageView iv_a = v.findViewById(R.id.fiv_cb_a);
            ClickableImageView iv_b = v.findViewById(R.id.fiv_cb_b);
            ClickableImageView iv_c = v.findViewById(R.id.fiv_cb_c);
            ClickableImageView iv_d = v.findViewById(R.id.fiv_cb_d);
            ClickableImageView iv_e = v.findViewById(R.id.fiv_cb_e);
            ClickableImageView iv_f = v.findViewById(R.id.fiv_cb_f);

            iv_answer_dan[0] = iv_a;
            iv_answer_dan[1] = iv_b;
            iv_answer_dan[2] = iv_c;
            iv_answer_dan[3] = iv_d;
            iv_answer_dan[4] = iv_e;
            iv_answer_dan[5] = iv_f;
            for(int i =0 ; i<6;i++){
                if(i<questionValueList.length()){
                    iv_answer_dan[i].setVisibility(View.VISIBLE);
                    iv_answer_dan[i].setOnClickListener(this);
                }else {
                    iv_answer_dan[i].setVisibility(View.GONE);
                }
            }
        }else if(questionTypeName.equals("多项选择题")){
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.VISIBLE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.GONE);
            //多选显示答案选项
            ClickableImageView iv_a = v.findViewById(R.id.fiv_cb_a2);
            ClickableImageView iv_b = v.findViewById(R.id.fiv_cb_b2);
            ClickableImageView iv_c = v.findViewById(R.id.fiv_cb_c2);
            ClickableImageView iv_d = v.findViewById(R.id.fiv_cb_d2);
            ClickableImageView iv_e = v.findViewById(R.id.fiv_cb_e2);
            ClickableImageView iv_f = v.findViewById(R.id.fiv_cb_f2);
            iv_answer_duo[0] = iv_a;
            iv_answer_duo[1] = iv_b;
            iv_answer_duo[2] = iv_c;
            iv_answer_duo[3] = iv_d;
            iv_answer_duo[4] = iv_e;
            iv_answer_duo[5] = iv_f;
            for(int i =0 ; i<6;i++){
                if(i<questionValueList.length()){
                    iv_answer_duo[i].setVisibility(View.VISIBLE);
                    iv_answer_duo[i].setOnClickListener(this);
                }else {
                    iv_answer_duo[i].setVisibility(View.GONE);
                }
            }
        }else if(questionTypeName.equals("判断题")){
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.VISIBLE);
            fll_cq_tiankong.setVisibility(View.GONE);
            //显示答案选项
            ClickableImageView iv_r = v.findViewById(R.id.fiv_cb_r);
            ClickableImageView iv_e = v.findViewById(R.id.fiv_cb_er);
            iv_answer_jud[0] = iv_r;
            iv_answer_jud[1] = iv_e;
            iv_r.setOnClickListener(this);
            iv_e.setOnClickListener(this);
        }else if(questionTypeName.equals("填空题")){
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.VISIBLE);

        }
        fll_cq.addView(v);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fiv_cb_a:
                answer_dan[questionId] = 0;
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_b:
                answer_dan[questionId] = 1;
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_c:
                answer_dan[questionId] = 2;
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_d:
                answer_dan[questionId] = 3;
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_e:
                answer_dan[questionId] = 4;
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_f:
                answer_dan[questionId] = 5;
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_a2:
                answer_duo[0] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_b2:
                answer_duo[1] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_c2:
                answer_duo[2] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_d2:
                answer_duo[3] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_e2:
                answer_duo[4] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_f2:
                answer_duo[5] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_r:
                answer_jud = 1;
                showRadioBtnJud();
                break;
            case R.id.fiv_cb_er:
                answer_jud = 0;
                showRadioBtnJud();
                break;
        }
    }


    //单选展示底部按钮
    private void showRadioBtnDan() {
        for (int i = 0; i < questionValueList.length(); ++i) {
            if (answer_dan[questionId] != i) {
                iv_answer_dan[i].setImageResource(unselectDan[i]);
            } else {
                iv_answer_dan[i].setImageResource(selectDan[i]);
            }
        }
    }

    //多选展示底部按钮
    private void showRadioBtnDuo() {
        for (int i = 0; i < questionValueList.length(); ++i) {
            if (answer_duo[questionId] != i) {
                iv_answer_duo[i].setImageResource(unselectDuo[i]);
            } else {
                iv_answer_duo[i].setImageResource(selectDuo[i]);
            }
        }
    }

    //判断展示底部按钮
    private void showRadioBtnJud() {
//        //同步答案给Activity
//        transmit.setStuAnswer(stuAnswerEntity.getOrder(), answer == 1 ? "对" : "错");
        for (int i = 0; i < 2; ++i) {
            if (answer_jud != i) {
                iv_answer_jud[i].setImageResource(unselectJudge[i]);
            } else {
                iv_answer_jud[i].setImageResource(selectJudge[i]);
            }
        }
    }
}