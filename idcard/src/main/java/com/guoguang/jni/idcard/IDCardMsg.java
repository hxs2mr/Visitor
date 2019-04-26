package com.guoguang.jni.idcard;

public class IDCardMsg {
	private String name = "";//名字
	private int sex = 0;		//性别		// 0:unknow, 1:man, 2:woman, 9:no explain
	private int nation = 0;//民族
	private IDCardDate birthDate;
	private String address = "";//地址
	private String idCardNum = "";//身份证号
	private String signOffice = "";//签发机关
				
	private IDCardDate usefulStartDate;//开始日期
	private String usefulEndDate;//结束日期
	private int ret=0;//证件  //1 表示是身份证识别
	private byte[] portrait;
//	public byte[] fingerPrint;


	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	private int genRespCode_HuaXu(int code) {
		switch (code) {
		case 0x90:					// 操作成功
		case 0x9F:
			return 0x00;
			
		case 0x02:					// 数据传输与接收失败
		case 0x03:
		case 0x10:
		case 0x11:
		case 0x21:
			return 0x01;

		case 0x23:					//	无权限操作
		case 0x66:
			return 0x02;
			
		case 0x31:					// 认证失败
		case 0x32:
			return 0x03;
			
		case 0x40:					// 识别身份证失败
		case 0x41:
			return 0x04;
			
		case 0x80:					// 寻找卡/证失败
		case 0x81:
			return 0x05;
			
		default:					// 获取身份证信息失败
			return -1;
		}
	}
	
	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}
	
	public int getSex() {
		return sex;
	}

	void setSex(int sex) {
		this.sex = sex;
	}

	public int getNation() {
		return nation;
	}

	void setNation(int nation) {
		this.nation = nation;
	}

	public String getAddress() {
		return address;
	}

	void setAddress(String address) {
		this.address = address;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}

	public String getSignOffice() {
		return signOffice;
	}

	void setSignOffice(String signOffice) {
		this.signOffice = signOffice;
	}

	public byte[] getPortrait() {
		return portrait;
	}

	void setPortrait(byte[] portrait) {
		this.portrait = portrait;
	}

	public IDCardDate getBirthDate() {
		return birthDate;
	}

	void setBirthDate(IDCardDate birthDate) {
		this.birthDate = birthDate;
	}

	public IDCardDate getUsefulStartDate() {
		return usefulStartDate;
	}

	void setUsefulStartDate(IDCardDate usefulStartDate) {
		this.usefulStartDate = usefulStartDate;
	}

	public String getUsefulEndDate() {
		return usefulEndDate;
	}

	void setUsefulEndDate(String usefulEndDate) {
		this.usefulEndDate = usefulEndDate;
	}
	
	
	public String getSexStr() {
		switch (getSex()) {
		case 1:
			return "男";
			
		case 2:
			return "女";

		default:
			return "未知";
		}
		
	}
	public String getNationStr() {
		if(getNation() > NATION_TABLE.length - 1 || nation < 0) {
			return "未知";
		}
		return NATION_TABLE[nation];
	}
	
	/** Nation table */
	public static final String[] NATION_TABLE = {
		"未知", "汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依",
		"朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎",
		"傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "克尔克孜",
		"土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌",
		"普米", "塔吉克", "怒", "乌兹别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京",
		"塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺"
	};
}
