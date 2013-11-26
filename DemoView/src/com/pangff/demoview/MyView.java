package com.pangff.demoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

@SuppressLint("NewApi")
public class MyView extends View {

	private Paint mTextPaint;
	private String mText;
	private int mAscent;

	private final Flinger flinger;
	private VelocityTracker velocityTracker;
	private final int minimumVelocity;
	private final int maximumVelocity;
	private int maxWidth;
	private int viewWidth;
	int downX;//按下的x坐标

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLabelView();
		this.flinger = new Flinger(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		this.minimumVelocity = configuration.getScaledMinimumFlingVelocity();
		this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
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

	/**
	 * 视图初始化
	 */
	private final void initLabelView() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(16);
		mTextPaint.setColor(0xFF000000);
		setPadding(3, 3, 3, 3);
	}

	/**
	 * 计算view的宽度
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		viewWidth = specSize;
		if (specMode == MeasureSpec.EXACTLY) {// match_parent或具体数值,直接使用
			result = specSize;
		} else {// 否则自己计算
			// 计算文字宽度
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			maxWidth = result;
			if (specMode == MeasureSpec.AT_MOST) {// wrap_content

				// 取specSize和计算出的文字宽度最小数值，如果result大于specSize说明文字超出了view宽度范围
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 计算view的高度
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {// match_parent或具体数值,直接使用
			// We were told how big to be
			result = specSize;
		} else {// 否则自己计算
			// 计算文字高度
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// 取specSize和计算出的文字高度最小数值，如果result大于specSize说明文字超出了view高度范围
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
		canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent,
				mTextPaint);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (velocityTracker == null) { 
			velocityTracker = VelocityTracker.obtain(); // 初始化速度追踪器
		}
		velocityTracker.addMovement(event); // 添加事件到速度追踪器中
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getX();
			if (!flinger.isFinished()) { // 如果正在滚动马上停止
				flinger.forceFinished();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) (downX - event.getX());
			scrollBy(dx, 0);
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = this.velocityTracker;
			velocityTracker.computeCurrentVelocity(1000, maximumVelocity);//计算当前速度(按1秒为单位)
			int velocityX = (int) velocityTracker.getXVelocity();//获取x方向速度
			int velocityY = (int) velocityTracker.getYVelocity();//获取y方向速度

			if (Math.abs(velocityX) > minimumVelocity
					|| Math.abs(velocityY) > minimumVelocity) {
				flinger.start(getScrollX(), getScrollY(), velocityX, 0,
						getMaxScrollX(), 0);
			} else {// 记得回收
				if (this.velocityTracker != null) {
					this.velocityTracker.recycle();
					this.velocityTracker = null;
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 对超出范围进行判断
	 */
	public void scrollBy(int dx, int dy) {
		if (getScrollX() + dx > getMaxScrollX()) {//超出最大范围
			super.scrollBy(getMaxScrollX() - getScrollX(), 0);
		} else if (getScrollX() + dx < 0) {//超出最小范围
			super.scrollBy(-getScrollX(), 0);
		} else {
			super.scrollBy(dx, 0);
		}
	}

	/**
	 * 获取最大的滑动距离
	 * 
	 * @return
	 */
	public int getMaxScrollX() {
		if (maxWidth - viewWidth > 0) {
			return (maxWidth - viewWidth);
		} else {
			return 0;
		}
	}

	/**
	 * 控制滚动的线程
	 * @author pangff
	 */
	private class Flinger implements Runnable {
		private final Scroller scroller;
		private int lastX = 0;
		private int lastY = 0;

		Flinger(Context context) {
			scroller = new Scroller(context);
		}

		void start(int initX, int initY, int initialVelocityX,
				int initialVelocityY, int maxX, int maxY) {
			scroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0,maxX, 0, maxY);
			lastX = initX;
			lastY = initY;
			post(this);
		}

		public void run() {
			if (scroller.isFinished()) {
				return;
			}
			boolean more = scroller.computeScrollOffset();//获取是否需要继续滑动
			int x = scroller.getCurrX();//获取滑动中的当前scrollX
			int y = scroller.getCurrY();//获取滑动中的当前scrollY
			int diffX = lastX - x;//取增量
			int diffY = lastY - y;//取增量
			if (diffX != 0 || diffY != 0) {
				scrollBy(diffX, diffY);
				lastX = x;//纪录当前位置
				lastY = y;//纪录当前位置
			}
			if (more) {//如果还需要继续滑动，再次执行
				post(this);
			}
		}

		boolean isFinished() {
			return scroller.isFinished();
		}

		void forceFinished() {
			if (!scroller.isFinished()) {
				scroller.forceFinished(true);
			}
		}
	}

}
