package com.guoguang.jni.idcard;

import android.app.Activity;

import com.guoguang.jni.JniCall;


/**
 * 
 * @author zjxin2	on 2016-03-24
 *
 */
public class IDCardManager extends com.sdt.Sdtapi{
	private static final String TAG = "IDCardManager";

	private boolean hasFoundIdCard = false;
	
	
	public IDCardManager(Activity activity) throws Exception {
		super(activity);
	}
	
	
	/**
	 * Get Identify Card module ID
	 * @return
	 * @throws IDCardException
	 */
	public String getSAMId() throws IDCardException {
		char[] pucSAMID = new char[36];
		int ret = SDT_GetSAMIDToStr(pucSAMID);
		if(ret == 0x90) {
			return (new String(pucSAMID)).trim();
		} else {
			throw new IDCardException(ret, getExceptionDetail(ret));
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws IDCardException 
	 */
	public boolean isSAMStatusInNormal() {
		return (SDT_GetSAMStatus() == 0x90);
	}
	
	/**
	 * 
	 * @return
	 * @throws IDCardException 
	 */
	private void startFindIDCard() throws IDCardException {
		if(hasFoundIdCard) {
			hasFoundIdCard = false;
			return;
		}
		int ret = SDT_StartFindIDCard();
		hasFoundIdCard = true;
		if(ret != 0x9F) {
			hasFoundIdCard = false;
			throw new IDCardException(ret, getExceptionDetail(ret));
		}
	}
	
	/**
	 * 获取身份证基本信息
	 * @return
	 * @throws IDCardException
	 */
	public IDCardMsg getIDCardMsg() throws IDCardException {
		int ret = SDT_StartFindIDCard();
		if(ret != 0x9F) {
			throw new IDCardException(ret, getExceptionDetail(ret));
		}
		
		ret = SDT_SelectIDCard();
		if(ret != 0x90) {
			throw new IDCardException(ret, getExceptionDetail(ret));
		}
		
		int[] puiCHMsgLen = new int[1];
		int[] puiPHMsgLen = new int[1];
//		int[] puiFPMsgLen = new int[1];
		
		byte[] pucCHMsg = new byte[256];
		byte[] pucPHMsg = new byte[1024];
//		byte[] pucFPMsg = new byte[1024];

		ret = SDT_ReadBaseMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen);
//		ret = mSdtapi.SDT_ReadBaseFPMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen, pucFPMsg, puiFPMsgLen);
		if(ret != 0x90) {
			throw new IDCardException(ret, getExceptionDetail(ret));
		}
		
		IDCardMsg msg = new IDCardMsg();
		char[] pucCHMsgStr = new char[128];
		try {
			decodeByte(pucCHMsg, pucCHMsgStr);
			chars2Msg(pucCHMsgStr, msg);

			byte[] temp = new byte[14 + 40 + 308 * 126];
			ret = JniCall.Huaxu_Wlt2Bmp(pucPHMsg, temp, 0);
			System.out.println("HXS 获取到的身份证特征标识=======:"+ret);
			msg.setPortrait(temp);
			msg.setRet(ret);
//			temp = new byte[14 + 40 + 308 * 126];
//			r = Reader.dewlt(pucFPMsg, temp);
//			msg.fingerPrint = temp;
//			if(!msg.getIdCardNum().equals("")){
//				return msg;
//			}else{
//				return	null;
//			}
			return msg;
		} catch (Exception e) {
			throw new IDCardException(e.getMessage(), e);
		}
	}
	
	
	private void chars2Msg(char[] pucCHMsgStr, IDCardMsg msg) {
		msg.setName(String.copyValueOf(pucCHMsgStr, 0, 15).trim());
		
		String temp = String.copyValueOf(pucCHMsgStr, 15, 1).trim();
		msg.setSex(Integer.parseInt(temp));
		
		temp = String.copyValueOf(pucCHMsgStr, 16, 2).trim();
		msg.setNation(Integer.parseInt(temp));
		
//		msg.birthDate = new IDCardDate();
//		temp = String.copyValueOf(pucCHMsgStr, 18, 4).trim();
//		msg.birthDate.setYear(Integer.parseInt(temp));
//		temp = String.copyValueOf(pucCHMsgStr, 22, 2).trim();
//		msg.birthDate.setMonth(Integer.parseInt(temp));
//		temp = String.copyValueOf(pucCHMsgStr, 24, 2).trim();
//		msg.birthDate.setDay(Integer.parseInt(temp));
		msg.setBirthDate(new IDCardDate(
				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 18, 4).trim()), 
				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 22, 2).trim()), 
				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 24, 2).trim())));
		
		msg.setAddress(String.copyValueOf(pucCHMsgStr, 26, 35).trim());
		
		msg.setIdCardNum(String.copyValueOf(pucCHMsgStr, 61, 18).trim());
		
		msg.setSignOffice(String.copyValueOf(pucCHMsgStr, 79, 15).trim());
		
