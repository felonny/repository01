package com.example.lucky;

import javax.crypto.spec.OAEPParameterSpec;






import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LuckyPan extends SurfaceView implements Callback, Runnable {
	
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	//用于绘制线程
	private Thread t;
	//线程的控制开关
	private boolean isRunning;
	//奖项
	private String[] mstr = new String[]{"单反相机","IPAD","恭喜发财","IPHONE","服装一套","恭喜发财"};
	//图片
	//R.drawable.danfan,R.drawable.ipad,R.drawable.f015,R.drawable.iphone,R.drawable.meizi,R.drawable.f015
	private int[] mImgs = new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f015,R.drawable.iphone,R.drawable.meizi,R.drawable.f015};
	private Bitmap[] mImagsBitmap;
	//盘块的颜色
	private int[] mColor = new int[]{0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01};
	private int mcount = 6;
	//盘块的范围
	private RectF mRange = new RectF();
	//整个盘块的直径
	private int mRadius;
	//绘制盘块的画笔
	private Paint mArcPaint;
	//绘制文本的画笔
	private Paint mTextPaint;
	//盘块滚动的速度
	private double mSpeed ;
	//盘块起始的角度
	private volatile float mStartAngle = 0;
	//判断是否点击停止按钮
	private boolean isShouldEnd;
	//转盘的中心位置
	private int mCenter;
	//以paddingleft为准
	private int mPadding;
	//背景图
	private Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
	//设置文字的大小
	private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SHIFT, 20, getResources().getDisplayMetrics());
	public LuckyPan(Context context) {
		super(context,null);
		// TODO Auto-generated constructor stub
	}

	public LuckyPan(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mHolder = getHolder();
		mHolder.addCallback(this);
		//可获得焦点 
		setFocusable(true);
		setFocusableInTouchMode(true);
		//设置常量
		setKeepScreenOn(true);
	}
	//设置盘为正放型范围
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
		
		mPadding = getPaddingLeft();
		//半径
		mRadius = width - mPadding*2;
		//中心店
		mCenter = width / 2;
		//把范围固定在正方形中
		setMeasuredDimension(width, width);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//初始化绘制盘块的画笔
		mArcPaint = new Paint();
		//设置抗锯齿属性
		mArcPaint.setAntiAlias(true);
		mArcPaint.setDither(true);
		
		//设置文本的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xFFFFFFFF);
		mTextPaint.setTextSize(mTextSize);
		
		mRange = new RectF(mPadding, mPadding, mPadding+mRadius, mPadding+mRadius);
		
		mImagsBitmap = new Bitmap[mcount];
		for(int i = 0;i<mcount;i++){
			mImagsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
		}
		
		isRunning = true;
		t = new Thread(this);
		t.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		isRunning = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(isRunning){
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();
			if((end - start)<50){
				try {
					Thread.sleep(50-(end-start));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		// TODO Auto-generated method stub
		try {
			mCanvas = mHolder.lockCanvas();
			if(mCanvas != null){
				//draw something
				//绘制背景
				drawbg();
				//绘制盘块
				float tmpAngle = mStartAngle;//起始角度
				float sweepAngle = 360/mcount;//每个盘块的角度
				for(int i= 0;i<mcount;i++){
					mArcPaint.setColor(mColor[i]);
					//绘制盘块
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
					
					//绘制文本
					drawText(tmpAngle,sweepAngle,mstr[i]);
					//回执每个盘块上的图片
					drawIcon(tmpAngle,mImagsBitmap[i]);
					tmpAngle += sweepAngle;
				}
				
				mStartAngle += mSpeed;
				//如果点击了停止按钮
				if(isShouldEnd){
					mSpeed -= 1;
				}
				if(mSpeed<=0){
					mSpeed = 0;
					isShouldEnd = false;
				}
			}
		} catch (Exception e) {
			
		}
		finally{
			if(mCanvas != null){
				//释放canvas
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}
	
	//电机启动旋转
	public void luckyStart(int index){
		//每一项的角度
		float angle= 360 / mcount;
		//每一项的中奖范围，当前index的中奖范围
		//1-->150~210
		//0-->210~270
		float from = 270 - (index+1)*angle;
		float end = from + angle;
		//mSpeed = 40;
		//isShouldEnd = false;
		
		//设置停下来需要旋转的距离
		float targetFrom = 4*360 +from;
		float targetEnd = 4 *360 +end;
		//计算要停在指定的区域内初始速度
		/*
		 * v1 -->0
		 * v1每次减1
		 * (v1+0)*(v1+1)/2 = targetFrom;
		 */
		float v1 = (float) ((-1+Math.sqrt(1+8*targetFrom))/2);
		float v2 = (float) ((-1+Math.sqrt(1+8*targetEnd))/2);
		mSpeed = (v1 + Math.random()*(v2 - v1));
		//mSpeed = v1;
		isShouldEnd = false;
	}
	public void luckyEnd(){
		mStartAngle = 0;
		isShouldEnd = true;
	}
	//转盘是否还在旋转
	public boolean isStart(){
		return mSpeed != 0;
	}
	public boolean isShouldEnd(){
		return isShouldEnd;
	}
	//回执Icon
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		// TODO Auto-generated method stub
		//设置图片的宽度为直径的1/2
		int imgWidth = mRadius/8;
		
		float angle  = (float) ((tmpAngle +360/mcount/2)*Math.PI/180);
		
		int x = (int) (mCenter +mRadius/2/2*Math.cos(angle));
		
		int y = (int) (mCenter +mRadius/2/2*Math.sin(angle));
		//确定图标的位置
		Rect rect = new Rect(x-imgWidth/2, y-imgWidth/2, x+imgWidth/2, y+imgWidth/2);
		
		mCanvas.drawBitmap(bitmap, null, rect, null);
		
	}

	//绘制每个盘块的文本
    private void drawText(float tmpAngle, float sweepAngle, String string) {
		// TODO Auto-generated method stub
		Path path = new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		//利用水平偏移量让文字居中
		float textWidth = mTextPaint.measureText(string);
		int hOffset = (int) (mRadius*Math.PI/mcount/2 - textWidth/2);
		//垂直偏移量
		int vOffset = mRadius/2/6;
		mCanvas.drawTextOnPath(string, path, hOffset,vOffset, mTextPaint);
	}

	//绘制背景
	private void drawbg() {
		// TODO Auto-generated method stub
		mCanvas.drawColor(0xFFFFFFFF);
		mCanvas.drawBitmap(background, null, new RectF(mPadding/2, mPadding/2, getMeasuredWidth()-mPadding/2, getMeasuredHeight()-mPadding/2), null);
	}

}
