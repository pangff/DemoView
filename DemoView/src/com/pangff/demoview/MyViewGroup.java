package com.pangff.demoview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyViewGroup extends ViewGroup {


  private VelocityTracker velocityTracker;
  private final int maximumVelocity;
  int downX;// 按下的x坐标
  int downY;// 按下的x坐标
  private Scroller mScroller;
  private static final int VELOCITY = 50;//最小触发自动滑动的速度
  private View currentView;
  private View fistView;
  private View lastView;
  private int mTouchSlop;
  private boolean mIsBeingDragged = true;
  public MyViewGroup(Context context) {
    this(context, null);
  }

  public MyViewGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    mScroller = new Scroller(getContext());
    mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    final ViewConfiguration configuration = ViewConfiguration.get(context);
    this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    for (int i = 0; i < this.getChildCount(); i++) {
      View child = this.getChildAt(i);
      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();
      child.layout(l, t, l + childWidth, t + childHeight);
    }
    if(currentView==null){
      lastView = this.getChildAt(this.getChildCount()-1);
      fistView = this.getChildAt(0);
      currentView = lastView;
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    measureChildren(widthMeasureSpec, heightMeasureSpec);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain(); // 初始化速度追踪器
    }
    velocityTracker.addMovement(event); // 添加事件到速度追踪器中
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (!mScroller.isFinished()) {
          mScroller.abortAnimation();
        }
        downX = (int) event.getX();
        break;
      case MotionEvent.ACTION_MOVE:
        if(mIsBeingDragged){
          int dx = (int) (downX - event.getX());
          downX = (int) event.getX();
          
          float oldScrollX = getChildAt(1).getScrollX();
          float scrollX = oldScrollX + dx;
          if(scrollX>getMaxScrollX()){
            scrollX = getMaxScrollX();
          }
          if(scrollX<0){
            scrollX = 0;
          }
          getChildAt(1).scrollTo((int)scrollX,(int)getChildAt(1).getScrollY());
        }
        break;
      case MotionEvent.ACTION_UP:
        final VelocityTracker velocityTracker = this.velocityTracker;
        velocityTracker.computeCurrentVelocity(100, maximumVelocity);//计算当前速度(按1秒为单位)
        int velocityX = (int) velocityTracker.getXVelocity();//获取x方向速度
        int lastScrollX = getChildAt(1).getScrollX();
        int rangX = 0;
        if (velocityX > VELOCITY) {//正向速度 是手从左向右滑动
          rangX = -lastScrollX;
        }else if(velocityX < -VELOCITY){//负向速度，是手从右向左滑动
          rangX = getMaxScrollX() - lastScrollX;
        }else if(lastScrollX>getMaxScrollX()/2){//打开
          rangX = getMaxScrollX() - lastScrollX;
        }else{
          rangX = - lastScrollX;
        }
        smoothScrollTo(rangX);
        if(velocityTracker!=null){
          velocityTracker.recycle();
          this.velocityTracker = null;
        }
        break;
      default:
        break;
    }
    return true;
  }
  
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    final int action = ev.getAction();
    final float x = ev.getX();
    final float y = ev.getY();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mIsBeingDragged = false;
        downX = (int) x;
        downY = (int) y;
        break;
      case MotionEvent.ACTION_MOVE:
        final float dx = x - downX;
        final float xDiff = Math.abs(dx);
        final float yDiff = Math.abs(y - downY);
        if (xDiff > mTouchSlop && xDiff > yDiff) {
          mIsBeingDragged = true;
          if(dx>0){//view向左打开
            if(currentView.getScrollX()==0){
              changeView();
            }
            if(currentView.getScrollX()==getMaxScrollX()){
              resetView();
            }
          }
          if(dx<0){//view从左向右还原
            if(currentView.getScrollX()==getMaxScrollX()){
              changeView();
            }
            if(currentView.getScrollX()==0){
              resetView();
            }
          }
        }else{
          mIsBeingDragged = false;
        }
        break;
      default:
        break;
    }
    return mIsBeingDragged;
  }
  
  @Override
  public void computeScroll() {
    if (!mScroller.isFinished() && currentView != null) {
      if (mScroller.computeScrollOffset()) {
        int oldX = currentView.getScrollX();
        int oldY = currentView.getScrollY();
        int x = mScroller.getCurrX();
        int y = mScroller.getCurrY();
        if (oldX != x || oldY != y) {
          currentView.scrollTo(x, y);
        }
        invalidate();
      }
    }else if(mScroller.isFinished()){
      if(currentView.getScrollX()==0){
        Log.e("dddd", "向左滑动结束");
      }
      if(currentView.getScrollX()==getMaxScrollX()){
        Log.e("dddd", "向右滑动结束");
      }
      currentView.bringToFront();
    }
  }
  
  private void changeView(){
    if(currentView.equals(fistView)){
      Log.e("dddddd", "底层替换上层");
      currentView = lastView;
      currentView.bringToFront();
      fistView.scrollTo(0, this.getChildAt(0).getScrollY());
    }else{
      Log.e("dddddd", "上层替换底层");
      currentView = fistView;
      currentView.bringToFront();
      lastView.scrollTo(0, this.getChildAt(1).getScrollY());
    }
  }
  
  private void resetView(){
    if(currentView.equals(fistView)){
      lastView.scrollTo(0, lastView.getScrollY());
    }else{
      fistView.scrollTo(0, fistView.getScrollY());
    }
  }
  
  void smoothScrollTo(int dx) {
    int duration = 500;
    int oldScrollX = getChildAt(1).getScrollX();
    mScroller.startScroll(oldScrollX, getChildAt(1).getScrollY(), dx, getChildAt(1).getScrollY(),duration);
    invalidate();
  }
  
  public void resultScroll(){
    if(currentView.getScrollX()>=getMaxScrollX()/2){
      scrollBy(getMaxScrollX()-currentView.getScrollX(),0);
    }else{
      scrollBy(-currentView.getScrollX(),0);
    }
  }
  
  /**
   * 获取最大的滑动距离
   * @return
   */
  public int getMaxScrollX() {
   return currentView.getMeasuredWidth();
  }

}
