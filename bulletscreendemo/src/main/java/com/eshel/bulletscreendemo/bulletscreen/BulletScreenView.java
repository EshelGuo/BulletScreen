package com.eshel.bulletscreendemo.bulletscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * 项目名称: Demo
 * 创建人: Eshel
 * 创建时间:2017/8/3 09时15分
 * 描述: TODO
 */

public class BulletScreenView extends RelativeLayout{
	private int scrollVelocity = 1;
	private ViewGroup mParent;
	private BulletScreenScrollListener mListener;
	private ScrollTask mScrollTask;

	public BulletScreenView(Context context) {
		this(context,null);
	}

	public BulletScreenView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public BulletScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {}
	public void startScroll(){
		if(mScrollTask == null)
			mScrollTask = new ScrollTask(mParent);
		if(mParent == null) {
			mParent = (ViewGroup) getParent();
		}
		if(mParent != null) {
			mScrollTask.start(mParent);
			post(mScrollTask);
		}else {
			throw new NullPointerException("parent 不能为空");
		}
	}
	public void startScroll(ScrollTask task){
		if(mParent == null)
			mParent = (ViewGroup) getParent();
		if(mParent != null && task != null) {
			if(mListener != null)
				mListener.startScroll();
			post(task);
		}
	}
private int mCurrentLocationX;
private int mCurrentLocationY;
	public class ScrollTask implements Runnable{

		private int mParentWidth;
		private int mParentHeight;

		public ScrollTask(ViewGroup parent) {
//			start(parent);
		}

		private void start(ViewGroup parent) {
			mParentWidth = parent.getMeasuredWidth();
			mParentHeight = parent.getMeasuredHeight();
			mCurrentLocationX = mParentWidth;
			beginNextScrolled = false;
			setY(mCurrentLocationY);
		}

		@Override
		public void run() {
			if(mCurrentLocationX + getWidth() < 0){
				if(mListener != null)
					mListener.endScroll();
				return;
			}
			setX(mCurrentLocationX);
			if(mListener != null) {
				mListener.scrolling(mCurrentLocationX, mCurrentLocationY);
				if(mParentWidth - (mCurrentLocationX + getWidth()) > beginNextWidth){
					if(!beginNextScrolled) {
						mListener.beginNextScroll();
						beginNextScrolled = true;
					}
				}
			}
			mCurrentLocationX -= scrollVelocity;
			post(this);
		}
	}
	int beginNextWidth = 100;
	boolean beginNextScrolled = false;
	public void setScrollVelocity(int scrollVelocity) {
		this.scrollVelocity = scrollVelocity;
	}
	public interface BulletScreenScrollListener{
		void startScroll();
		void beginNextScroll();
		void endScroll();
		void scrolling(int currentLocationX, int currentLocatoinY);
	}

	public void setListener(BulletScreenScrollListener listener) {
		mListener = listener;
	}

	public void setCurrentLocationY(int currentLocationY) {
		mCurrentLocationY = currentLocationY;
	}

	public void setParent(ViewGroup parent) {
		mParent = parent;
	}

	/**
	 * 单位 px
	 * @param beginNextWidth
	 */
	public void setBeginNextWidth(int beginNextWidth) {
		this.beginNextWidth = beginNextWidth;
	}
}
