package com.guoguang.jni;

public class JniCall {
	private static native int wlt2bmp(byte[] wlt, byte[] bmp, int bmpSave);
	static {
		System.loadLibrary("dewlt2-jni");
	}
	public static int Huaxu_Wlt2Bmp(byte[] wlt, byte[] bmp, int bmpSave) {
		return wlt2bmp(wlt, bmp, bmpSave);
	}
}

