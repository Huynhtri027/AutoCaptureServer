package ken.innovation.iot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class LogUtils {
	private static Object lock = new Object();
	public static void appendLog(String text)
	{       
	   try {
		   Log.d("IoT", text);
		   synchronized (lock) {
			   File logFile = new File(CameraUtils.getDataPath()+ "log.txt");
			   if (!logFile.exists())
			   {
				   try
				   {
					   logFile.createNewFile();
				   } 
				   catch (IOException e)
				   {
					   // TODO Auto-generated catch block
					   e.printStackTrace();
				   }
			   }
			   try
			   {
				   //BufferedWriter for performance, true to set append to file flag
				   BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
				   buf.append(text);
				   buf.newLine();
				   buf.close();
			   }
			   catch (IOException e)
			   {
				   e.printStackTrace();
			   }
		   }
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	}
	
	public static String getLog() {
		try {
			synchronized (lock) {
				File logFile = new File(CameraUtils.getDataPath()+ "log.txt");
				if (!logFile.exists()) return null;
				StringBuilder builder = new StringBuilder();
				try {
					BufferedReader input = new BufferedReader(new FileReader(logFile));
					try {
						String temp = input.readLine();
						while (temp != null) {
							builder.append(temp).append("\n");
							temp = input.readLine();
						}
					} finally {
						input.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

    }
	
	public static void clearLog(){
		synchronized (lock) {
			File file = new File(CameraUtils.getDataPath());
			if (file.exists()){
				recursiveDelete(file.getPath());
			}
		}
	}
	
	private static void recursiveDelete(String fileName){
		File file = new File(fileName);
		if (!file.isDirectory()){
			file.delete();
		} else {
			String[] listFile = file.list();
			for (String f : listFile){
				recursiveDelete(fileName + "/" + f);
			}
		}
	}
}
