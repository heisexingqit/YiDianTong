
package com.example.yidiantong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yidiantong.adapter.MyArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ListView lvvv = findViewById(R.id.lvvv);
        MyArrayAdapter adapter = new MyArrayAdapter(this, new ArrayList<>(Arrays.asList("1111", "222", "3333")));
        lvvv.setAdapter(adapter);
        lvvv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(TestActivity.this, "dadas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}