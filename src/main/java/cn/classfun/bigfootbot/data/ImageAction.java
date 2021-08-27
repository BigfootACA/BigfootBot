package cn.classfun.bigfootbot.data;
import javax.annotation.Nullable;
public enum ImageAction{
	UNKNOWN(0),
	ACCEPT(1),
	REJECT(2),
	DENIED(3);
	int id;
	ImageAction(int i){id=i;}
	public int getID(){return id;}
	public static @Nullable ImageAction get(int id){
		for(ImageAction a:values())if(a.getID()==id)return a;
		return null;
	}
}
