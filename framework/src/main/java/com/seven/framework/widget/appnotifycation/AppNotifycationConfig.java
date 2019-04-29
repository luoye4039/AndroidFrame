package com.seven.framework.widget.appnotifycation;

import android.view.animation.Animation;

/**
 * app内通知配置
 */
public class AppNotifycationConfig {

    private long showTime;
    private Animation mEnterAnimation;
    private Animation mOutAnimation;

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public Animation getEnterAnimation() {
        return mEnterAnimation;
    }

    public void setEnterAnimation(Animation enterAnimation) {
        mEnterAnimation = enterAnimation;
    }

    public Animation getOutAnimation() {
        return mOutAnimation;
    }

    public void setOutAnimation(Animation outAnimation) {
        mOutAnimation = outAnimation;
    }

    public static class Builder {

        private final AppNotifycationConfig mAppNotifycationConfig;

        public Builder() {
            mAppNotifycationConfig = new AppNotifycationConfig();
        }

        public Builder setShowTime(long showTime) {
            mAppNotifycationConfig.setShowTime(showTime);
            return this;
        }

        public Builder setEnterAnimation(Animation enterAnimation) {
            mAppNotifycationConfig.setEnterAnimation(enterAnimation);
            return this;
        }

        public Builder setOutAnimation(Animation outAnimation) {
            mAppNotifycationConfig.setOutAnimation(outAnimation);
            return this;
        }

        public AppNotifycationConfig build() {
            return mAppNotifycationConfig;
        }

    }
}
