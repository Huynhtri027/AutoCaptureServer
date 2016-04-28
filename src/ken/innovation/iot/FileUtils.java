package ken.innovation.iot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

public class FileUtils {
	
	public static Uri saveBitmap(Bitmap bitmap) throws IOException{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 72, bytes);
		String fileName = System.currentTimeMillis() + ".jpeg";
		File f = new File(getDataPath() + fileName);
		Uri returnUri = Uri.fromFile(f);
		f.createNewFile();
		//write the bytes in file
		FileOutputStream fo = new FileOutputStream(f);
		fo.write(bytes.toByteArray());

		// remember close de FileOutput
		fo.close();
		bitmap.recycle();
		LogUtils.appendLog("Save bitmap: " + f.getPath());
		return returnUri;
	}
	
	public static Uri saveImage(byte[] data) throws IOException{
		String fileName = System.currentTimeMillis() + ".jpeg";
		File f = new File(getDataPath() + fileName);
		Uri returnUri = Uri.fromFile(f);
		f.createNewFile();
		//write the bytes in file
		FileOutputStream fo = new FileOutputStream(f);
		fo.write(data);

		// remember close de FileOutput
		fo.close();
		LogUtils.appendLog("Save bitmap: " + f.getPath());
		return returnUri;
	}
	
	public static void deleteFile(String[] files){
		for (String file : files){
			File f = new File(file);
			if (f.exists()){
				f.delete();
			}
		}
	}
	

	public static Uri onPictureTaken(byte[] data) {
		try {
			return saveImage(data);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean hasSDCard() {
		File root = Environment.getExternalStorageDirectory();
		return (root.exists() && root.canWrite());
	}
	public static String getPackagePath(){
		return FileUtils.class.getPackage().getName();
	}
	public static String getDataPath() {
		String dataFolderPath;
		if (hasSDCard()) {
			dataFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/data/"+ getPackagePath() +"/files/";
		} else {
			dataFolderPath = "/data/data/"+ getPackagePath() +"/files/";
		}
		File file = new File(dataFolderPath);
		file.mkdirs();
		return dataFolderPath;
	}

}
