package com.guoguang.jni.idcard;

public class IDCardException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362731666310304555L;
	
	
	private int code;

	public IDCardException(int code, String detailMessage) {
		super(detailMessage);
		this.code = code;
	}
	
	public IDCardException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}
	
	
	public IDCardException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public IDCardException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
