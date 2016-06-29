package com.example.lucky;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SurfaceViewTemplator extends SurfaceView implements Callback, Runnable {
	
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	//���ڻ����߳�
	private Thread t;
	//�̵߳Ŀ��ƿ���
	private boolean isRunning;

	public SurfaceViewTemplator(Context context) {
		super(context,null);
		// TODO Auto-generated constructor stub
	}

	public SurfaceViewTemplator(Context context, AttributeSet attrs) {
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

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
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
			draw();
		}
	}

	private void draw() {
		// TODO Auto-generated method stub
		try {
			mCanvas = mHolder.lockCanvas();
			if(mCanvas != null){
				//draw something
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

}
