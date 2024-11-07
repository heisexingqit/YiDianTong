#include "com_example_yidiantong_util_LameUtil.h"
#include "./lame_libmp3lame/lame.h"  // 需要确保 LAME 库的头文件路径正确

// 声明一个 lame_global_flags 指针，保存 LAME 实例
static lame_t lame = NULL;

// 初始化 LAME 编码器
JNIEXPORT void JNICALL Java_com_example_yidiantong_util_LameUtil_init
  (JNIEnv *env, jclass cls, jint inSamplerate, jint inChannel, jint outSamplerate, jint outBitrate, jint quality) {
    // 初始化 LAME 全局实例
    if (lame != NULL) {
        lame_close(lame);  // 确保多次初始化时不会泄漏之前的实例
    }
    lame = lame_init();
    lame_set_in_samplerate(lame, inSamplerate);
    lame_set_num_channels(lame, inChannel);
    lame_set_out_samplerate(lame, outSamplerate);
    lame_set_brate(lame, outBitrate);
    lame_set_quality(lame, quality);
    lame_init_params(lame);
}

// 编码 PCM 数据为 MP3
JNIEXPORT jint JNICALL Java_com_example_yidiantong_util_LameUtil_encode
  (JNIEnv *env, jclass cls, jshortArray bufferLeft, jshortArray bufferRight, jint samples, jbyteArray mp3buf) {
    jshort *left = (*env)->GetShortArrayElements(env, bufferLeft, NULL);
    jshort *right = (*env)->GetShortArrayElements(env, bufferRight, NULL);
    jbyte *mp3 = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int mp3buf_size = (*env)->GetArrayLength(env, mp3buf);

    // 编码 PCM 数据到 MP3
    int result = lame_encode_buffer(lame, left, right, samples, (unsigned char*)mp3, mp3buf_size);

    // 释放 JNI 数组
    (*env)->ReleaseShortArrayElements(env, bufferLeft, left, 0);
    (*env)->ReleaseShortArrayElements(env, bufferRight, right, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf, mp3, 0);

    return result;
}

// 刷新 LAME 缓存，获取剩余 MP3 数据
JNIEXPORT jint JNICALL Java_com_example_yidiantong_util_LameUtil_flush
  (JNIEnv *env, jclass cls, jbyteArray mp3buf) {
    jbyte *mp3 = (*env)->GetByteArrayElements(env, mp3buf, NULL);
    int mp3buf_size = (*env)->GetArrayLength(env, mp3buf);

    // 刷新 LAME 缓存并获取剩余的 MP3 数据
    int result = lame_encode_flush(lame, (unsigned char*)mp3, mp3buf_size);

    // 释放 JNI 数组
    (*env)->ReleaseByteArrayElements(env, mp3buf, mp3, 0);

    return result;
}

// 关闭 LAME 编码器
JNIEXPORT void JNICALL Java_com_example_yidiantong_util_LameUtil_close
  (JNIEnv *env, jclass cls) {
    if (lame != NULL) {
        lame_close(lame);
        lame = NULL;
    }
}
