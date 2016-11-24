package com.example.obd2;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Semaphore;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

public class Picture {

	private final String TAG = "PICTURE";
	public Camera camera;
	boolean inPreview;
	private boolean cameraConfigured;
	private SurfaceHolder surfaceHolder;
	private Semaphore sem;
	private Thread th;
	boolean isStopped;
	String filenamePart_1;

	Gui gui;

	public Picture(Gui gui) {
		this.gui = gui;
		surfaceHolder = gui.preview.getHolder();
		surfaceHolder.addCallback(surfaceCallback);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		sem = new Semaphore(0);
	}

	public void takePicture(String filenamePart_1) {
		this.filenamePart_1 = filenamePart_1;
		sem.release();
	}

	boolean running = true;
	boolean next = false;
	Semaphore sem2 = new Semaphore(0);

	boolean onInit;

	public void _onCreate_() {
		th = new Thread() {

			int delay = 1000;

			// int photoCount = 1000000;

			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				onInit = true;
				sem.release();

				while (true) {
					sem.acquireUninterruptibly();
					// filenamePart_1 = "" + photoCount++;
					if (!running) {
						break;
					}
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					handlerCameraBack.sendEmptyMessage(0);
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Log.v("knips", "" + inPreview);
					if (inPreview) {
						try {
							Log.v("knips", "1 back");
							camera.takePicture(null, null, photoCallback);
							sem2.acquireUninterruptibly();
							Log.v("knips", "2 back");
						} catch (Exception e) {
							Log.v("knips", "takePicture failed");
						}

						inPreview = false;
						// running = false;
					}
					try {
						Thread.sleep(2 * delay);
					} catch (InterruptedException e) {
					}
					handlerCameraFront.sendEmptyMessage(0);
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Log.v("knips", "" + inPreview);
					if (inPreview) {
						try {
							Log.v("knips", "1 front");
							camera.takePicture(null, null, photoCallback);
							sem2.acquireUninterruptibly();
							Log.v("knips", "2 front");
						} catch (Exception e) {
							Log.v("knips", "takePicture failed");
						}
						inPreview = false;
						// running = false;
					}
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
					}
					onInit = false;
				}
				isStopped = true;
			}
		};
		th.start();
	}

	public void _onPause_() {
		// if (inPreview) {
		// camera.stopPreview();
		// }
		// camera.release();
		// camera = null;
		inPreview = false;
	}

	@SuppressWarnings("null")
	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			// Log.v(TAG, "w=" + size.width + "  h=" + size.height);
			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;

				if (newArea < resultArea) {
					result = size;
				}
			}
		}

		result.width = 1280;
		result.height = 960;

		return (result);
	}

	void initPreview(int width, int height) {
		if (camera != null && surfaceHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);
				Camera.Size pictureSize = getSmallestPictureSize(parameters);

				if (size != null && pictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(pictureSize.width,
							pictureSize.height);
					// parameters.setPictureFormat(ImageFormat.JPEG);
					parameters.setJpegQuality(100);
					parameters
							.setWhiteBalance(Parameters.WHITE_BALANCE_DAYLIGHT);
					// parameters.SCENE_MODE_LANDSCAPE
					// parameters.setFocusMode(Parameters.FOCUS_MODE_FIXED);
					camera.setParameters(parameters);
					cameraConfigured = true;
				}
			}
		}
	}

	void startPreview() {
		// Log.v(TAG, "startPreview");
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			Log.v(TAG, "surfaceCreated");
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.v(TAG, "surfaceChanged1");
			initPreview(width, height);
			// Log.v(TAG, "width=" + width + " height=" + height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				Log.v(TAG, "camera=null");
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		}
	};

	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		@SuppressWarnings("synthetic-access")
		public void onPictureTaken(byte[] data, Camera camera) {
			if (!onInit) {
				savePhoto(data);
			}
			Log.v("knips", "1 omPictureTaken");
			camera.startPreview();
			inPreview = true;
			Log.v("knips", "2 omPictureTaken");
			sem2.release();
		}
	};

	private void savePhoto(byte[] data) {
		Log.v("knips", "****  Bitte lächeln! ****");
		File f = new File(Environment.getExternalStorageDirectory(),
				filenamePart_1 + filenamePart_2 + ".jpg");

		if (f.exists()) {
			f.delete();
		}

		try {
			FileOutputStream fos = new FileOutputStream(f.getPath());
			fos.write(data);
			fos.close();
			Log.v("knips", f.getAbsolutePath());
		} catch (java.io.IOException e) {
			Log.e("PictureDemo", "Exception in photoCallback", e);
		}
	}

	String filenamePart_2 = "";

	@SuppressWarnings("null")
	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			// Log.v(TAG, "Preview width=" + size.width + "  Preview height="
			// + size.height);
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		result.width = 640;
		result.height = 480;
		return result;
	}

	public void selectCamera(int id) {
		switch (id) {
		case 1:
			handlerCameraBack.sendEmptyMessage(0);
			break;

		case 2:
			handlerCameraFront.sendEmptyMessage(0);
			break;

		}
	}

	public String fn;
	Handler handlerCameraBack = new Handler() {
		@SuppressWarnings("synthetic-access")
		@Override
		public void handleMessage(Message cameraId) {
			if (camera != null) {
				Log.v(TAG, "camera=null");
				camera.stopPreview();
				camera.release();
				camera = null;
				cameraConfigured = false;
			}
			camera = Camera.open(0);
			filenamePart_2 = "_BACK";
			Log.v(TAG, "camera=" + camera);
			initPreview(1280, 960);
			startPreview();
		}
	};

	Handler handlerCameraFront = new Handler() {
		@SuppressWarnings("synthetic-access")
		@Override
		public void handleMessage(Message fn) {
			if (camera != null) {
				Log.v(TAG, "camera=null");
				camera.stopPreview();
				camera.release();
				camera = null;
				cameraConfigured = false;
			}
			camera = Camera.open(1);
			filenamePart_2 = "_FRONT";
			Log.v(TAG, "camera=" + camera);
			initPreview(1280, 960);
			startPreview();
		}
	};

	public void close() {
		if (camera != null) {
			running = false;
			sem.release();
			while (!isStopped) {
			}
			camera.stopPreview();
			camera.release();
			camera = null;
			cameraConfigured = false;
		}
	}
}
