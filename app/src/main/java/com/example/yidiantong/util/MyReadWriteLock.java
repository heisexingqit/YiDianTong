package com.example.yidiantong.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class MyReadWriteLock {
    private static final String TAG = "ReadWriteLock";
    public static void checkin(String taskId, String userName, String userType, String teacherId, Handler handler, Context mContext) {
        String mRequestUrl = Constant.API + Constant.CHECK_IN + "?taskId=" + taskId + "&userName=" + userName + "&userType=" + userType + "&teacherId=" + teacherId;
        Log.e("0110", "请求进入修改URL:" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Message message = new Message();
                message.obj = json.getBoolean("data");
                message.what = 110;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, "checkin");
    }

    public static void checkoutT(String teacherID, Context mContext) {
        String mRequestUrl = Constant.API + Constant.T_CHECK_OUT + "?teacherID=" + teacherID;
        Log.e("0110", "教师请求退出URL:" + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

        }, error -> {
            Log.e(TAG, "checkoutT: 网络连接失败");
        });
        MyApplication.addRequest(request, "checkoutT");
    }

    public static void checkout(String studentID, Context mContext) {
        String mRequestUrl = Constant.API + Constant.CHECK_OUT + "?studentID=" + studentID;
        Log.e("0110", "学生请求退出URL:" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {


        }, error -> {
            Log.e(TAG, "checkout: 网络连接失败");
        });
        MyApplication.addRequest(request, "checkout");
    }
}