//		msg.usefulStartDate = new IDCardDate();
//		temp = String.copyValueOf(pucCHMsgStr, 94, 4).trim();
//		msg.usefulStartDate.setYear(Integer.parseInt(temp));
//		temp = String.copyValueOf(pucCHMsgStr, 98, 2).trim();
//		msg.usefulStartDate.setMonth(Integer.parseInt(temp));
//		temp = String.copyValueOf(pucCHMsgStr, 100, 2).trim();
//		msg.usefulStartDate.setDay(Integer.parseInt(temp));
		msg.setUsefulStartDate(new IDCardDate(
				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 94, 4).trim()), 
				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 98, 2).trim()), 
				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 100, 2).trim())));	
		
//		msg.usefulEndDate = new IDCardDate();
//		temp = String.copyValueOf(pucCHMsgStr, 102, 4).trim();
//		msg.usefulEndDate.setYear(Integer.parseInt(temp));
//		temp = String.copyValueOf(pucCHMsgStr, 106, 2).trim();
//		msg.usefulEndDate.setMonth(Integer.parseInt(temp));
//		temp = String.copyValueOf(pucCHMsgStr, 108, 2).trim();
//		msg.usefulEndDate.setDay(Integer.parseInt(temp));
		String str=String.copyValueOf(pucCHMsgStr, 102, 4).trim();
		if(str.equals("长期")){
			msg.setUsefulEndDate(str);
		}else{
			msg.setUsefulEndDate(String.copyValueOf(pucCHMsgStr, 102, 4).trim()+"-"+ 
					String.copyValueOf(pucCHMsgStr, 106, 2).trim()+"-"+
					String.copyValueOf(pucCHMsgStr, 108, 2).trim());
		}
//		msg.setUsefulEndDate(new IDCardDate(
//				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 102, 4).trim()), 
//				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 106, 2).trim()), 
//				Integer.parseInt(String.copyValueOf(pucCHMsgStr, 108, 2).trim())));
		
	}
	
	private void decodeByte(byte[] msg, char[] msg_str) throws Exception {
		byte[] newmsg = new byte[msg.length + 2];
		
		newmsg[0]=(byte) 0xff;
		newmsg[1] =(byte) 0xfe;
		
		for(int i=0;i<msg.length;i++) {
			newmsg[i+2]= msg[i];
		}
		
		String s = new String(newmsg, "UTF-16");
		for(int i = 0; i < s.toCharArray().length; i++) {
			msg_str[i] = s.toCharArray()[i];
		}
	}
	
	
	
	private String getExceptionDetail(int code) {
		String detailMsg = "Exception code : " + code;
		switch (code) {
		case 0x01:
			detailMsg += ". Fail to Open Serial port.";
			break;
			
		case 0x02:
			detailMsg += ". Read data overtime.";
			break;
			
		case 0x03:
			detailMsg += ". Data transmission error.";
			break;
			
		case 0x05:
			detailMsg += ". SAM_A serial port is unavailable.";
			break;
			
		case 0x09:
			detailMsg += ". Fail to open file.";
			break;
			
		case 0x10:
			detailMsg += ". The received data with a wrong checksum.";
			break;
			
		case 0x11:
			detailMsg += ". The received data with a wrong length.";
			break;
			
		case 0x21:
			detailMsg += ". The received data with a wrong command, including each value or logic error";
			break;
			
		case 0x23:
			detailMsg += ". Beyond authority.";
			break;
			
		case 0x24:
			detailMsg += ". With an unknow error.";
			break;
			
		case 0x80:
			detailMsg += ". Can't find identity card.";
			break;
			
		case 0x81:
			detailMsg += ". Fail to seclet identity card.";
			break;
			
		case 0x31:
			detailMsg += ". Identity card fail to identify SAM_A.";
			break;
			
		case 0x32:
			detailMsg += ". SAM_A fail to identify identity card.";
			break;
			
		case 0x33:
			detailMsg += ". Fail to identify the information.";
			break;
			
		case 0x40:
			detailMsg += ". Can't recognize the type of identity card.";
			break;
			
		case 0x41:
			detailMsg += ". Fail to read identity card.";
			break;
			
		case 0x47:
			detailMsg += ". Fail to read random number.";
			break;
			
		case 0x60:
			detailMsg += ". SAM_A fail to execute self-inspection, can't receive conmmand.";
			break;
			
		case 0x66:
			detailMsg += ". SAM_A is unauthorized, can't be used.";
			break;

		default:
			break;
		}
		return detailMsg;
	}
	
	
//	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
//				
//			} else if(mCommon.ACTION_USB_PERMISSION.equals(intent.getAction())) {
//				
//			}
//			
//		}
//	};
//	
//	private void initUsbReceive() {
//		mCommon = new Common();
//		IntentFilter filter = new IntentFilter();
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED); // USB device was extracted
//        filter.addAction(mCommon.ACTION_USB_PERMISSION); //自定义的USB设备请求授权
//        mActivity.registerReceiver(mUsbReceiver, filter);
//	}
//	
//	
//	public void free() {
//		mActivity.unregisterReceiver(mUsbReceiver);
//	}

}
