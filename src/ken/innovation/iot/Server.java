package ken.innovation.iot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Intent;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

public class Server {
	MainActivity activity;
	ServerSocket serverSocket;
	String message = "";
	static final int socketServerPORT = 6690;

	public Server(MainActivity activity) {
		this.activity = activity;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}

	public int getPort() {
		return socketServerPORT;
	}

	public void onDestroy() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LogUtils.appendLog("Close server");
	}

	private class SocketServerThread extends Thread {

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(socketServerPORT);

				while (!isInterrupted()) {
					Socket socket = serverSocket.accept();
					
					new SocketGetDataThread(socket).start();
					
					LogUtils.appendLog("========New Client connected!========");
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogUtils.appendLog("Server finished!");
		}

	}
	
	private class SocketGetDataThread extends Thread {
//		private Socket socket;
		BufferedReader input;
		private boolean isStop = false;
		public SocketGetDataThread(Socket socket){
//			this.socket = socket;
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			int count = 0;
			while (!isStop && !isInterrupted()){
				try {
					final String s = input.readLine();
					if (s == null){
						return;
					} else if (s.contains("1")){
						count ++;
					}
					message = s + "(" + count + ")";
					LogUtils.appendLog("***" + message + " - " + DateFormat.format("hh:mm:ss dd/MM/yy", System.currentTimeMillis()));
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if (s.contains("1")){
								activity.takePhoto();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			LogUtils.appendLog("////////////GetDataThread finished!//////////");
			LogUtils.appendLog("////////////////////////////////////////////");
			
		}
	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;

		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			cnt = c;
		}

		@Override
		public void run() {
			OutputStream outputStream;
			String msgReply = "Hello from Server, you are #" + cnt;

			try {
				outputStream = hostThreadSocket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				printStream.print(msgReply);
				printStream.close();

				message += "replayed: " + msgReply + "\n";

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					activity.msg.setText(message);
				}
			});
		}

	}

	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += inetAddress.getHostAddress();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip = null;
		}
		return ip;
	}
}
