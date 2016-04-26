//package ken.innovation.iot;
//
//import java.io.IOException;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.PixelFormat;
//import android.hardware.Camera;
//import android.hardware.Camera.PictureCallback;
//import android.net.Uri;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceHolder.Callback;
//import android.view.SurfaceView;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//
//
//
///** Takes a single photo on service start. */
//public class TakingPhotoService extends Service {
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        LogUtils.appendLog("###################################");
//        LogUtils.appendLog("1. Service creating!");
//        takePhoto(this);
//    }
//
//    @SuppressWarnings("deprecation")
//    private void takePhoto(final Context context) {
//        final SurfaceView preview = new SurfaceView(context);
//        SurfaceHolder holder = preview.getHolder();
//        // deprecated setting, but required on Android versions prior to 3.0
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        holder.addCallback(new Callback() {
//        	Camera camera = null;
//        	
//            @Override
//            //The preview must happen at or after this point or takePicture fails
//            public void surfaceCreated(SurfaceHolder holder) {
//                LogUtils.appendLog("2. Start taking photo");
//
//                if (camera != null)
//                    camera.release();
//
//                try {
//                    camera = Camera.open();
//                    LogUtils.appendLog("3. Opened camera");
//
//                    try {
//                        camera.setPreviewDisplay(holder);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    camera.startPreview();
//
//                    camera.takePicture(null, null, new PictureCallback() {
//
//                        @Override
//                        public void onPictureTaken(byte[] data, Camera camera) {
//                        	LogUtils.appendLog("4. Took picture");
//                            final Uri uri = CameraUtils.onPictureTaken(data);
//                            new Thread(new Runnable() {
//								
//								@Override
//								public void run() {
//									sendMail(uri.getPath());
//								}
//							}).start();
//                            if (camera != null)
//                            	camera.release();
//                            TakingPhotoService.this.stopSelf();
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (camera != null)
//                        camera.release();
//                    TakingPhotoService.this.stopSelf();
//                    LogUtils.appendLog("!!!!!!!!!!! Taking photo failed " + e.getMessage());
//                } 
//            }
//
//            @Override public void surfaceDestroyed(SurfaceHolder holder) {
//            	if (camera != null)
//                    camera.release();
//            	camera = null;
//            }
//            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
//        });
//
//        WindowManager wm = (WindowManager)context
//            .getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                1, 1, //Must be at least 1x1
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                0,
//                //Don't know if this is a safe default
//                PixelFormat.UNKNOWN);
//
//        //Don't set the preview visibility to GONE or INVISIBLE
//        wm.addView(preview, params);
//    }
//
//    
//    private void sendMail(String fileName){
//    	GMailSender sender = new GMailSender("neklaken@gmail.com", "Kennguyen@6690#");
//		try {
//			sender.sendMail("This is Subject",   
//					"This is Body",   
//					"neklaken@gmail.com",   
//					"kennguyen.hut@gmail.com,khanhnq2@vng.com.vn", new String[]{fileName});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }
//    
//    @Override
//    public void onDestroy() {
//    	LogUtils.appendLog("7. Service Destroying");
//    	LogUtils.appendLog("###################################");
//    	super.onDestroy();
//    }
//
//    @Override public IBinder onBind(Intent intent) { return null; }
//}