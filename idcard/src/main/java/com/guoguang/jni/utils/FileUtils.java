package com.guoguang.jni.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author zjxin2 on 2016-05-15
 *
 */
public class FileUtils {

	private static final String IMG_PATH =Environment.getExternalStorageDirectory()//GrgFaceAIOApp.PARENT_FILE_PATH
			+ "/img";
	//public static final String IMG_PATH = Environment.getExternalStorageDirectory() + File.separator+"faceDb/";
	public static final String IMG_CARD = "card.jpg";//身份证照片路径

	private static final String IMG_CAPTURE = "capture.jpg";//拍照图像

	private static final String IMG_CAPTURE_FACE = "capture_face.jpg";
	/**
	 * 判断抓取的人脸图片是否存在
	 * @return
	 */
	public boolean isCaptureFaceExist() {
		if((new File(getCaptureFaceImgPath())).exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取拍照图像人脸文件路径
	 * 
	 * @return
	 */
	public static String getCaptureFaceImgPath() {
		return IMG_PATH + File.separator + IMG_CAPTURE_FACE;
	}


	/**
	 * 获取拍照图像文件路径
	 * 
	 * @return
	 */
	public String getCaptureImgPath() {
		return IMG_PATH + File.separator + IMG_CAPTURE;
	}
	
	/**
	 * 获取身份证照片文件路径
	 * 
	 * @return
	 */
	public String getCardImgPath() {
		return IMG_PATH + File.separator + IMG_CARD;
	}

	/**
	 * 获取拍照图像
	 * 
	 * @return
	 */
	public byte[] getCaptureImg() {
		return getImg(IMG_PATH + "/" + IMG_CAPTURE);
	}


	/**
	 * 获取身份证头像图像
	 * 
	 * @return
	 */
	public byte[] getCardImg() {
		return getImg(IMG_PATH + "/" + IMG_CARD);
	}

	/**
	 * 获取图像数据
	 * 
	 * @param filePath
	 * @return
	 */
	public byte[] getImg(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return new byte[1];
		}
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		bitmap.recycle();

		return bytes;
	}

	/**
	 * 保存所拍图像
	 * 
	 * @param data
	 * @param rotateDegree 旋转角度
	 * @return
	 */
	public boolean saveCaptureImg(byte[] data, int rotateDegree) {
		return saveImg(data, IMG_CAPTURE, rotateDegree);
	}
	/**
	 * 保存身份证头像
	 * 
	 * @param data
	 * @return
	 */
	public boolean saveCardImg(byte[] data) {
		return saveImg(data, IMG_CARD, 0);
	}

	/**
	 * 保存图片
	 * 
	 * @param data
	 * @param fileName
	 * @return
	 */
	public boolean saveImg(byte[] data, String fileName, int rotateDegree) {
		if (data == null) {
			return false;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}

		boolean result = false;
		File file = new File(IMG_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file, fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bitmap == null) {
				return false;
			}
			if(rotateDegree != 0) {
				Matrix matrix = new Matrix();
			    matrix.postRotate(rotateDegree);
			    try {
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			bitmap.recycle();
			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}
	
	
	public String getImgParentPath() {
		return IMG_PATH;
	}
	
	
	public static Bitmap getFaceBitmap() {
		return BitmapFactory.decodeFile(getCaptureFaceImgPath());
	}

	/**
	 * 从照片中截取人脸并保存
	 * @param rect
	 * @return
	 */
	public boolean catchAndSaveFaceImp(RectF rect) {
		Bitmap bitmap = BitmapFactory.decodeFile(getCaptureImgPath());
		bitmap = Bitmap.createBitmap(bitmap, Math.round(rect.left), Math.round(rect.top), 
				Math.round(rect.right - rect.left), Math.round(rect.bottom - rect.top));
		if(bitmap == null) {
			return false;
		}
		File file = new File(IMG_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file, IMG_CAPTURE_FACE);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			bitmap.recycle();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	

	/**
	 * 复制单个文件(可更名复制)
	 * 
	 * @param oldPathFile
	 *            准备复制的文件源
	 * @param newPathFile
	 *            拷贝到新绝对路径带文件名(注：目录路径需带文件名)
	 * @return
	 */
	public boolean copySingleFile(String oldPathFile, String newPathFile) {
		boolean result = false;
		try {
//			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
				@SuppressWarnings("resource")
				FileOutputStream fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
//					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * 复制整个文件夹的内容(含自身)
	 * 
	 * @param oldPath
	 *            准备拷贝的目录
	 * @param newPath
	 *            指定绝对路径的新目录
	 * @return
	 */
	public static void copyFolderWithSelf(String oldPath, String newPath) {
		try {
			new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File dir = new File(oldPath);
			// 目标
			newPath += File.separator + dir.getName();
			File moveDir = new File(newPath);
			if (dir.isDirectory()) {
				if (!moveDir.exists()) {
					moveDir.mkdirs();
				}
			}
			String[] file = dir.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) { // 如果是子文件夹
					copyFolderWithSelf(oldPath + "/" + file[i], newPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static final String IMG_CAPTURE_0 = "captured_0.jpg";

	public static final String IMG_FACE_0 = "face_0.jpg";
	
	public static final String IMG_CAPTURE_1 = "captured_1.jpg";

	public static final String IMG_FACE_1 = "face_1.jpg";
	
	public String getImgPath(String imgName) {
		return IMG_PATH + File.separator + imgName;
	}
	
	/**
	 * 判断人脸图片是否存在
	 * @param faceImgName
	 * @return
	 */
	public boolean isFaceImgExist(String faceImgName) {
		if((new File(getImgPath(faceImgName))).exists()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 获取拍照图像文件路径
	 * 
	 * @return
	 */
	public String getFaceImgPath() {
		return IMG_PATH + File.separator + IMG_FACE_0;
	}
	public String getFace1ImgPath() {
		return IMG_PATH + File.separator + IMG_FACE_1;
	}
	
	/**
	 * 获取拍照图像
	 * 
	 * @return
	 */
	public byte[] getFaceImg() {
		return getImg(IMG_PATH + "/" + IMG_FACE_0);
	}
	public byte[] getFace1Img() {
		return getImg(IMG_PATH + "/" + IMG_FACE_1);
	}
	/**
	 * 保存所拍图像
	 * 
	 * @param data
	 * @param rotateDegree 旋转角度
	 * @return
	 */
	public boolean saveFaceImg(byte[] data, int rotateDegree) {
		return saveImg(data, IMG_FACE_0, rotateDegree);
	}
	public boolean saveFace1Img(byte[] data, int rotateDegree) {
		return saveImg(data, IMG_FACE_1, rotateDegree);
	}
	
	/**
	 * 从照片中截取人脸并保存
	 * @param rectf	检测到人脸位置
	 * @param orginalImgPath	原图路径
	 * @param faceImgName	要保存的人脸图片名称
	 * @return
	 */
	public boolean catchFaceImg(RectF rectf, String orginalImgPath, String faceImgName) {
		Bitmap bitmap = BitmapFactory.decodeFile(orginalImgPath);
		bitmap = Bitmap.createBitmap(bitmap, Math.round(rectf.left), Math.round(rectf.top), 
				Math.round(rectf.right - rectf.left), Math.round(rectf.bottom - rectf.top));
		if(bitmap == null) {
			return false;
		}
		File file = new File(IMG_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file, faceImgName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			bitmap.recycle();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

}
