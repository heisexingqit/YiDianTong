package com.example.yidiantong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.THomeworkCameraItem;

public class THomeworkCameraSingleFragment extends Fragment {

    private THomeworkCameraItem item;

    public static THomeworkCameraSingleFragment newInstance(THomeworkCameraItem item) {
        THomeworkCameraSingleFragment fragment = new THomeworkCameraSingleFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_homework_camera_single, container, false);
        //取出携带的参数
        Bundle arg = getArguments();
        item = (THomeworkCameraItem) arg.getSerializable("data");




        return view;
    }
}