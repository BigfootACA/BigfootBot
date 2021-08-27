package cn.classfun.bigfootbot.core;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import static cn.classfun.bigfootbot.core.Utils.iFeelNoisy;
import static cn.classfun.bigfootbot.config.Messager.randomStringAtWhoShortSend;
public final class UserChangeName{
	private String msg_id;
	private boolean enabled=true;
	private int mute=60,warn_time=60;
	private final List<Long>groups=new ArrayList<>();
	private final static List<UserChangeName>rules=new ArrayList<>();
	private final static Map<Long,Map<Long,Long>>users=new HashMap<>();
	private static boolean notChange(NormalMember m){
		return
			m.getNameCard().trim().length()==0||
			m.getNameCard().equalsIgnoreCase(m.getNick());
	}
	public static boolean checkMemberName(Group g,NormalMember m){
		if(!notChange(m))return false;
		if(!Utils.iamAdmin(g))return false;
		if(Utils.isAdmin(m))return false;
		final long gid=g.getId();
		final long uid=m.getId();
		final UserChangeName rule=findRule(gid);
		if(rule==null)return false;
		final Map<Long,Long>grp=users.containsKey(gid)?
			users.get(gid):new HashMap<>();
		final long cur=System.currentTimeMillis();
		final Long last=grp.get(uid);
		if(last!=null){
			final long age=(cur-last)/1000;
			if(age>=rule.warn_time){
				m.mute(rule.mute);
				randomStringAtWhoShortSend(rule.msg_id,g,m);
			}
			return true;
		}
		grp.put(uid,cur);
		users.put(gid,grp);
		randomStringAtWhoShortSend(rule.msg_id,g,m);
		return true;
	}
	public static boolean conflictName(Group grp,NormalMember x){
		final String myname=grp.getBotAsMember().getNick();
		if(
			!x.getNick().equalsIgnoreCase(myname)&&
			!x.getNameCard().equalsIgnoreCase(myname)
		)return false;
		iFeelNoisy(x,30*60);
		return true;
	}
	public static void setRules(@Nonnull JSONArray ja){rules.clear();addRules(ja);}
	public static void addRules(@Nonnull JSONArray ja){ja.forEach(o->addRule((JSONObject)o));}
	public static void addRule(@Nonnull JSONObject jo){
		final UserChangeName u=new UserChangeName();
		u.msg_id=jo.getString("msg_id");
		if(jo.has("enabled"))u.enabled=jo.getBoolean("enabled");
		if(jo.has("mute"))u.mute=jo.getInt("mute");
		if(jo.has("warn_time"))u.warn_time=jo.getInt("warn_time");
		if(jo.has("groups"))Utils.longArrayParse(u.groups,jo.getJSONArray("groups"));
		rules.add(u);
	}
	private static @Nullable UserChangeName findRule(long id){
		for(UserChangeName u:rules){
			if(!u.enabled)continue;
			if(u.groups.size()==0)return u;
			for(long l:u.groups)if(id==l)return u;
		}
		return null;
	}
	private UserChangeName(){}
}
