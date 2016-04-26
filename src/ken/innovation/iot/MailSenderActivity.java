//package ken.innovation.iot;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.widget.TextView;
//
//public class MailSenderActivity extends Activity {
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(new TextView(this));
//        
//        new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				GMailSender sender = new GMailSender("neklaken@gmail.com", "Kennguyen@6690#");
//				try {
//					sender.sendMail("This is Subject",   
//							"This is Body",   
//							"kennguyen.hut@gmail.com",   
//							"khanhnq2@vng.com.vn", new String[]{});
//				} catch (Exception e) {
//					e.printStackTrace();
//				}  
//			}
//		}).start();
//
//        finish();
//
//    }
//}
