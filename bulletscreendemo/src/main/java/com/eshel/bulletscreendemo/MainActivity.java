package com.eshel.bulletscreendemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eshel.bulletscreendemo.bulletscreen.BulletScreenView;
import com.eshel.bulletscreendemo.bulletscreen.BulletScreens;
import com.eshel.bulletscreendemo.bulletscreen.bean.BulletContent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private FrameLayout parent;
	private EditText mEditText;
	private Button mButton;
	private BulletScreens mBulletScreens;
	private ArrayList<BulletContent> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		parent = (FrameLayout) findViewById(R.id.parent);
		mBulletScreens = BulletScreens.newInstance(parent);
		// 设置如果没有要执行的任务或屏幕上没有空间,多久重新检测是否能执行任务
		mBulletScreens.setSleepTime(300);
		if(mList == null)
			mList = new ArrayList();
		mList.clear();
		for (int i = 0; i < 10; i++) {
			mList.add(new BulletContent("hello world"+i));
		}
		//添加数据
		mBulletScreens.addDatas(mList);
		//添加一条数据
		mBulletScreens.addData(new BulletContent("哈哈哈哈哈"));
		// 设置弹幕上下间距 单位dp
		mBulletScreens.setMarginTop(5);
		//设置弹幕距离右边缘多远时显示下一条弹幕
		mBulletScreens.setMarginRight(10);
		//设置弹幕出现时间差
		mBulletScreens.setTime(500);
		// 设置监听用来创建 每一条弹幕以及给弹幕设置内容
		mBulletScreens.setObtainBulletScreenView(new BulletScreens.ObtainBulletScreenView() {
			@Override
			public BulletScreenView obtain() {
				ViewGroup view = (ViewGroup) View.inflate(MainActivity.this, R.layout.item_bullet_screen, null);
				BulletScreenView bulletScreenView = (BulletScreenView) view.findViewById(R.id.bullet);
				//设置弹幕滚动速度
				bulletScreenView.setScrollVelocity(3);
				view.removeView(bulletScreenView);
				return bulletScreenView;
			}

			@Override
			public void setContent(BulletScreenView bulletScreenView, BulletContent content) {
				TextView textView = (TextView) bulletScreenView.getChildAt(0);
				textView.setText(content.title);
			}
		});
		mBulletScreens.start();
		/*final BulletScreenView bulletScreenView = (BulletScreenView) findViewById(R.id.bullet);
		bulletScreenView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				bulletScreenView.startScroll();
				bulletScreenView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});*/
		mEditText = (EditText) findViewById(R.id.edit_text);
		mButton = (Button) findViewById(R.id.bt_send);
		mButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String content = mEditText.getText().toString();
		if(!TextUtils.isEmpty(content)){
			if(content.equals("again")){
				mBulletScreens.addDatas(mList);
			}else if(content.equals("stop")){
				mBulletScreens.stop();
			} else {
				mBulletScreens.addData(new BulletContent(content));
			}
			mEditText.setText("");
		}else {
			Toast.makeText(this,"请先输入内容", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBulletScreens.stop();
	}
}
