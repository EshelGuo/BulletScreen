package com.eshel.bulletscreendemo.bulletscreen;

import android.content.Context;
import android.os.Parcel;
import android.os.SystemClock;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.eshel.bulletscreendemo.bulletscreen.bean.BulletContent;
import com.eshel.bulletscreendemo.bulletscreen.exception.MastObtainBulletScreenViewException;
import com.eshel.bulletscreendemo.bulletscreen.queue.BulletContentQueue;
import com.eshel.bulletscreendemo.bulletscreen.queue.BulletScreenQueue;
import com.eshel.bulletscreendemo.bulletscreen.queue.BulletYQueue;

import java.util.Collection;
import java.util.Random;

/**
 * 项目名称: Demo
 * 创建人: Eshel
 * 创建时间:2017/8/3 09时59分
 * 描述: 弹幕s管理者
 */

public class BulletScreens implements ViewTreeObserver.OnGlobalLayoutListener {
	private Context context;
	/**
	 * 如果没有数据, 多久重新检查数据是否变化
	 */
	private long sleepTime = 300;

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	private BulletScreenQueue mBulletScreenQueue;
	private BulletYQueue mBulletYQueue;
	private BulletContentQueue mContentQueue;
	public void addDatas(Collection<BulletContent> bulletContents){
		mContentQueue.addAll(bulletContents);
	}
	public void addData(BulletContent bulletContent){
		mContentQueue.inQueue(bulletContent);
	}
	private ViewGroup parent;
	private static BulletScreens mBulletScreens;

	private BulletScreens(ViewGroup parent){
		this.parent = parent;
		this.context = parent.getContext();
		margin = dp2px(5);
		mBulletScreenQueue = new BulletScreenQueue();
		mBulletYQueue = new BulletYQueue();
		mContentQueue = new BulletContentQueue();
	}
	public static BulletScreens newInstance(ViewGroup bulletScreenParent){
/*		if(mBulletScreens == null){
			synchronized (BulletScreens.class){
				if(mBulletScreens == null){*/
					mBulletScreens = new BulletScreens(bulletScreenParent);
			/*	}
			}*/
//		}else {
//			mBulletScreens.parent = bulletScreenParent;
//		}
		return mBulletScreens;
	}

	public void start(){
		if(mObtainBulletScreenView == null) {
			throw new MastObtainBulletScreenViewException("setObtainBulletScreenView 方法必须被设置, 作用是创建 BulletScreenView(即指定每一条弹幕的样式)");
		}else {
			task = new BulletsTask();
			parent.getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
	}

	@Override
	public void onGlobalLayout() {
		prepare();
		task.start();
		new Thread(task).start();
		parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	}

	private void prepare() {
		if(!mBulletYQueue.isEmpty())
			mBulletYQueue.clear();
		BulletScreenView bulletScreenView = getBulletScreenView();
		bulletScreenView.measure(0,0);
		int measuredHeight = bulletScreenView.getMeasuredHeight();
		int parentHeight = parent.getHeight();
		int heightSum = margin;
		while (heightSum + measuredHeight < parentHeight){
			mBulletYQueue.inQueue(heightSum);
			heightSum += (measuredHeight + margin);
		}
		mBulletScreenQueue.inQueue(bulletScreenView);
	}

	BulletsTask task;
	class BulletsTask implements Runnable{
		public boolean isStop = false;
		@Override
		public void run() {
			while (true) {
				if (isStop) {
					return;
				}
				if (mContentQueue.isEmpty()) {
					SystemClock.sleep(sleepTime);
				} else {
					final int locationY = getLocationY();
					if(locationY != -1) {
						final BulletContent bulletContent = mContentQueue.outQueue();
						final BulletScreenView bulletScreenView = getBulletScreenView();
						bulletScreenView.setCurrentLocationY(locationY);
						bulletScreenView.setBeginNextWidth(marginRight);
						parent.post(new Runnable() {
							@Override
							public void run() {
								parent.addView(bulletScreenView);
								bulletScreenView.setX(parent.getWidth());
							}
						});
						parent.post(new Runnable() {
							@Override
							public void run() {
								mObtainBulletScreenView.setContent(bulletScreenView, bulletContent);
							}
						});
						bulletScreenView.setListener(new BulletScreenView.BulletScreenScrollListener() {
							@Override
							public void startScroll() {
							}

							@Override
							public void beginNextScroll() {
								mBulletYQueue.inQueue(locationY);
							}

							@Override
							public void endScroll() {
								parent.post(new Runnable() {
									@Override
									public void run() {
										parent.removeView(bulletScreenView);
										mBulletScreenQueue.inQueue(bulletScreenView);
									}
								});
							}
							@Override
							public void scrolling(int currentLocationX, int currentLocatoinY) {
							}
						});
						// TODO: 2017/8/3
						bulletScreenView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
							@Override
							public void onGlobalLayout() {
								bulletScreenView.startScroll();
								bulletScreenView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							}
						});
//						bulletScreenView.setParent(parent);
//						bulletScreenView.startScroll();
						SystemClock.sleep(time);
					}else {
						SystemClock.sleep(sleepTime);
					}
				}
			}
		}
		public void start(){
			isStop = false;
		}

		public void stop() {
			isStop = true;
		}
	}
	private int getLocationY(){
		if(!mBulletYQueue.isEmpty()) {
			return mBulletYQueue.remove(mRandom.nextInt(mBulletYQueue.size()));
		}
		return -1;
	}

	private BulletScreenView getBulletScreenView(){
		BulletScreenView bulletScreenView;

		if(mBulletScreenQueue.isEmpty()){
			bulletScreenView = mObtainBulletScreenView.obtain();
		}else{
			bulletScreenView = mBulletScreenQueue.outQueue();
		}

		bulletScreenView.task = task;
		return bulletScreenView;
	}

	public void stop(){
		task.stop();
	}

	/**
	 * 创建一条弹幕的View
	 */
	public interface ObtainBulletScreenView{
		/**
		 * 创建一个 BulletScreenView
		 * @return
		 */
		BulletScreenView obtain();

		/**
		 * 给 BulletScreenView 设置内容
		 * @param bulletScreenView
		 * @param content
		 */
		void setContent(BulletScreenView bulletScreenView, BulletContent content);
	}
	private ObtainBulletScreenView mObtainBulletScreenView;
	public void setObtainBulletScreenView(ObtainBulletScreenView obtainBulletScreenView){
		if(obtainBulletScreenView == null)
			throw new MastObtainBulletScreenViewException("obtainBulletScreenView 不能为空,必须指定");
		mObtainBulletScreenView = obtainBulletScreenView;
	}

	private int margin;

	/**
	 * 单位是dp
	 * @param margin
	 */
	public void setMarginTop(int margin){
		margin = dp2px(margin);
	}

	public int dp2px(int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dp * density + .5f);
	}
	public int px2dp(int px) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (px / density + .5f);
	}
	Random mRandom = new Random();
	/**
	 * 弹幕之间出现时间差
	 */
	private long time = 500;

	public void setTime(long time) {
		this.time = time;
	}
	int marginRight = 100;
	public void setMarginRight(int marginDp){
		marginRight = dp2px(marginDp);
	}
}
