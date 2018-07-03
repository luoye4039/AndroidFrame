package com.seven.framework.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;

/**
 * MediaRecorder 录音
 * Created by wangbin on 2017/5/20.
 */

public class MediaRecorderUtil {
    private final static int SPACE = 500;// 间隔取样时间
    private static MediaRecorderUtil sMediaRecorderUtil;
    private boolean isRecording;
    private MediaRecorder mRecorder;
    private MediaRecorderStateListener mMediaRecorderStateListener;
    private Disposable mDisposable;

    public boolean isRecording() {
        return isRecording;
    }

    private MediaRecorderUtil() {
    }

    public static MediaRecorderUtil getInstance() {
        if (sMediaRecorderUtil == null) {
            synchronized (MediaRecorderUtil.class) {
                if (sMediaRecorderUtil == null) {
                    sMediaRecorderUtil = new MediaRecorderUtil();
                }
            }
        }
        return sMediaRecorderUtil;
    }

    /**
     * 初始化录音
     *
     * @return
     */
    private boolean initMedia() {
        mRecorder = new MediaRecorder();
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            return true;
        } catch (RuntimeException e) {
            LogUtils.i("初始化失败" + e.toString());
            return false;
        }
    }

    /**
     * 开始录制
     *
     * @param filePath 文件保存地址
     */
    public boolean startRecorder(String filePath, long maxRecordTime, MediaRecorderStateListener mediaRecorderStateListener) {
        if (!initMedia()) {
            return false;
        }
        File voiceFile = createRecordFile(filePath);
        if (voiceFile == null)
            return false;

        mRecorder.setOutputFile(voiceFile.getAbsolutePath());
        if (maxRecordTime > 0) {
            mRecorder.setMaxDuration((int) maxRecordTime);
            mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        if (mMediaRecorderStateListener != null) {
                            mMediaRecorderStateListener.maxTimeRunOut();
                        }
                    }
                }
            });
        }
        try {
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
            mMediaRecorderStateListener = mediaRecorderStateListener;
            startGetDBValue();
            return true;
        } catch (Exception e) {
            LogUtils.i("开始录音失败" + e.toString());
            return false;
        }
    }


    /**
     * 创建录音文件
     *
     * @param filePath
     * @return
     */
    private File createRecordFile(String filePath) {
        File voiceFile = new File(filePath);
        if (voiceFile.exists()) {
            try {
                if (voiceFile.delete())
                    voiceFile.createNewFile();
            } catch (IOException e) {
                LogUtils.i("创建录音文件失败" + e.toString());
                voiceFile = null;
            }
        } else {
            try {
                voiceFile.createNewFile();
            } catch (IOException e) {
                LogUtils.i("创建录音文件失败" + e.toString());
                voiceFile = null;
            }
        }
        return voiceFile;
    }

    /**
     * 停止录制
     */
    public void stopRecorder() {
        try {
            if (isRecording) {
                mRecorder.setOnInfoListener(null);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
                isRecording = false;
                mMediaRecorderStateListener = null;
                stopGetDBValue();
            }
        }
    }

    /**
     * 开始获取录音分贝
     */
    private void startGetDBValue() {
        mDisposable = Observable.interval(SPACE, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mRecorder != null) {
                            int ratio = mRecorder.getMaxAmplitude();
                            if (ratio > 1)
                                ratio = (int) (20 * Math.log10(ratio));
                            if (mMediaRecorderStateListener != null)
                                mMediaRecorderStateListener.onDbValue(ratio);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 停止获取录音分贝
     */
    private void stopGetDBValue() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public interface MediaRecorderStateListener {
        void onDbValue(int dbValue);

        void maxTimeRunOut();
    }
}
