package com.camera.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PictureCallback {

	private SurfaceHolder holder;
	private Camera camera;
	private Camera.Parameters parameters;
	private Activity act;
	private Handler handler = new Handler();
	private Context context;
	private SurfaceView surfaceView;
	private AudioManager audio;
	private int current;

	@SuppressWarnings("deprecation")
	public CameraView(Context context) {
		super(context);

		surfaceView = this;
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		final int current = audio.getRingerMode();
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		this.context = context;
		holder = getHolder();// ����Surface Holder
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ָ��Push Buffer

		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// if (camera == null) {
		// handler.postDelayed(this, 2 * 1000);//
		// ��������camera��Ҫʱ�䣬�ڴ�����������ٽ��о۽�֪��camera��Ϊ��
		// } else {
		// camera.autoFocus(new AutoFocusCallback() {
		// @Override
		// public void onAutoFocus(boolean success, Camera camera) {
		// Log.i("testFile", "auto focused with "+success);
		// if (success) {
		// camera.takePicture(new ShutterCallback() {// ����۽��ɹ����������
		// @Override
		// public void onShutter() {
		// Log.i("testFile", "take a picture");
		// }
		// }, null, CameraView.this);
		// } else {
		// }
		// }
		// });
		// }
		// }
		// }, 1 * 1000);
	}

	public CameraView(Context context, Activity act) {// �ڴ˶���һ�����췽���������չ����CameraActivity��finish��
		this(context);
		this.act = act;
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		// TODO Auto-generated method stub

		int defaultId = -1;
		int mNumberOfCameras = Camera.getNumberOfCameras();
		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				defaultId = i;
				break;
			}
		}
		if (-1 == defaultId) {
			if (mNumberOfCameras > 0) {
				// ���û�к�������ͷ
				defaultId = 0;
				Log.i("testFile", "no front camera!!!");
			} else {
				// û������ͷ
				// Toast.makeText(getApplicationContext(), R.string.no_camera,
				// Toast.LENGTH_LONG).show();
				Log.i("testFile", "no camera!!!");
				return;
			}
		}
		Log.i("testFile", "the front camera id is " + defaultId);

		camera = Camera.open(defaultId);// ����ͷ�ĳ�ʼ��
		Log.i("testFile", "camera opened");

		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// if (holder != null) {
		// try {
		// camera.setPreviewDisplay(holder);
		// Log.i("testFile", "camera set preview display");
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else {
		// handler.postDelayed(this, 1 * 1000);
		// Log.i("testFile", "handler post dealayed");
		//
		// }
		// }
		// }, 2 * 1000);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

		parameters = camera.getParameters();
		Log.i("testFile", "width is "+width +" height is "+height);
//		parameters.setPreviewSize(480, 800);
		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			// ���������
			parameters.set("orientation", "portrait");
//			// ��2.2���Ͽ���ʹ��
//			camera.setDisplayOrientation(90);
			Log.i("testFile", "90");
		} else {
			parameters.set("orientation", "landscape");
			// ��2.2���Ͽ���ʹ��
			camera.setDisplayOrientation(90);
		}
		parameters.setPictureSize(480, 600);
		camera.setParameters(parameters);// ���ò���
		try {
			// ������ʾ
			camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			camera.release();
			camera = null;
			Log.i("testFile", "io exception:" + exception.toString());
			return;
		}
		camera.startPreview();// ��ʼԤ��
		Log.i("testFile", "camera start preview");

		camera.autoFocus(new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Log.i("testFile", "auto focused with " + success);
				if (success) {
					camera.takePicture(new ShutterCallback() {// ����۽��ɹ����������
								@Override
								public void onShutter() {
									Log.i("testFile", "take a picture");
								}
							}, null, CameraView.this);
				} else {
					camera.takePicture(new ShutterCallback() {// ����۽��ɹ����������
								@Override
								public void onShutter() {
									Log.i("testFile", "take a picture");
								}
							}, null, CameraView.this);
				}
			}
		});

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// �ͷ��ֻ�����ͷ

		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void onPictureTaken(byte[] data, Camera camera) {// ������ɺ󱣴���Ƭ

		try {
			Log.i("testFile", "picture taken");

			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String time = format.format(date);

			// ��SD���ϴ����ļ���
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/myCamera/pic");
			if (!file.exists()) {

				file.mkdirs();
			}

			String path = Environment.getExternalStorageDirectory()
					+ "/myCamera/pic/" + time + ".jpg";
			Log.i("testFile", "path is " + path);
			data2file(data, path);

			int blink = IsBlink(data, 480, 600);
			Log.i("testFile", "is blink? "+ blink);
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
			holder.removeCallback(CameraView.this);
			audio.setRingerMode(current);
			act.finish();
			// uploadFile(path);

		} catch (Exception e) {

		}

	}

	private void data2file(byte[] w, String fileName) throws Exception {// ������������ת��Ϊ�ļ��ĺ���
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(w);
			out.close();
		} catch (Exception e) {
			if (out != null)
				out.close();
			throw e;
		}
	}
	// private void uploadFile(String filePath)// ���չ����ϴ��ļ���������
	// {
	// }

	public static native int IsBlink(byte[] data, int w, int h);
	
	static {
		 System.loadLibrary("eye");   
	}
}
