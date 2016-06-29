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
	//���ڻ����߳�
	private Thread t;
	//�̵߳Ŀ��ƿ���
	private boolean isRunning;
	//����
	private String[] mstr = new String[]{"�������","IPAD","��ϲ����","IPHONE","��װһ��","��ϲ����"};
	//ͼƬ
	//R.drawable.danfan,R.drawable.ipad,R.drawable.f015,R.drawable.iphone,R.drawable.meizi,R.drawable.f015
	private int[] mImgs = new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f015,R.drawable.iphone,R.drawable.meizi,R.drawable.f015};
	private Bitmap[] mImagsBitmap;
	//�̿����ɫ
	private int[] mColor = new int[]{0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01};
	private int mcount = 6;
	//�̿�ķ�Χ
	private RectF mRange = new RectF();
	//�����̿��ֱ��
	private int mRadius;
	//�����̿�Ļ���
	private Paint mArcPaint;
	//�����ı��Ļ���
	private Paint mTextPaint;
	//�̿�������ٶ�
	private double mSpeed ;
	//�̿���ʼ�ĽǶ�
	private volatile float mStartAngle = 0;
	//�ж��Ƿ���ֹͣ��ť
	private boolean isShouldEnd;
	//ת�̵�����λ��
	private int mCenter;
	//��paddingleftΪ׼
	private int mPadding;
	//����ͼ
	private Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
	//�������ֵĴ�С
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
		//�ɻ�ý��� 
		setFocusable(true);
		setFocusableInTouchMode(true);
		//���ó���
		setKeepScreenOn(true);
	}
	//������Ϊ�����ͷ�Χ
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
		
		mPadding = getPaddingLeft();
		//�뾶
		mRadius = width - mPadding*2;
		//���ĵ�
		mCenter = width / 2;
		//�ѷ�Χ�̶�����������
		setMeasuredDimension(width, width);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//��ʼ�������̿�Ļ���
		mArcPaint = new Paint();
		//���ÿ��������
		mArcPaint.setAntiAlias(true);
		mArcPaint.setDither(true);
		
		//�����ı��Ļ���
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
				//���Ʊ���
				drawbg();
				//�����̿�
				float tmpAngle = mStartAngle;//��ʼ�Ƕ�
				float sweepAngle = 360/mcount;//ÿ���̿�ĽǶ�
				for(int i= 0;i<mcount;i++){
					mArcPaint.setColor(mColor[i]);
					//�����̿�
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
					
					//�����ı�
					drawText(tmpAngle,sweepAngle,mstr[i]);
					//��ִÿ���̿��ϵ�ͼƬ
					drawIcon(tmpAngle,mImagsBitmap[i]);
					tmpAngle += sweepAngle;
				}
				
				mStartAngle += mSpeed;
				//��������ֹͣ��ť
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
				//�ͷ�canvas
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}
	
	//���������ת
	public void luckyStart(int index){
		//ÿһ��ĽǶ�
		float angle= 360 / mcount;
		//ÿһ����н���Χ����ǰindex���н���Χ
		//1-->150~210
		//0-->210~270
		float from = 270 - (index+1)*angle;
		float end = from + angle;
		//mSpeed = 40;
		//isShouldEnd = false;
		
		//����ͣ������Ҫ��ת�ľ���
		float targetFrom = 4*360 +from;
		float targetEnd = 4 *360 +end;
		//����Ҫͣ��ָ���������ڳ�ʼ�ٶ�
		/*
		 * v1 -->0
		 * v1ÿ�μ�1
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
	//ת���Ƿ�����ת
	public boolean isStart(){
		return mSpeed != 0;
	}
	public boolean isShouldEnd(){
		return isShouldEnd;
	}
	//��ִIcon
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		// TODO Auto-generated method stub
		//����ͼƬ�Ŀ��Ϊֱ����1/2
		int imgWidth = mRadius/8;
		
		float angle  = (float) ((tmpAngle +360/mcount/2)*Math.PI/180);
		
		int x = (int) (mCenter +mRadius/2/2*Math.cos(angle));
		
		int y = (int) (mCenter +mRadius/2/2*Math.sin(angle));
		//ȷ��ͼ���λ��
		Rect rect = new Rect(x-imgWidth/2, y-imgWidth/2, x+imgWidth/2, y+imgWidth/2);
		
		mCanvas.drawBitmap(bitmap, null, rect, null);
		
	}

	//����ÿ���̿���ı�
    private void drawText(float tmpAngle, float sweepAngle, String string) {
		// TODO Auto-generated method stub
		Path path = new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		//����ˮƽƫ���������־���
		float textWidth = mTextPaint.measureText(string);
		int hOffset = (int) (mRadius*Math.PI/mcount/2 - textWidth/2);
		//��ֱƫ����
		int vOffset = mRadius/2/6;
		mCanvas.drawTextOnPath(string, path, hOffset,vOffset, mTextPaint);
	}

	//���Ʊ���
	private void drawbg() {
		// TODO Auto-generated method stub
		mCanvas.drawColor(0xFFFFFFFF);
		mCanvas.drawBitmap(background, null, new RectF(mPadding/2, mPadding/2, getMeasuredWidth()-mPadding/2, getMeasuredHeight()-mPadding/2), null);
	}

}
