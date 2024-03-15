package com.example.yidiantong.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.BookExerciseEntity;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.List;

public class BookExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BookExerciseEntity> itemList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public BookExerciseAdapter(Context context, List<BookExerciseEntity> itemList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        mContext = context;
    }

    public void update(List<BookExerciseEntity> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(itemList.get(position).baseTypeId);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case 101:
            case 102:
            case 103:
                v = layoutInflater.inflate(R.layout.item_book_exercise, parent, false);
                break;
            case 108:
            case 109:
                v = layoutInflater.inflate(R.layout.item_book_exercise_reading, parent, false);
                break;
            case 104:
                v = layoutInflater.inflate(R.layout.item_book_exercise_cloze, parent, false);
                break;
            default:
                v = layoutInflater.inflate(R.layout.item_book_exercise_cloze, parent, false);
                break;
        }
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BookExerciseAdapter.MyViewHolder) holder).update(position, holder);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_type_name, tv_shiti_answer, tv_stu_answer;
        private LinearLayout ll_danxuan, ll_duoxuan, ll_panduan, ll_answer_analysis;
        private WebView wv_timian, wv_analysis;
        private Button btn_submit;
        private ImageView iv_result;
        private ClickableImageView iv_a, iv_b, iv_c, iv_d;  // 单选按钮
        private ClickableImageView iv_a2, iv_b2, iv_c2, iv_d2;  // 多选按钮
        private ClickableImageView iv_r, iv_e;  // 判断按钮


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_type_name = itemView.findViewById(R.id.tv_type_name);
            ll_danxuan = itemView.findViewById(R.id.ll_danxuan);
            ll_duoxuan = itemView.findViewById(R.id.ll_duoxuan);
            ll_panduan = itemView.findViewById(R.id.ll_panduan);
            wv_timian = itemView.findViewById(R.id.wv_timian);
            wv_analysis = itemView.findViewById(R.id.wv_analysis);
            ll_answer_analysis = itemView.findViewById(R.id.ll_answer_analysis);
            iv_a = itemView.findViewById(R.id.iv_a);
            iv_b = itemView.findViewById(R.id.iv_b);
            iv_c = itemView.findViewById(R.id.iv_c);
            iv_d = itemView.findViewById(R.id.iv_d);
            btn_submit = itemView.findViewById(R.id.btn_submit);
            iv_result = itemView.findViewById(R.id.iv_result);
            tv_shiti_answer = itemView.findViewById(R.id.tv_shiti_answer);
            tv_stu_answer = itemView.findViewById(R.id.tv_stu_answer);
            iv_a2 = itemView.findViewById(R.id.iv_a2);
            iv_b2 = itemView.findViewById(R.id.iv_b2);
            iv_c2 = itemView.findViewById(R.id.iv_c2);
            iv_d2 = itemView.findViewById(R.id.iv_d2);
            iv_r = itemView.findViewById(R.id.iv_r);
            iv_e = itemView.findViewById(R.id.iv_e);
        }

        public void update(int pos, RecyclerView.ViewHolder holder) {

            BookExerciseEntity item = itemList.get(pos); // 获取当前item
            tv_type_name.setText(item.typeName); // 设置题目类型名称
            // 题面设置
            String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + item.shiTiShow + "</body>";
            String html = html_content.replace("#", "%23");
            wv_timian.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

            // 解析设置
            String html_content_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + item.shiTiAnalysis + "</body>";
            String html_analysis = html_content_analysis.replace("#", "%23");
            wv_analysis.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);


            // 答案设置
            switch (item.baseTypeId) {
                case "101":
                    // UI显示
                    ll_danxuan.setVisibility(View.VISIBLE);
                    ll_duoxuan.setVisibility(View.GONE);
                    ll_panduan.setVisibility(View.GONE);
                    // 单选按钮点击
                    View.OnClickListener danxuanListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.iv_a:
                                    item.stuAnswer = "A";
                                    break;
                                case R.id.iv_b:
                                    item.stuAnswer = "B";
                                    break;
                                case R.id.iv_c:
                                    item.stuAnswer = "C";
                                    break;
                                case R.id.iv_d:
                                    item.stuAnswer = "D";
                                    break;
                            }
                            showDanxuanBtn(item.stuAnswer);
                        }
                    };
                    iv_a.setOnClickListener(danxuanListener);
                    iv_b.setOnClickListener(danxuanListener);
                    iv_c.setOnClickListener(danxuanListener);
                    iv_d.setOnClickListener(danxuanListener);

                    // html清洗
                    item.shiTiAnswer = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");


                    // 答案设置
                    tv_shiti_answer.setText("【参考答案】" + item.shiTiAnswer);
                    break;
                case "102": // 多选题
                    // UI显示
                    ll_danxuan.setVisibility(View.GONE);
                    ll_duoxuan.setVisibility(View.VISIBLE);
                    ll_panduan.setVisibility(View.GONE);
                    // 单选按钮点击
                    View.OnClickListener duoxuanListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.iv_r:
                                    item.stuAnswer = "对";
                                    break;
                                case R.id.iv_e:
                                    item.stuAnswer = "错";
                            }
                            showDanxuanBtn(item.stuAnswer);
                        }
                    };


                    // html清洗
                    item.shiTiAnswer = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");


                    // 答案设置
                    tv_shiti_answer.setText("【参考答案】" + item.shiTiAnswer);
                    break;
                case "103": // 判断题
                    // UI显示
                    ll_danxuan.setVisibility(View.GONE);
                    ll_duoxuan.setVisibility(View.GONE);
                    ll_panduan.setVisibility(View.VISIBLE);
                    // 单选按钮点击
                    View.OnClickListener panduanListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.iv_r:
                                    item.stuAnswer = "对";
                                    break;
                                case R.id.iv_e:
                                    item.stuAnswer = "错";
                            }
                            showPanduanBtn(item.stuAnswer);
                        }
                    };
                    iv_r.setOnClickListener(panduanListener);
                    iv_e.setOnClickListener(panduanListener);

                    // html清洗
                    item.shiTiAnswer = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");


                    // 答案设置
                    tv_shiti_answer.setText("【参考答案】" + item.shiTiAnswer);
                    break;
                case "108":
                case "109": // 阅读题
                    break;
                case "104": // 填空题
                    break;
                default: // 主观题
                    Log.e("wen0315", "update: " + "pos = " + pos + "  item.baseTypeId = " + item.baseTypeId + "  item.typeName = " + item.typeName);
                    break;

            }
            ll_answer_analysis.setVisibility(View.GONE);


            // 提交按钮设置
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.stuAnswer == null || item.stuAnswer.length() == 0) {
                        Toast.makeText(mContext, "请选择答案", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (item.baseTypeId) {
                        case "101": // 单选题
                            // 结果图标
                            if (item.shiTiAnswer.equals(item.stuAnswer)) {
                                iv_result.setImageResource(R.drawable.ansright);
                            } else {
                                iv_result.setImageResource(R.drawable.answrong);
                            }
                            // 学生作答显示
                            tv_stu_answer.setText("【你的答案】" + item.stuAnswer);
                            break;
                        case "102": // 多选题
                            break;
                        case "103": // 判断题
                            break;
                        case "108":
                        case "109": // 阅读题
                            break;
                        case "104": // 填空题
                            break;
                        default: // 主观题
                            break;

                    }
                    ll_answer_analysis.setVisibility(View.VISIBLE); // 显示答案解析
                }
            });

        }

        private void showDanxuanBtn(String showAnswer) {
            iv_a.setImageResource(R.drawable.a_unselect);
            iv_b.setImageResource(R.drawable.b_unselect);
            iv_c.setImageResource(R.drawable.c_unselect);
            iv_d.setImageResource(R.drawable.d_unselect);
            switch (showAnswer) {
                case "A":
                    iv_a.setImageResource(R.drawable.a_select);
                    break;
                case "B":
                    iv_b.setImageResource(R.drawable.b_select);
                    break;
                case "C":
                    iv_c.setImageResource(R.drawable.c_select);
                    break;
                case "D":
                    iv_d.setImageResource(R.drawable.d_select);
                    break;
            }

        }

        private void showPanduanBtn(String showAnswer) {
            iv_r.setImageResource(R.drawable.right_unselect);
            iv_e.setImageResource(R.drawable.error_unselect);
            switch (showAnswer) {
                case "对":
                    iv_r.setImageResource(R.drawable.right_select);
                    break;
                case "错":
                    iv_e.setImageResource(R.drawable.error_select);
                    break;
            }
        }

        private void showDuoxuanBtn(String showAnswer) {
        }
    }


}
