package com.example.yidiantong.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class PermissionUtil {

    //检查多个权限。返回true表示已完全启用权限，返回false表示未完全启用权限
    public static boolean checkPermissionForFragment(Activity act, String[] permissions, int requestCode) {
        //判断是否为Android 6.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = PackageManager.PERMISSION_GRANTED;
            for (String permission : permissions) {
                check = ContextCompat.checkSelfPermission(act, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
            //弹窗获取权限,传入标识码
            if (check != PackageManager.PERMISSION_GRANTED) {
                act.requestPermissions(permissions, requestCode);
                return false;
            }
        }
        return true;
    }

    //检查回调函数中的结果码
    public static boolean checkGrant(int[] grantResults) {
        if (grantResults != null) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
