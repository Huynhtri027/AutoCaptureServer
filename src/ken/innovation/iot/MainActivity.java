package ken.innovation.iot;

import java.util.ArrayList;

import ken.innovation.iot.WiFiUtils.ValueCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity{

	Server server;
	TextView infoip, msg;
	LinearLayout surfaceLayout;
	
	ArrayList<String> adds = new ArrayList<>();
	
	public CaptureWakeTask captureTask = new CaptureWakeTask();
	
	
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		surfaceLayout = (LinearLayout) findViewById(R.id.surfaceLayout);
		infoip = (TextView) findViewById(R.id.infoip);
		msg = (TextView) findViewById(R.id.msg);
		server = new Server(this);
		infoip.setText(server.getIpAddress()+":"+server.getPort());
		Button takePhoto = (Button) findViewById(R.id.takePhoto);
		takePhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});
		
		Button getLog = (Button) findViewById(R.id.getLog);
		getLog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				msg.setText(LogUtils.getLog());
			}
		});
		
		Button delLog = (Button) findViewById(R.id.delLog);
		delLog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LogUtils.clearLog();
				msg.setText(LogUtils.getLog());
			}
		});
		
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//			requestPermissions(new String[]{"android.permission.CAMERA"}, 0);
//		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		server.onDestroy();
		captureTask.stopCapture();
		System.exit(0);
	}

	
	private boolean takingPhoto = false;
	@SuppressWarnings("deprecation")
    public void takePhoto() {
		if (takingPhoto) return;
		takingPhoto = true;
        final SurfaceView preview = new SurfaceView(this);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new Callback() {
        	Camera camera = null;
        	
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                LogUtils.appendLog("2. Start taking photo");

                if (camera != null)
                    camera.release();

                try {
                    camera = Camera.open();
                    LogUtils.appendLog("3. Opened camera");

                    camera.setPreviewDisplay(holder);

                    camera.startPreview();

                    LogUtils.appendLog("Taking picture");
                    
                    camera.takePicture(null, null, new PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                        	LogUtils.appendLog("4. Took picture");
                            final Uri uri = FileUtils.onPictureTaken(data);
                            new Thread(new Runnable() {
								
								@Override
								public void run() {
									sendMail(uri.getPath());
								}
							}).start();
                            if (camera != null)
                            	camera.release();
                            takingPhoto = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (camera != null)
                        camera.release();
                    takingPhoto = false;
                    LogUtils.appendLog("!!!!!!!!!!! Taking photo failed " + e.getMessage());
                } 
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {
            }
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });
        surfaceLayout.removeAllViews();
        surfaceLayout.addView(preview, new LinearLayout.LayoutParams(1, 1));
    }
	
	public class CaptureWakeTask extends Thread {
		private boolean stop = false;
		private boolean restart = false;
		private Object lock = new Object();
		
		public void startCapture(){
			try {
				start();
			} catch (IllegalThreadStateException e) {
				restart = true;
				synchronized (lock) {
					lock.notify();
				}
			}
		}
		
		public void stopCapture(){
			stop = true;
			synchronized (lock) {
				lock.notify();
			}
		}
		
		@Override
		public void run() {
			while(!stop){
				restart = false;
				MainActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						MainActivity.this.takePhoto();
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!restart && !stop){
					try {
						synchronized (lock) {
							lock.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

    private GMailSender sender = new GMailSender("neklaken@gmail.com", "Kennguyen@6690#");
    private void sendMail(String fileName){
		try {
			sender.sendMail("Home has theft!",   
					"Becareful!",   
					" thiefdetector@googlegroups.com", new String[]{fileName});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}