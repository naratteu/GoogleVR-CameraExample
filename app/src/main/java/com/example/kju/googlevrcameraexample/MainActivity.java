package com.example.kju.googlevrcameraexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.pedro.rtplibrary.rtmp.RtmpDisplay;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.util.Arrays;

public class MainActivity extends GvrActivity {

	private static final String TAG = "MainActivity";

	private GvrView cameraView;

	private CameraDevice cameraDevice;
	private CaptureRequest.Builder previewBuilder;
	private CameraCaptureSession previewSession;
	private SurfaceTexture surfaceTexture;
	private StreamConfigurationMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
        cameraView = new GvrView(this);//(GvrView) findViewById(R.id.camera_view);
		//cameraView.setTransitionViewEnabled(false);
		//setContentView(mSurfaceView);
		setContentView(cameraView);
		setGvrView(cameraView);

		GvrRenderer gvrRenderer = new GvrRenderer(cameraView, new GvrRenderer.GvrRendererEvents() {
			@Override
			public void onSurfaceTextureCreated(SurfaceTexture aSurfaceTexture) {
				surfaceTexture = aSurfaceTexture;
				openCamera();
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		cameraView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cameraView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void openCamera()
	{
		try {
			CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
			String cameraId = cameraManager.getCameraIdList()[0];
			CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
			map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			Size previewSize = map.getOutputSizes(SurfaceTexture.class)[0];
			cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
				@Override
				public void onOpened(CameraDevice camera) {
					cameraDevice = camera;
					startPreview();
				}

				@Override
				public void onDisconnected(CameraDevice cameraDevice) {
					Log.d(TAG, "onDisconnected");
				}

				@Override
				public void onError(CameraDevice cameraDevice, int i) {
					Log.e(TAG, "onError");
				}
			}, cameraView.getHandler());
			rtmpIntent();
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	protected void startPreview()
	{
		if (cameraDevice == null) {
			Log.e(TAG, "preview failed");
			return;
		}

		Surface surface = new Surface(surfaceTexture);

		try {
			previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

		previewBuilder.addTarget(surface);

		try {
			cameraDevice.createCaptureSession(Arrays.asList(surface),
					new CameraCaptureSession.StateCallback() {
						@Override
						public void onConfigured(CameraCaptureSession session) {
							previewSession = session;
							updatePreview();
						}

						@Override
						public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
							Log.e(TAG, "onConfigureFailed");
						}
					}, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	protected void updatePreview() {
		if (null == cameraDevice) {
			Log.e(TAG, "updatePreivew error, return");
		}

		previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

		HandlerThread thread = new HandlerThread("CameraPreview");
		thread.start();
		Handler backgroundHandler = new Handler(thread.getLooper());

		try {
			previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	RtmpDisplay rd;
	public void rtmpIntent() {
		rd = new RtmpDisplay(this, true, new ConnectCheckerRtmp() {
			@Override
			public void onConnectionSuccessRtmp() {

			}

			@Override
			public void onConnectionFailedRtmp(@NonNull String reason) {

			}

			@Override
			public void onNewBitrateRtmp(long bitrate) {

			}

			@Override
			public void onDisconnectRtmp() {

			}

			@Override
			public void onAuthErrorRtmp() {

			}

			@Override
			public void onAuthSuccessRtmp() {

			}
		});
		startActivityForResult(rd.sendIntent(), 1);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "requestCode: "+ requestCode);
		Log.d(TAG, "resultCode: "+ resultCode);
		Log.d(TAG, "data: "+ data);
		rd.setIntentResult(resultCode, data);
		if (rd.prepareAudio() && rd.prepareVideo(1280, 720, 30,  2500 * 1024, 0, 320)) {//파라미터 넣을수 있음
			rd.startStream("rtmp://a.rtmp.youtube.com/live2/5zvy-7tgp-f6y8-d2we");
		} else {
			//This device cant init encoders, this could be for 2 reasons: The encoder selected doesnt support any configuration setted or your device hasnt a H264 or AAC encoder (in this case you can see log error valid encoder not found)
		}
	}
}