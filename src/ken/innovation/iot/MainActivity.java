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
		
		final EditText modText = (EditText) findViewById(R.id.mod_ip_text);
		Button changeBut = (Button) findViewById(R.id.change_mod_ip);
		Button conBut = (Button) findViewById(R.id.connect_mod);
		checkHost(modText);
		changeBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (adds.size() > 1){
					adds.remove(0);
					modText.setText(adds.get(0));
				} else {
					checkHost(modText);
				}
			}
		});
		conBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ip = modText.getText().toString();
				if (ip.length() > 0){
					//TODO connect
				}
			}
		});
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			requestPermissions(new String[]{"android.permission.CAMERA"}, 0);
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		server.onDestroy();
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
                            final Uri uri = CameraUtils.onPictureTaken(data);
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

    
    private void sendMail(String fileName){
    	GMailSender sender = new GMailSender("neklaken@gmail.com", "Kennguyen@6690#");
		try {
			sender.sendMail("This is Subject",   
					"This is Body",   
					"kennguyen.hut@gmail.com,khanhnq2@vng.com.vn", new String[]{fileName});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void checkHost(final EditText et){
    	WiFiUtils.checkHosts("192.168.1", new ValueCallback<ArrayList<String>>() {
			
			@Override
			public void onReceiveValue(final ArrayList<String> value) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						adds.clear();
						adds.addAll(value);
						if (adds.size() > 0){
							et.setText(adds.get(0));
						}
					}
				});
			}
		});
    }
}