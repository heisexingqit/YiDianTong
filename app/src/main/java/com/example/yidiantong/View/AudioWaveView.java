package com.example.yidiantong.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.yidiantong.util.LameUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioWaveView extends View {

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    );
    private AudioRecord audioRecord;
    private short[] buffer = new short[BUFFER_SIZE];
    private boolean isRecording = false;
    private Paint paint;
    private Path wavePath;
    private Context mContext;
    private File outputMp3File;

    private FileOutputStream mp3FileOutputStream;

    private byte[] mp3Buffer; // 用于存储编码后的 MP3 数据


    public AudioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @SuppressLint("MissingPermission")
    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#59b9e0"));
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE); // 仅绘制线条
        paint.setAntiAlias(true); // 启用抗锯齿

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE
        );
    }

    public void startRecording(String audioPath) {
        this.outputMp3File = new File(audioPath);
        isRecording = true;

        // 初始化 LAME
        LameUtil.init(SAMPLE_RATE, 1, SAMPLE_RATE, 128, 5); // 比特率设置为128kbps，质量为5

        // 初始化 MP3 文件输出流
        try {
            mp3FileOutputStream = new FileOutputStream(outputMp3File);
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioRecord.startRecording();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRecording) {
                    int readSize = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    if (readSize > 0) {
                        encodeToMp3(buffer, readSize);
                    }
                }
                // 停止录音时刷新缓冲区并关闭编码器
                flushAndCloseLame();
            }
        }).start();
    }

    private void encodeToMp3(short[] pcmBuffer, int readSize) {
        // mp3buf 必须足够大，建议的大小是 7200 + (1.25 * 读入的 PCM 数据大小)
        if (mp3Buffer == null) {
            mp3Buffer = new byte[7200 + (int)(1.25 * readSize)];
        }

        // 调用 LameUtil.encode() 编码 PCM 数据为 MP3
        int encodedSize = LameUtil.encode(pcmBuffer, pcmBuffer, readSize, mp3Buffer);

        if (encodedSize > 0) {
            try {
                // 将编码后的 MP3 数据写入文件
                mp3FileOutputStream.write(mp3Buffer, 0, encodedSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void flushAndCloseLame() {
        // 刷新 LAME 缓冲区
        int flushResult = LameUtil.flush(mp3Buffer);
        if (flushResult > 0) {
            try {
                // 写入剩余的 MP3 数据
                mp3FileOutputStream.write(mp3Buffer, 0, flushResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 关闭文件流
        try {
            mp3FileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭 LAME 编码器
        LameUtil.close();
    }

    //  原始的实现方式 startRecording
//    public void startRecording(String audioPath) {
//        this.outputMp3File = new File(audioPath);
//        isRecording = true;
//        audioRecord.startRecording();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isRecording) {
//                    int readSize = audioRecord.read(buffer, 0, BUFFER_SIZE);
//                    Log.e("wen", "run: " + readSize);
//                }
//            }
//        }).start();
//    }


    public void stopRecording() {
        isRecording = false;
        audioRecord.stop();
    }


    private float phase = 0f;
    private float speed = 4f; // 调整移动速度

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        float centerY = getHeight() / 2f;
        float maxAmplitude = getHeight() / 2.0f; // 调整为半高以增大振幅
        float scale = 4.0f * maxAmplitude / Short.MAX_VALUE; // 增大缩放比例，增加敏感度

        // 计算音量的平均值，用于调整振幅
        float sum = 0;
        for (short value : buffer) {
            sum += Math.abs(value);
        }

        float averageVolume = sum / buffer.length;

        // 调整振幅，使其随音量变化
        float dynamicAmplitude = Math.min(maxAmplitude, averageVolume * scale);

        // 定义 Path 并将起点移动到左端的中间线位置
        Path path = new Path();
        path.moveTo(0, centerY);

        // 正弦波参数 - 提高频率增加波的数量
        float frequency = 3f; // 增加频率（波的数量）

        // 绘制固定两端的正弦波形
        for (int i = 0; i < getWidth(); i++) {
            float x = i;

            // 计算一个因子，用于将两端的振幅平滑过渡到零
            float amplitudeFactor = (float) Math.sin(Math.PI * i / getWidth()); // 振幅过渡因子，保证两端为零

            // 根据振幅因子调整 Y 轴的动态振幅
            float y = centerY - (dynamicAmplitude * amplitudeFactor) * (float) Math.sin((i * frequency * 2 * Math.PI / getWidth()) + phase);

            path.lineTo(x, y);
        }

        // 确保路径的终点与右端点的中心线平滑连接
        path.lineTo(getWidth(), centerY);

        // 绘制路径
        canvas.drawPath(path, paint);

        // 更新水平位移，减小步长以减慢移动速度
        phase -= speed * 0.05f; // 调整步长

        // 使波形不断刷新，达到动画效果
        invalidate();
    }
}