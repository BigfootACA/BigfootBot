package cn.classfun.bigfootbot.data;
import cn.classfun.bigfootbot.core.Utils;
import cn.classfun.bigfootbot.hooks.github.GitHubHookProcess;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public final class GitHubNotifier{
	private boolean enabled=true;
	private String name,secret;
	private Mac msha1,msha256;
	private SecretKeySpec ssha1,ssha256;
	private final List<Long>groups=new ArrayList<>();
	private final static List<GitHubNotifier>rules=new ArrayList<>();
	public static void setRules(@Nonnull JSONArray ja){rules.clear();addRules(ja);}
	public static void addRules(@Nonnull JSONArray ja){ja.forEach(o->addRule((JSONObject)o));}
	public static void addRule(@Nonnull JSONObject jo){
		final GitHubNotifier u=new GitHubNotifier();
		if(jo.has("enabled"))u.enabled=jo.getBoolean("enabled");
		if(u.enabled)try{
			u.name=jo.getString("name");
			u.secret=jo.getString("secret");
			final byte[]b=u.secret.getBytes();
			u.ssha1=new SecretKeySpec(b,"HmacSHA1");
			u.ssha256=new SecretKeySpec(b,"HmacSHA256");
			u.msha1=Mac.getInstance("HmacSHA1");
			u.msha256=Mac.getInstance("HmacSHA256");
			u.msha1.init(u.ssha1);
			u.msha256.init(u.ssha256);
			Utils.longArrayParse(u.groups,jo.getJSONArray("groups"));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		rules.add(u);
	}
	public static @Nullable GitHubNotifier findRule(GitHubHookProcess e,String name) throws IOException{
		boolean mismatch=false;
		for(GitHubNotifier u:rules){
			if(
				!u.enabled||
				!u.name.equalsIgnoreCase(name)||
				u.groups.size()==0
			)continue;
			if(
				!e.checkSign1(u.msha1)||
				!e.checkSign256(u.msha256)
			){
				mismatch=true;
				System.err.println("check signature failed");
				continue;
			}
			return u;
		}
		if(mismatch)e.response(403,"invalid secret or no matched found");
		else e.response(403,"no matched found");
		return null;
	}
	public List<Long>getGroups(){return groups;}
	private GitHubNotifier(){}
}
