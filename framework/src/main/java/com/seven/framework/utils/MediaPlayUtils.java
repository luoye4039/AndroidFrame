package com.seven.framework.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.seven.framework.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 媒体音乐播放util
 * C
 */

public class MediaPlayUtils {
    private MediaPlayer mPlayer;
    private static MediaPlayUtils sMediaUtils;
    private OnPlayProgressListener mOnPlayProgressListener;
    private Disposable mDisposable;

    private MediaPlayUtils() {

    }

    public static MediaPlayUtils getInstance() {
        if (sMediaUtils == null) {
            synchronized (MediaPlayUtils.class) {
                if (sMediaUtils == null) {
                    sMediaUtils = new MediaPlayUtils();
                }
            }
        }
        return sMediaUtils;
    }


    /**
     * 播放SDfile
     *
     * @param filePath
     * @return
     */
    public boolean playerLocalFile(Context context, String filePath, boolean isEarpiece) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        smallVolumeTips(context);
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                stopPalyer();
            }
        }
        int streamType = AudioManager.STREAM_MUSIC;
        if (isEarpiece) {
            streamType = AudioManager.STREAM_VOICE_CALL;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(streamType);
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
            startPlayProgrssListener();
            LogUtils.i("开始报播放");
            return true;
        } catch (IOException e) {
            LogUtils.i("播放失败");
            return false;
        }
    }


    /**
     * 播放网络资源
     *
     * @param context
     * @param fileUrl    文件网络地址
     * @param isEarpiece 是否插入耳机
     * @return
     */
    public boolean playerNetUrl(Context context, String fileUrl, boolean isEarpiece) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (TextUtils.isEmpty(fileUrl)) {
            return false;
        }
        smallVolumeTips(context);
        int streamType = AudioManager.STREAM_MUSIC;
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                stopPalyer();
            }
        }
        if (isEarpiece) {
            streamType = AudioManager.STREAM_VOICE_CALL;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(streamType);
        try {
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                //网络不可用
                ToastUtil.showMessage("请检查网络");
                return false;
            }
            mPlayer.setDataSource(context, Uri.parse(fileUrl));
            mPlayer.prepare();
            mPlayer.start();
            startPlayProgrssListener();
            LogUtils.i("开始报播放");
            return true;
        } catch (IOException e) {
            LogUtils.i("播放失败");
            return false;
        }
    }


    /**
     * 播放资源文件
     *
     * @param context
     * @param resounreId 资源id
     * @param isEarpiece 是否插入耳机
     * @return 播放结果
     */
    public boolean playerResounreID(Context context, int resounreId, boolean isEarpiece, boolean isReplay) {
        int streamType = AudioManager.STREAM_MUSIC;
        smallVolumeTips(context);
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                stopPalyer();
            }
        }
        if (isEarpiece) {
            streamType = AudioManager.STREAM_VOICE_CALL;
        }
        mPlayer = MediaPlayer.create(context, resounreId);
        mPlayer.setAudioStreamType(streamType);
        if (isReplay) {
            mPlayer.setLooping(true);
        }
        try {
            mPlayer.start();
            startPlayProgrssListener();
            LogUtils.i("开始报播放");
            return true;
        } catch (Exception e) {
            LogUtils.i("播放失败");
            return false;
        }
    }


    /**
     * 提示音量较小
     *
     * @param context
     */
    private void smallVolumeTips(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (current == 0) {
            ToastUtil.showMessage(R.string.tips_small_volume);
        }
    }

    /**
     * 是否在播放中
     *
     * @return
     */
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        } else {
            return false;
        }
    }


    /**
     * 跳转播放
     *
     * @param seekToTime
     */
    public void seekTo(int seekToTime) {
        if (mPlayer != null && isPlaying()) {
            mPlayer.seekTo(seekToTime);
            mPlayer.start();
        }
    }

    /**
     * 停止播放
     */
    public void pausePlay() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }
    }

    /**
     * 开始播放
     */
    public void reStartPlay() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    /**
     * 停止播放音频
     */
    public void stopPalyer() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.release();
                stopPlayProgrssListener();
                mPlayer = null;
            }
        }
    }

    /**
     * 设置音频播完回掉
     *
     * @param onCompletionListener
     */
    public void setPlayerOnCompletionListener(final MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mPlayer != null) {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    onCompletionListener.onCompletion(mediaPlayer);
                    stopPlayProgrssListener();
                }
            });
        }
    }


    /**
     * 监听播放进度
     *
     * @param onPlayProgressListener
     */
    public void setOnPlayProgressListener(OnPlayProgressListener onPlayProgressListener) {
        mOnPlayProgressListener = onPlayProgressListener;
    }


    /**
     * 开始监听播放进度
     */
    private void startPlayProgrssListener() {
        mDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mOnPlayProgressListener != null && mPlayer != null && isPlaying()) {
                            mOnPlayProgressListener.onProgress(mPlayer.getCurrentPosition());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 停止监听播放进度
     */
    private void stopPlayProgrssListener() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public interface OnPlayProgressListener {
        void onProgress(int currentProgress);
    }

}
