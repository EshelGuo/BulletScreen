# BulletScreens 弹幕效果		
>
BulletScreens, 一个弹幕效果的实现,支持自定义样式<br>
发送弹幕<br><br>
![](http://i.imgur.com/EtXoqkv.gif)
>
展示多条弹幕(速度可调节)<br><br>
![](http://i.imgur.com/svEQzwK.gif)

##使用方式:
* 
// 获得弹幕管理类的一个实例,传入一个ViewGroup用来显示所有弹幕到parent中<br>
mBulletScreens = BulletScreens.getInstance(ViewGroup parent);<br><br>
* 
// 设置如果没有要显示的弹幕或屏幕上没有空间,多久重新显示弹幕<br>
mBulletScreens.setSleepTime(300);<br><br>
* 
//添加多条要显示的弹幕<br>
mBulletScreens.addDatas(mList);<br><br>
* 
//添加一条要显示的弹幕<br>
mBulletScreens.addData(new BulletContent("哈哈哈哈哈"));<br><br>
* 
// 设置弹幕上下间距 单位dp<br>
mBulletScreens.setMarginTop(5);<br><br>
* 
//设置弹幕距离右边缘多远时显示下一条弹幕<br>
mBulletScreens.setMarginRight(10);<br><br>
* 
//设置弹幕出现时间差<br>
mBulletScreens.setTime(500);<br><br>
* 
// 设置监听用来创建 每一条弹幕以及给弹幕设置内容<br>
// BulletScreenView 父类是 相对布局, 可以自定义弹幕样式, 只需更改xml文件 , 在BulletScreenView中添加子孩子<br><br>
>
		mBulletScreens.setObtainBulletScreenView(new BulletScreens.ObtainBulletScreenView() {
			@Override
			public BulletScreenView obtain() {
				return bulletScreenView;
			}
			// 该方法执行 会把数据显示到界面上 , BulletContent 是数据, 如果不能满足需求, 可以自定义类继承 BulletContent ,然后设置属性,
			// 在该方法中将父类强转为子类 获取其中数据并显示到每一条弹幕上
			@Override
			public void setContent(BulletScreenView bulletScreenView, BulletContent content) {
				TextView textView = (TextView) bulletScreenView.getChildAt(0);
				textView.setText(content.title);
			}
		});
* 
//设置弹幕滚动速度<br>
bulletScreenView.setScrollVelocity(3);<br><br>
* 
// 开始执行任务,如果有数据会显示弹幕到界面,否则等待
mBulletScreens.start();
* 
// 停止弹幕任务
mBulletScreens.stop();

在 MainActivity 中:

		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// parent 是一个 ViewGroup , 使用该弹幕控件需要传入一个 ViewGroup 装所有弹幕
		parent = (FrameLayout) findViewById(R.id.parent);
		// 获得弹幕管理类的一个实例
		mBulletScreens = BulletScreens.getInstance(parent);
		// 设置如果没有要显示的弹幕或屏幕上没有空间,多久重新显示弹幕
		mBulletScreens.setSleepTime(300);
		mList = new ArrayList();
		for (int i = 0; i < 20; i++) {
			mList.add(new BulletContent("hello world"+i));
		}
		//添加多条要显示的弹幕
		mBulletScreens.addDatas(mList);
		//添加一条要显示的弹幕
		mBulletScreens.addData(new BulletContent("哈哈哈哈哈"));
		// 设置弹幕上下间距 单位dp
		mBulletScreens.setMarginTop(5);
		//设置弹幕距离右边缘多远时显示下一条弹幕
		mBulletScreens.setMarginRight(10);
		//设置弹幕出现时间差
		mBulletScreens.setTime(500);
		// 设置监听用来创建 每一条弹幕以及给弹幕设置内容
		// BulletScreenView 父类是 相对布局, 可以自定义弹幕样式, 只需更改xml文件 , 在BulletScreenView中添加子孩子
		mBulletScreens.setObtainBulletScreenView(new BulletScreens.ObtainBulletScreenView() {
			@Override
			public BulletScreenView obtain() {
				ViewGroup view = (ViewGroup) View.inflate(MainActivity.this, R.layout.item_bullet_screen, null);
				BulletScreenView bulletScreenView = (BulletScreenView) view.findViewById(R.id.bullet);
				//设置弹幕滚动速度
				bulletScreenView.setScrollVelocity(3);
				// 在这里移除 View 是因为 View.inflate 找到的 View 不包括根节点属性, 所以我在BulletScreenView外加了一个LinearLayout , 
				// 此时 BulletScreenView 的 parent 是 LinearLayout, 但是要显示弹幕 BulletScreenView 不能有 parent ,所以必须先 removeView
				view.removeView(bulletScreenView);
				return bulletScreenView;
			}
			// 该方法执行 会把数据显示到界面上 , BulletContent 是数据, 如果不能满足需求, 可以自定义类继承 BulletContent ,然后设置属性,
			// 在该方法中将父类强转为子类 获取其中数据并显示到每一条弹幕上
			@Override
			public void setContent(BulletScreenView bulletScreenView, BulletContent content) {
				TextView textView = (TextView) bulletScreenView.getChildAt(0);
				textView.setText(content.title);
			}
		});
		// 开始执行,如果有数据会显示弹幕到界面,否则等待
		mBulletScreens.start();
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
			}else {
				//点击按钮发送弹幕, 如果屏幕还有空间 , 该弹幕将会被显示, 否则放入队列等待显示
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
		// 在 activity 被销毁时关闭 弹幕任务
		mBulletScreens.stop();
	}

**item_bullet_screen文件:**
>
		<?xml version="1.0" encoding="utf-8"?>
		<LinearLayout
		    xmlns:android="http://schemas.android.com/apk/res/android"
		    android:layout_width="match_parent"
		    android:layout_height="wrap_content">
		    <com.eshel.bulletscreendemo.bulletscreen.BulletScreenView
		         android:id="@+id/bullet"
		         android:layout_width="wrap_content"
		         android:layout_height="wrap_content">
		        <TextView
		            android:background="@drawable/bullet_bg"
		            android:textColor="#ff0"
		            android:paddingTop="5dp"
		            android:paddingBottom="5dp"
		            android:paddingLeft="20dp"
		            android:paddingRight="20dp"
		            android:layout_width="wrap_content"
		            android:layout_height="wrap_content"
		            android:text="Hello World!"/>
		    </com.eshel.bulletscreendemo.bulletscreen.BulletScreenView>
		</LinearLayout>

**main_activity 文件:**
>
	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    android:orientation="vertical"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent">
	    <FrameLayout
	        android:id="@+id/parent"
	        android:layout_width="match_parent"
	        android:layout_height="0dp"
	        android:layout_weight="1"
	        android:background="@drawable/img">
	    </FrameLayout>
	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content">
	        <EditText
	            android:id="@+id/edit_text"
	            android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"/>
	        <Button
	            android:id="@+id/bt_send"
	            android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="发送"/>
	    </LinearLayout>
	</LinearLayout>
