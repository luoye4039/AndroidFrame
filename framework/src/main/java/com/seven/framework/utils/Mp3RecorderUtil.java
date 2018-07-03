package com.seven.framework.utils;

import com.czt.mp3recorder.MP3Recorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Mp3格式音频录制
 */

public class Mp3RecorderUtil {
    private static Mp3RecorderUtil sMp3RecordUtil;
    private Mp3RecorderStateListener mMp3RecorderStateListener;
    private MP3Recorder mMp3Recorder;
    private Disposable mMaxRecordTimeDisposable;
    private Disposable mDetDBValueDisposable;

    private Mp3RecorderUtil() {
    }

    public static Mp3RecorderUtil getInstance() {
        if (sMp3RecordUtil == null) {
            synchronized (Mp3RecorderUtil.class) {
                if (sMp3RecordUtil == null)
                    sMp3RecordUtil = new Mp3RecorderUtil();
            }
        }
        return sMp3RecordUtil;
    }

    /**
     * 开始录制
     *
     * @param filePath                 录音文件地址
     * @param maxRecordTime            最大录音时间  0 没有最大时长
     * @param mp3RecorderStateListener 录音监听
     * @return 开始录音是否成功
     */
    public boolean startRecorder(String filePath, int maxRecordTime, Mp3RecorderStateListener mp3RecorderStateListener) {
        File voiceFile = createRecordFile(filePath);
        if (voiceFile == null)
            return false;
        mMp3Recorder = new MP3Recorder(voiceFile);
        try {
            mMp3Recorder.start();
            mMp3RecorderStateListener = mp3RecorderStateListener;
            if (maxRecordTime > 0) {
                startMaxRecordTime(maxRecordTime);
            }
            startGetDBValue();
            return true;
        } catch (IOException e) {
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
     * 停止后需要等会生成完成录音文件
     */
    public void stopRecorder() {
        if (mMp3Recorder != null && mMp3Recorder.isRecording()) {
            mMp3Recorder.stop();
        }
        stopMaxRecordTime();
        stopGetDBValue();
        mMp3Recorder = null;
        mMp3RecorderStateListener = null;
    }


    public boolean isRecording() {
        if (mMp3Recorder != null)
            return mMp3Recorder.isRecording();
        return false;
    }


    /**
     * 开始最大录音时长定时
     *
     * @param maxRecordTime
     */
    private void startMaxRecordTime(int maxRecordTime) {
        mMaxRecordTimeDisposable = Observable.timer(maxRecordTime, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                if (mMp3RecorderStateListener != null) {
                    mMp3RecorderStateListener.onMaxRecordTimeout();
                }
                stopRecorder();
            }
        });

    }

    /**
     * 停止最大录音时长定时
     */
    private void stopMaxRecordTime() {
        if (mMaxRecordTimeDisposable != null && !mMaxRecordTimeDisposable.isDisposed())
            mMaxRecordTimeDisposable.dispose();
        mMaxRecordTimeDisposable = null;
    }

    /**
     * 开始获取录音分贝
     */
    private void startGetDBValue() {
        mDetDBValueDisposable = Observable.interval(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                if (mMp3Recorder != null && mMp3Recorder.isRecording() && mMp3RecorderStateListener != null) {
                    mMp3RecorderStateListener.onDbValue(mMp3Recorder.getRealVolume());
                }
            }
        });
    }

    /**
     * 停止获取录音分贝
     */
    private void stopGetDBValue() {
        if (mDetDBValueDisposable != null && !mDetDBValueDisposable.isDisposed()) {
            mDetDBValueDisposable.dispose();
            mDetDBValueDisposable = null;
        }
    }

    public interface Mp3RecorderStateListener {
        void onDbValue(int dbValue);

        void onMaxRecordTimeout();

    }

}
