package com.seven.framework.widget.loadingindicatorview.indicator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by Jack on 2015/10/19.
 */
public class BallTrianglePathIndicator extends BaseIndicatorController {

    float[] translateX=new float[3],translateY=new float[3];

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < 3; i++) {

            if (i ==0 ){
                paint.setColor(Color.parseColor("#efcc4f"));
            }if (i ==1 ){
                paint.setColor(Color.parseColor("#00b8ee"));
            }
            if (i ==2 ){
                paint.setColor(Color.parseColor("#58a663"));
            }

            canvas.save();
            canvas.translate(translateX[i], translateY[i]);
            canvas.drawCircle(0, 0, getWidth() / 7, paint);
            canvas.restore();
        }
    }

    @Override
    public void createAnimation() {
        float startX=getWidth()/7;
        float startY=getWidth()/7;
        for (int i = 0; i < 3; i++) {
            final int index=i;
            ValueAnimator translateXAnim=ValueAnimator.ofFloat(getWidth()/2,getWidth()-startX,startX,getWidth()/2);
            if (i==1){
                translateXAnim=ValueAnimator.ofFloat(getWidth()-startX,startX,getWidth()/2,getWidth()-startX);
            }else if (i==2){
                translateXAnim=ValueAnimator.ofFloat(startX,getWidth()/2,getWidth()-startX,startX);
            }
            ValueAnimator translateYAnim=ValueAnimator.ofFloat(startY,getHeight()-startY,getHeight()-startY,startY);
            if (i==1){
                translateYAnim=ValueAnimator.ofFloat(getHeight()-startY,getHeight()-startY,startY,getHeight()-startY);
            }else if (i==2){
                translateYAnim=ValueAnimator.ofFloat(getHeight()-startY,startY,getHeight()-startY,getHeight()-startY);
            }

            translateXAnim.setDuration(2000);
            translateXAnim.setInterpolator(new LinearInterpolator());
                translateXAnim.setRepeatCount(-1);
            translateXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    translateX [index]= (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            translateXAnim.start();

            translateYAnim.setDuration(2000);
            translateYAnim.setInterpolator(new LinearInterpolator());
            translateYAnim.setRepeatCount(-1);
            translateYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    translateY [index]= (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            translateYAnim.start();
        }
    }


}
