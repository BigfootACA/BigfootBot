package cn.classfun.bigfootbot.data;
import cn.classfun.ipcalculator.exceptions.*;
import cn.classfun.ipcalculator.ipv4.types.*;
public final class IPCalcChinese{
	public static String ipv4Group2String(IPv4Group g){
		switch(g){
			case ANY:      return "RFC1122 此主机";
			case LOOP:     return "RFC1122 环回";
			case CGN:      return "RFC6598 共享地址空间";
			case CHAIN:    return "RFC3927 本地链路";
			case BENCHMARK:return "RFC2544 性能测试";
			case IP6TO4:   return "RFC3068 6to4中继选任播";
			case TEST_1:   return "RFC5737 文档(TEST-NET-1)";
			case TEST_2:   return "RFC5737 文档(TEST-NET-2)";
			case TEST_3:   return "RFC5737 文档(TEST-NET-3)";
			case IETF:     return "RFC6890 IETF协议分配";
			case LOCAL:    return "RFC1918 私网地址";
			case EXPER:    return "RFC1112 保留";
			case PUBLIC:   return "公网地址";
			case BROADCAST:return "广播地址";
			case MULTICAST:return "多播地址";
			default:return "未知";
		}
	}
	public static String ipv4Class2String(IPv4Class c){
		switch(c){
			case A:return "A类地址";
			case B:return "B类地址";
			case C:return "C类地址";
			case D:return "D类地址";
			case E:return "E类地址";
			case M:return "广播地址";
			default:return "未知";
		}
	}
	public static String ipv4Type2String(IPv4Type t){
		switch(t){
			case NET:      return "网络地址";
			case HOST:     return "主机地址";
			case BROADCAST:return "广播地址";
			default:return "未知";
		}
	}
	public static String exception2String(Exception e){
		if(e instanceof NullPointerException)return "计算器内部错误";
		else if(e instanceof IPInvalidAddressNumberException)return "无效的IP地址数字";
		else if(e instanceof IPInvalidAddressFormatException)return "无效的IP地址格式";
		else if(e instanceof IPInvalidAddressException)return "无效的IP地址";
		else if(e instanceof IPNotNetmaskAddressException)return "输入不是掩码地址";
		else if(e instanceof IPNotNetworkAddressException)return "输入不是网络地址";
		else if(e instanceof IPInvalidNetworkException)return "无效的网络";
		else if(e instanceof IPInvalidException)return "无效的IP";
		else return "无法解析的IP地址";
	}
	private IPCalcChinese(){throw new RuntimeException();}
}
