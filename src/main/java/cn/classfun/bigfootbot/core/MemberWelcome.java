package cn.classfun.bigfootbot.core;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import static cn.classfun.bigfootbot.config.Messager.randomStringAtWhoLongSend;
public final class MemberWelcome{
	private String msg_id;
	private boolean enabled=true;
	private final List<Long>groups=new ArrayList<>();
	private final static List<MemberWelcome>rules=new ArrayList<>();
	private MemberWelcome(){}
	public static void setRules(@Nonnull JSONArray ja){rules.clear();addRules(ja);}
	public static void addRules(@Nonnull JSONArray ja){ja.forEach(o->addRule((JSONObject)o));}
	public static void addRule(@Nonnull JSONObject jo){
		final MemberWelcome u=new MemberWelcome();
		u.msg_id=jo.getString("msg_id");
		if(jo.has("enabled"))u.enabled=jo.getBoolean("enabled");
		if(jo.has("groups"))Utils.longArrayParse(u.groups,jo.getJSONArray("groups"));
		rules.add(u);
	}
	private static @Nullable MemberWelcome findRule(long id){
		for(MemberWelcome u:rules){
			if(!u.enabled)continue;
			if(u.groups.size()==0)return u;
			for(long l:u.groups)if(id==l)return u;
		}
		return null;
	}
	public static void onMemberJoinEvent(MemberJoinEvent e){
		final Group g=e.getGroup();
		final NormalMember m=e.getMember();
		final MemberWelcome w=findRule(g.getId());
		if(w==null)return;
		if(g.getBotAsMember().isMuted())return;
		randomStringAtWhoLongSend(w.msg_id,g,m);
	}
}
