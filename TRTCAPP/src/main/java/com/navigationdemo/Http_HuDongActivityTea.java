package com.navigationdemo;

import android.annotation.SuppressLint;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Http_HuDongActivityTea extends AnswerActivityTea {

    //http访问服务器请求,开始答题、结束答题等接口
    @SuppressLint("LongLogTag")
    public static boolean saveQueHdAction(int questionAnswerType, int questionSubNum, int questionBaseTypeId, String questionAction, String id) {
        try {
            //试题id、房间号、自己的课堂id
//            AnswerActivityTea.answerQuestionId = "9c411703-91ef-4400-941f-01d8d660016e";
            AnswerActivityTea.roomId = MainActivity_tea.roomid;
            AnswerActivityTea.currentketangId = MainActivity_tea.keTangId;
            String roomId = AnswerActivityTea.roomId;
            String answerQuestionId = AnswerActivityTea.answerQuestionId;
            String currentketangId = AnswerActivityTea.currentketangId;
            //1,找水源--创建URL
            URL url;
            Time time = new Time("GMT+8");
            time.setToNow();
            String path = "class/" + time.year + "/" + (time.month + 1) + "/" + time.monthDay + "/" + MainActivity_tea.subjectId + "/" + MainActivity_tea.roomid + "/question/" + id + ".png";
            if (questionAction.equals("stopAnswer")) {
                url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_saveQueHdAction.do?roomId="
                        + roomId
                        + "&questionAnswerType="
                        + questionAnswerType
                        + "&questionSubNum="
                        + questionSubNum
                        + "&questionBaseTypeId="
                        + questionBaseTypeId
                        + "&questionAction="
                        + questionAction
                        + "&answerQuestionId="
                        + answerQuestionId
                        + "&answerQuestionTime="
                        + AnswerActivityTea.startTime
                        + "&currentketangId="
                        + currentketangId
                        + "&versionFlag="
                        + "whiteBoard"
                        + "&path=" + path);
            } else {
                url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_saveQueHdAction.do?roomId="
                        + roomId
                        + "&questionAnswerType="
                        + questionAnswerType
                        + "&questionSubNum="
                        + questionSubNum
                        + "&questionBaseTypeId="
                        + questionBaseTypeId
                        + "&questionAction="
                        + questionAction
                        + "&answerQuestionId="
                        + answerQuestionId
                        + "&answerQuestionTime="
                        + ""
                        + "&currentketangId="
                        + ""
                        + "&versionFlag="
                        + "whiteBoard"
                        + "&path=" + path);
            }

            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();

            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                JSONObject jsonObject = new JSONObject(backlogJsonStr);

                if (questionAction.indexOf("start") >= 0) {
                    AnswerActivityTea.startTime = jsonObject.getLong("time");
                }
                AnswerActivityTea.isSuccess = jsonObject.getString("success").equals("success") ? true : false;
                Log.d("wenbug2", "【教师端】=》开始/结束答题【返回数据】:" + jsonObject);
                return AnswerActivityTea.isSuccess;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //http访问服务器请求,获取已经提交答案的学生人数
    @SuppressLint("LongLogTag")
    public static void getSubmitAnswerStuNum() {
        try {
            //试题id、房间号、自己的课堂id
//            AnswerActivityTea.answerQuestionId = "9c411703-91ef-4400-941f-01d8d660016e";
            AnswerActivityTea.roomId = MainActivity_tea.roomid;
            AnswerActivityTea.currentketangId = MainActivity_tea.keTangId;
            String roomId = AnswerActivityTea.roomId;
            String answerQuestionId = AnswerActivityTea.answerQuestionId;
            String currentketangId = AnswerActivityTea.currentketangId;
            //1,找水源--创建URL
            URL url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_getStuAnswerNum.do?roomId="
                    + roomId
                    + "&answerQuestionId="
                    + answerQuestionId
                    + "&currentketangId="
                    + currentketangId);
            Log.d("wen", "getSubmitAnswerStuNum:" + url);
            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();

            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                JSONObject jsonObject = new JSONObject(backlogJsonStr);

                int stuNum = jsonObject.getInt("answerNum");
                AnswerActivityTea.answerNum = stuNum;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //http访问服务器请求,获取已经提交答案的课堂的学生答案汇总（用于客观题单题分析、查看答案详情;主观题答题详情）
    @SuppressLint("LongLogTag")
    public static JSONObject getSubmitAnswerClass_keguan(String ketangId, int stuNum) {
        try {
            //试题id、房间号、自己的课堂id
//            AnswerActivityTea.answerQuestionId = "9c411703-91ef-4400-941f-01d8d660016e";
            AnswerActivityTea.roomId = MainActivity_tea.roomid;
            AnswerActivityTea.currentketangId = MainActivity_tea.keTangId;
            String roomId = AnswerActivityTea.roomId;
            String answerQuestionId = AnswerActivityTea.answerQuestionId;
            String currentketangId = AnswerActivityTea.currentketangId;
            //1,找水源--创建URL
            URL url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_initDataChartsKt.do?roomId="
                    + roomId
                    + "&ketangId="
                    + ketangId
                    + "&answerQuestionId="
                    + answerQuestionId
                    + "&stuNum="
                    + stuNum
                    + "&currentketangId="
                    + currentketangId);
            System.out.println(url);
            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();

            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                System.out.println("提交答案的课堂的学生答案汇总1111==>" + backlogJsonStr);
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                System.out.println("提交答案的课堂的学生答案汇总2222==>" + backlogJsonStr);
                JSONObject jsonObject = new JSONObject(backlogJsonStr);
                System.out.println("提交答案的课堂的学生答案汇总Json对象==>" + jsonObject);

                //是否有人作答
                boolean status = jsonObject.getBoolean("status");
                AnswerActivityTea.submitAnswerStatus = status;
                System.out.println("是否有人提交答案==>" + AnswerActivityTea.submitAnswerStatus);

                if (status) {
                    return jsonObject;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //http访问服务器请求,设置答案
    @SuppressLint("LongLogTag")
    public static boolean setAnswer(String ketangId, String answer) {
        System.out.println("开始设置答案！！！！！！！！！！！！！！！！！！！！！！！！");
        try {
            //试题id、房间号、自己的课堂id
//            AnswerActivityTea.answerQuestionId = "9c411703-91ef-4400-941f-01d8d660016e";
            AnswerActivityTea.roomId = MainActivity_tea.roomid;
            String roomId = AnswerActivityTea.roomId;
            String answerQuestionId = AnswerActivityTea.answerQuestionId;
            //1,找水源--创建URL
            URL url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_saveQueAnswer.do?roomId="
                    + roomId
                    + "&ketangId="
                    + ketangId
                    + "&answerQuestionId="
                    + answerQuestionId
                    + "&answer="
                    + answer);
            System.out.println(url);
            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();
            Log.e("MAIN-http!!!!!!!!!!!!!!!!!!!!!", buffer.toString());//打印结果

            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                System.out.println("设置答案1111==>" + backlogJsonStr);
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                System.out.println("设置答案2222==>" + backlogJsonStr);
                JSONObject jsonObject = new JSONObject(backlogJsonStr);
                System.out.println("设置答案Json对象==>" + jsonObject);

                //设置答案成功
                String string_status = jsonObject.getString("status");
                boolean status = string_status.equals("success") ? true : false;
                System.out.println("答案是否设置成功==>" + status);
                return status;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //http访问服务器请求,获取主观题答案内容
    @SuppressLint("LongLogTag")
    public static JSONObject getSubmitAnswerClass_zhuguan(String ketangId) {
        System.out.println("开始获取已经提交主观题答案的课堂的学生答案汇总！！！！！！！！！！！！！！！！！！！！！！！！");
        try {
            //试题id、房间号、自己的课堂id
//            AnswerActivityTea.answerQuestionId = "9c411703-91ef-4400-941f-01d8d660016e";
            AnswerActivityTea.roomId = MainActivity_tea.roomid;
            AnswerActivityTea.currentketangId = MainActivity_tea.keTangId;
            String roomId = AnswerActivityTea.roomId;
            String answerQuestionId = AnswerActivityTea.answerQuestionId;
            String currentketangId = AnswerActivityTea.currentketangId;
            //1,找水源--创建URL
            URL url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_initDataContentKt.do?roomId="
                    + roomId
                    + "&ketangId="
                    + ketangId
                    + "&answerQuestionId="
                    + answerQuestionId
                    + "&currentketangId="
                    + currentketangId);
            System.out.println(url);
            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();
            Log.e("MAIN-http!!!!!!!!!!!!!!!!!!!!!", buffer.toString());//打印结果

            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                System.out.println("主观题答案1111==>" + backlogJsonStr);
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                System.out.println("主观题答案2222==>" + backlogJsonStr);
                JSONObject jsonObject = new JSONObject(backlogJsonStr);
                System.out.println("主观题答案Json对象==>" + jsonObject);

                //是否有人作答
                boolean status = jsonObject.getBoolean("status");
                AnswerActivityTea.submitAnswerStatus_zhuguan = status;
                System.out.println("是否有人作答==>" + status);

                if (status) {
                    return jsonObject;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //http访问服务器请求,获取随机或者抢答的学生姓名，学生id
    @SuppressLint("LongLogTag")
    public static void getSjOrQdStu(int type) {
        try {
            //房间号
            AnswerActivityTea.roomId = MainActivity_tea.roomid;
            String roomId = AnswerActivityTea.roomId;
            //1,找水源--创建URL
            URL url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_getSjOrQdStudent.do?roomId="
                    + roomId
                    + "&type="
                    + type);
            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();
            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                JSONObject jsonObject = new JSONObject(backlogJsonStr);

                //选中的是课堂或移动端的学生
                AnswerActivityTea.flag = jsonObject.getString("flag");
                //ketangId
                AnswerActivityTea.ketangId = jsonObject.getString("ketangId");
                //stuId
                AnswerActivityTea.stuId = jsonObject.getString("stuId");
                //stuName
                AnswerActivityTea.stuName = jsonObject.getString("stuName");
                Log.d("wenbug2", "【教师端】=》获取随机或抢答的【学生数据】:" + jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //http访问服务器请求,获取随机或者抢答的学生提交的答案
    @SuppressLint("LongLogTag")
    public static void getSjOrQdAnswer() {
        try {
//            AnswerActivityTea.answerQuestionId = "9c411703-91ef-4400-941f-01d8d660016e";
            //1,找水源--创建URL
            URL url = new URL("http://www.cn901.com/ShopGoods/ajax/livePlay_getSjOrQdAnswer.do?questionId="
                    + AnswerActivityTea.answerQuestionId);
            //2,开水闸--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //3，建管道--InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，建蓄水池蓄水-InputStreamReader
            //InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
            //5，水桶盛水--BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //取水--如果不为空就一直取
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();

            try {
                // 从服务器端获取Json字符串
                String backlogJsonStr = buffer.toString();
                backlogJsonStr = backlogJsonStr.substring(backlogJsonStr.indexOf("{"), backlogJsonStr.lastIndexOf("}") + 1);
                backlogJsonStr = backlogJsonStr.replace("\\\"", "'");
                JSONObject jsonObject = new JSONObject(backlogJsonStr);

                if (jsonObject.getString("status").equals("yes")) {
                    //学生答案
                    AnswerActivityTea.answer_sjOrQd = jsonObject.getString("answer");
                } else {
                    AnswerActivityTea.answer_sjOrQd = "";
                }
                Log.d("wenbug2", "【教师端】=>接收答案【返回数据】" + jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
