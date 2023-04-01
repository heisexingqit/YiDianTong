package com.example.yidiantong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yidiantong.adapter.ImagePagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_menu);
        ViewPager vp = findViewById(R.id.vp_picture);
        List<Integer> picList = new ArrayList<>(Arrays.asList(R.drawable.camera,R.drawable.photo));
//        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, picList);
//        vp.setAdapter(imagePagerAdapter);
        TextView tv = findViewById(R.id.tv_picNum);
        tv.setText("1/" + picList.size());
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv.setText(position + 1 + "/" + picList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Test2Activity.this, "TETETETE", Toast.LENGTH_SHORT).show();
            }
        });
    }
}