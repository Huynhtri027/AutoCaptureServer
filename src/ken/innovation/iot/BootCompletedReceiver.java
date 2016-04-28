package ken.innovation.iot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, MainActivity.class);  
	    context.startActivity(i); 
	    LogUtils.appendLog("Restart");
	}

}
