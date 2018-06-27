package com.seven.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.seven.framework.R;


/**
 * Created by wangbin on 2017/3/7.
 */

public class SquareInputEditView extends android.support.v7.widget.AppCompatEditText {
    private int mLineColor;
    private float mSqureWidth;
    private int mSeparateLineWidth;
    private int mTextLength = 6;

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
        initPaint();
        invalidate();
    }

    public float getSqureWidth() {
        return mSqureWidth;
    }

    public void setSqureWidth(float squreWidth) {
        mSqureWidth = squreWidth;
        initPaint();
        invalidate();
    }

    public int getSeparateLineWidth() {
        return mSeparateLineWidth;
    }

    public void setSeparateLineWidth(int separateLineWidth) {
        mSeparateLineWidth = separateLineWidth;
        initPaint();
        invalidate();
    }

    public int getTextLength() {
        return mTextLength;
    }

    public void setTextLength(int textLength) {
        mTextLength = textLength;
        initPaint();
        invalidate();
    }

    private Paint textPaint = new Paint();
    private Paint mSquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSeparatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String mCharSequenceContent;

    public SquareInputEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SqureInputView, 0, 0);
        try {
            mTextLength = a.getInteger(R.styleable.SqureInputView_textnum, 6);
            mLineColor = a.getColor(R.styleable.SqureInputView_line_color, Color.parseColor("#979797"));
            mSqureWidth = a.getDimension(R.styleable.SqureInputView_squre_width, 2);
            mSeparateLineWidth = a.getInt(R.styleable.SqureInputView_separate_line_width, 1);
        } finally {
            a.recycle();
        }
        initPaint();
        setSingleLine(true);
    }

    private void initPaint() {
        mSquarePaint.setStrokeWidth(mSqureWidth);
        mSquarePaint.setColor(mLineColor);
        mSquarePaint.setStyle(Paint.Style.STROKE);

        mSeparatePaint.setStrokeWidth(mSeparateLineWidth);
        mSeparatePaint.setColor(mLineColor);
        mSeparatePaint.setStyle(Paint.Style.FILL);

        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(getTextSize());
        textPaint.setColor(getCurrentTextColor());
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        // 画矩形
        canvas.drawRect(0, 0, width, height, mSquarePaint);
        // 分割线
        for (int i = 1; i < mTextLength; i++) {
            float x = width * i / mTextLength;
            canvas.drawLine(x, 0, x, height, mSeparatePaint);
        }
        // 密码
        float cx, cy = height / 2;
        float half = width / mTextLength / 2;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        cy = cy - top / 2 - bottom / 2;
        if (mCharSequenceContent != null) {
            for (int i = 0; i < mCharSequenceContent.length(); i++) {
                cx = width * i / mTextLength + half;
                canvas.drawText(mCharSequenceContent.charAt(i) + "", cx, cy, textPaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mTextLength != 0) {
            if (text.length() == mTextLength && !isReSet) {
                if (mInputCompliteListener != null)
                    mInputCompliteListener.onCompliteInput(text.toString());
            }
            if (text.length() > mTextLength) {
                isReSet = true;
                setText(mCharSequenceContent);
                setSelection(mCharSequenceContent.length());
                return;
            }
        }
        mCharSequenceContent = text.toString();
        isReSet = false;
        invalidate();
    }

    private boolean isReSet;
    private InputCompliteListener mInputCompliteListener;

    public void setInputCompliteListener(InputCompliteListener inputCompliteListener) {
        mInputCompliteListener = inputCompliteListener;
    }

    public interface InputCompliteListener {
        void onCompliteInput(String code);
    }

}
