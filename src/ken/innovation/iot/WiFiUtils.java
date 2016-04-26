package ken.innovation.iot;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;


public class WiFiUtils {
	public interface ValueCallback<T> {
	    public void onReceiveValue(T value);
	};
	
	public static void checkHosts(final String subnet, final  ValueCallback<ArrayList<String>> callback){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<String> listStrings = new ArrayList<>();
				try {
					int timeout=50;
					for (int i=1;i<255;i++){
						String host=subnet + "." + i;
						if (InetAddress.getByName(host).isReachable(timeout)){
							listStrings.add(host);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
//				try {
//			        Enumeration nis = NetworkInterface.getNetworkInterfaces();
//			        while(nis.hasMoreElements())
//			        {
//			            NetworkInterface ni = (NetworkInterface) nis.nextElement();
//			            Enumeration ias = ni.getInetAddresses();
//			            while (ias.hasMoreElements())
//			            {
//			                InetAddress ia = (InetAddress) ias.nextElement();
//			                listStrings.add(ia.getHostAddress());
//			            }
//
//			        }
//			    } catch (SocketException ex) {
//			        ex.printStackTrace();
//			    }
				callback.onReceiveValue(listStrings);
			}
		}).start();
	}
}
