package com.pangff.demoview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {

	private Paint mTextPaint;
	private String mText;
	private int mAscent;
	
	public MyView(Context context) {
		super(context);
		initLabelView();
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLabelView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
	}

	
	public void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		invalidate();
	}


	private final void initLabelView() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(16);
		mTextPaint.setColor(0xFF000000);
		setPadding(3, 3, 3, 3);
	}

	/**
	 * 计算view的宽度
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {//match_parent或具体数值,直接使用
			result = specSize;
		} else {//否则自己计算
			// 计算文字宽度
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {//wrap_content
				
				//取specSize和计算出的文字宽度最小数值，如果result大于specSize说明文字超出了view宽度范围
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 计算view的高度
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {//match_parent或具体数值,直接使用
			// We were told how big to be
			result = specSize;
		} else {//否则自己计算
			// 计算文字高度
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				//取specSize和计算出的文字高度最小数值，如果result大于specSize说明文字超出了view高度范围
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 绘制视图
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent,mTextPaint);
	}

}
