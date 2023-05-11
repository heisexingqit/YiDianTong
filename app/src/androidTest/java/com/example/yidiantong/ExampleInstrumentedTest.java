package com.example.yidiantong;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.yidiantong.util.JsonUtils;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws JSONException {
        String s = "\"{\\\"message\\\":\\\"数据获取成功！\\\",\\\"data\\\":[{\\\"order\\\":1,\\\"questionId\\\":\\\"PRQUI9001081938\\\",\\\"status\\\":\\\"\\\",\\\"type\\\":\\\"\\\",\\\"stuAnswer\\\":\\\"\\\",\\\"teaScore\\\":\\\"\\\"},{\\\"order\\\":2,\\\"questionId\\\":\\\"PRQUI9001081630\\\",\\\"status\\\":\\\"\\\",\\\"type\\\":\\\"\\\",\\\"stuAnswer\\\":\\\"\\\",\\\"teaScore\\\":\\\"\\\"},{\\\"order\\\":3,\\\"questionId\\\":\\\"PRQUI9001081627\\\",\\\"status\\\":\\\"\\\",\\\"type\\\":\\\"\\\",\\\"stuAnswer\\\":\\\"\\\",\\\"teaScore\\\":\\\"\\\"},{\\\"order\\\":4,\\\"questionId\\\":\\\"PRQUI9001081628\\\",\\\"status\\\":\\\"\\\",\\\"type\\\":\\\"\\\",\\\"stuAnswer\\\":\\\"\\\",\\\"teaScore\\\":\\\"\\\"},{\\\"order\\\":5,\\\"questionId\\\":\\\"PRQUI9001079696\\\",\\\"status\\\":\\\"\\\",\\\"type\\\":\\\"\\\",\\\"stuAnswer\\\":\\\"\\\",\\\"teaScore\\\":\\\"\\\"},{\\\"order\\\":6,\\\"questionId\\\":\\\"PRQUI9001081941\\\",\\\"status\\\":\\\"\\\",\\\"type\\\":\\\"\\\",\\\"stuAnswer\\\":\\\"ddd<img onclick='bigimage(this)' onclick='bigimage(this)' onclick='bigimage(this)' onclick=\\\\\\\"bigimage(this)\\\\\\\" src=\\\\\\\"http://www.cn901.com/res/studentAnswerImg/AppImage/2023/03/23/m6002_093419095.png\\\\\\\" style=\\\\\\\"max-width:80px\\\\\\\"><img onclick='bigimage(this)' onclick='bigimage(this)' onclick='bigimage(this)' onclick=\\\\\\\"bigimage(this)\\\\\\\" src=\\\\\\\"http://www.cn901.com/res/studentAnswerImg/AppImage/2023/03/23/m6002_093432670.png\\\\\\\" style=\\\\\\\"max-width:80px\\\\\\\"><img onclick='bigimage(this)' onclick='bigimage(this)' onclick='bigimage(this)' onclick=\\\\\\\"bigimage(this)\\\\\\\" src=\\\\\\\"http://www.cn901.com/res/studentAnswerImg/AppImage/2023/03/23/m6002_093437409.png\\\\\\\" style=\\\\\\\"max-width:80px\\\\\\\"><img onclick='bigimage(this)' onclick='bigimage(this)' onclick='bigimage(this)' onclick=\\\\\\\"bigimage(this)\\\\\\\" src=\\\\\\\"http://www.cn901.com/res/studentAnswerImg/AppImage/2023/03/23/m6002_093441431.png\\\\\\\" style=\\\\\\\"max-width:80px\\\\\\\"><img onclick='bigimage(this)' onclick='bigimage(this)' onclick='bigimage(this)' onclick=\\\\\\\"bigimage(this)\\\\\\\" src=\\\\\\\"http://www.cn901.com/res/studentAnswerImg/AppImage/2023/03/23/m6002_093507646.png\\\\\\\" style=\\\\\\\"max-width:80px\\\\\\\">sadfsdfdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdfafdsssssssssssssssssssssssssss\\\",\\\"teaScore\\\":\\\"\\\"}],\\\"success\\\":true}\"";
        JSONObject jsonObject = JsonUtils.getJsonObjectFromString(s);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        String s2 = jsonArray.getJSONObject(5).getString("stuAnswer");
        Log.d("wen", "useAppContext: " + s2);
    }
}