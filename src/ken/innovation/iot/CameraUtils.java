package ken.innovation.iot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

public class CameraUtils {
	
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
		LogUtils.appendLog("Save bitmap: " + fileName);
		return returnUri;
	}
	

	public static Uri onPictureTaken(byte[] data) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
		try {
			return saveBitmap(bitmap);
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
		return CameraUtils.class.getPackage().getName();
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
