package cn.classfun.bigfootbot.core;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static java.lang.String.format;
public  final class JoinRequest{
	private int max_try=0;
	private String fail_msg_id;
	private boolean enabled=true;
	private final List<Long>groups=new ArrayList<>();
	private final List<String>must=new ArrayList<>();
	private final List<String>appear=new ArrayList<>();
	private final static Map<Long,Map<Long,Integer>>tries=new HashMap<>();
	private final static List<JoinRequest>rules=new ArrayList<>();
	private JoinRequest(){}
	public static void setRules(@Nonnull JSONArray ja){rules.clear();addRules(ja);}
	public static void addRules(@Nonnull JSONArray ja){ja.forEach(o->addRule((JSONObject)o));}
	public static void addRule(@Nonnull JSONObject jo){
		final JoinRequest u=new JoinRequest();
		u.fail_msg_id=jo.getString("fail_msg_id");
		if(jo.has("max_try"))u.max_try=jo.getInt("max_try");
		if(jo.has("enabled"))u.enabled=jo.getBoolean("enabled");
		if(jo.has("groups"))Utils.longArrayParse(u.groups,jo.getJSONArray("groups"));
		if(jo.has("rule")){
			final JSONObject r=jo.getJSONObject("rule");
			if(r.has("must"))r.getJSONArray("must").forEach(o->u.must.add((String)o));
			if(r.has("appear"))r.getJSONArray("appear").forEach(o->u.appear.add((String)o));
		}
		rules.add(u);
	}
	public boolean check(String str){
		if(str==null)return false;
		final String msg=str.trim().toLowerCase();
		if(msg.length()<=0)return false;
		for(String m:must)if(!msg.contains(m.toLowerCase()))return false;
		if(appear.size()<=0)return true;
		for(String a:appear)if(msg.contains(a.toLowerCase()))return true;
		return false;
	}
	private static @Nullable JoinRequest findRule(long id){
		for(JoinRequest u:rules){
			if(!u.enabled)continue;
			if(u.groups.size()==0)return u;
			for(long l:u.groups)if(id==l)return u;
		}
		return null;
	}
	public static void onMemberJoinRequestEvent(MemberJoinRequestEvent e){
		final Group g=e.getGroup();
		final long num=e.getFromId();
		if(g==null)return;
		final long grp=g.getId();
		final JoinRequest j=findRule(g.getId());
		if(j==null||(j.must.size()<=0&&j.appear.size()<=0))return;
		Map<Long,Integer>tried=tries.get(grp);
		if(tried==null)tried=new HashMap<>();
		final boolean success=j.check(e.getMessage());
		String str=cfg.getStringsRandom(j.fail_msg_id);
		if(str==null)str="";
		blog.info(format(
			"%s(%d) in group %d %s because %s",
			e.getFromNick(),num,grp,
			success?"success":"failed",str
		));
		if(success){
			tried.remove(num);
			e.accept();
		}else{
			int t=tried.getOrDefault(num,0);
			t++;
			final boolean block=t>=j.max_try&&j.max_try>0;
			e.reject(block,str);
			if(block)tried.remove(num);
			else tried.put(num,t);
		}
		tries.put(grp,tried);
	}
}
