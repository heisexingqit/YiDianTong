package com.example.yidiantong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yidiantong.R;

public class CourseQuestionFragment extends Fragment {



    public CourseQuestionFragment() {
        // Required empty public constructor
    }

    public static CourseQuestionFragment newInstance(String param1, String param2) {
        CourseQuestionFragment fragment = new CourseQuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_question, container, false);
    }
}