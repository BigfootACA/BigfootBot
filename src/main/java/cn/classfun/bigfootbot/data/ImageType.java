package cn.classfun.bigfootbot.data;
import javax.annotation.Nullable;
public enum ImageType{
	UNKNOWN(0),
	NORMAL(1),
	PORN(2),
	ABUSE(3),
	AD(4),
	OTHER(5);
	int id;
	ImageType(int i){id=i;}
	public int getID(){return id;}
	public static @Nullable ImageType get(int id){
		for(ImageType a:values())if(a.getID()==id)return a;
		return null;
	}
	public static ImageType getType(String lbl){
		for(ImageType a:values())
			if(lbl.equalsIgnoreCase(a.name()))
				return a;
		return OTHER;
	}
}
