package cn.classfun.bigfootbot.config;
import cn.classfun.bigfootbot.core.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ims.v20201229.ImsClient;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cn.classfun.bigfootbot.core.Storage.getStorage;
import static cn.classfun.bigfootbot.core.Utils.longArrayParse;
import static cn.classfun.bigfootbot.data.Question.parseQuestions;
import static cn.classfun.utils.StreamUtils.stream2string;
import static java.util.Objects.requireNonNull;
public final class ConfigParser{
	public static void parseConfig(@Nonnull Config c,@Nonnull String cfg)throws IOException{
		parseConfig(c,new File(cfg));
	}
	public static void parseConfig(@Nonnull Config c,@Nonnull File cfg)throws IOException{
		if(!cfg.exists()||!cfg.isFile())throw new FileNotFoundException();
		if(cfg.length()>Integer.MAX_VALUE)throw new IOException("Config file too large");
		parseConfig(c,new JSONObject(stream2string(new FileInputStream(cfg),(int)cfg.length())));
	}
	public static void parseConfig(@Nonnull Config c,@Nonnull InputStream cfg)throws IOException{
		parseConfig(c,new JSONObject(stream2string(cfg)));
	}
	private static void parseGroups(@Nonnull Config c,@Nonnull JSONArray groups){
		c.setGroup(longArrayParse(groups));
	}
	private static void parseStrings(@Nonnull Config c,@Nonnull JSONArray strings){
		final Map<String,List<String>> strs=new HashMap<>();
		strings.forEach(o->{
			final JSONObject jo=(JSONObject)o;
			final JSONArray res=jo.getJSONArray("string");
			final String id=requireNonNull(jo.getString("id"));
			final List<String>list=new ArrayList<>();
			res.forEach(x->list.add((String)x));
			strs.put(id,list);
		});
		c.setStrings(strs);
	}
	private static void parseCommands(@Nonnull Config c,@Nonnull JSONArray commands){
		final List<String>cmds=new ArrayList<>();
		commands.forEach(o->cmds.add((String)o));
		c.setCommands(cmds);
	}
	private static void parseQQ(@Nonnull Config c,@Nonnull JSONObject qq){
		c.setQQNumber(qq.getLong("number"));
		c.setQQPassword(qq.getString("password"));
	}
	private static void parseTencentCloudAPI(@Nonnull Config c,@Nonnull JSONObject api){
		if(!api.getBoolean("enabled"))return;
		final Credential cred =new Credential(
			api.getString("secretid"),
			api.getString("secretkey")
		);
		final JSONObject eps=api.getJSONObject("endpoints");
		final HttpProfile h=new HttpProfile();
		final ClientProfile cl=new ClientProfile();
		h.setEndpoint(eps.getString("ims"));
		cl.setHttpProfile(h);
		c.setImsClient(new ImsClient(cred,api.getString("region"),cl));
	}
	public static void parseConfig(@Nonnull Config c,@Nonnull JSONObject cfg){
		parseQQ(c,cfg.getJSONObject("qq"));
		parseTencentCloudAPI(c,cfg.getJSONObject("tencentcloudapi"));
		Storage s=getStorage();
		s.parseStorage(c,cfg.getJSONObject("storage"));
		parseQuestions(c,cfg.getJSONArray("questions"));
		parseGroups(c,cfg.getJSONArray("groups"));
		parseStrings(c,cfg.getJSONArray("strings"));
		if(cfg.has("commands"))parseCommands(c,cfg.getJSONArray("commands"));
		if(cfg.has("googletransapi"))Translate.setAPI(cfg.getString("googletransapi"));
		UserChangeName.setRules(cfg.getJSONArray("change_name"));
		MemberWelcome.setRules(cfg.getJSONArray("member_welcome"));
		JoinRequest.setRules(cfg.getJSONArray("joinreq"));
	}
}
