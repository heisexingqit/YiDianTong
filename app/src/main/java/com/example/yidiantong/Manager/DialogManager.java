package com.example.yidiantong.Manager;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xuexiang.xui.widget.dialog.DialogLoader;

public class DialogManager {

    // 请求权限对话框
    public static void permissionDialog(Context myContext, String str) {
        DialogLoader.getInstance().showConfirmDialog(
                myContext, "提示", str, "去设置",
                (dialog, which) -> {
                    //引导用户到设置中去进行设置
//                    Intent intent = new Intent();
//                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                    intent.setData(Uri.fromParts("package", myContext.getPackageName(), null));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    myContext.startActivity(intent);
//                    dialog.dismiss();
                },
                "取消",
                (dialog, which) -> {
                    dialog.dismiss();
                }
        );
    }

}
