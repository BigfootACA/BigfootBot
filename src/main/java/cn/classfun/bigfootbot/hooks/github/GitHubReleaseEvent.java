package cn.classfun.bigfootbot.hooks.github;
import cn.classfun.bigfootbot.data.GitHubNotifier;
import net.mamoe.mirai.contact.Group;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.io.IOException;
import static cn.classfun.bigfootbot.BigfootBot.bot;
public final class GitHubReleaseEvent extends GitHubEvent{
	public@Override@Nonnull
	String getEventName(){return "release";}
	public@Override void process(GitHubHookProcess d) throws IOException{
		final JSONObject json=d.getJSON();
		final JSONObject rel=json.getJSONObject("release");
		final String name=json.getJSONObject("repository").getString("full_name");
		final GitHubNotifier ghn=GitHubNotifier.findRule(d,name);
		if(ghn==null)return;
		if(!json.getString("action").equalsIgnoreCase("released")){
			d.response(200,"done");
			return;
		}
		try{
			final StringBuilder sb=new StringBuilder();
			sb.append("GitHub: ");
			sb.append(rel.getJSONObject("author").getString("login"));
			sb.append("发布了").append(name).append("的新版本");
			sb.append(rel.getString("name")).append("\n");
			final String body=rel.getString("body");
			if(body.length()<=256)sb.append(body);
			else sb.append(body,0,256).append("...");
			sb.append("\n").append(rel.getString("html_url"));
			for(long l:ghn.getGroups()){
				final Group g=bot.getGroup(l);
				if(g==null||g.getBotMuteRemaining()>0)continue;
				g.sendMessage(sb.toString());
			}
			d.response(200,"done");
		}catch(Exception e){
			d.response(500,"bot error");
			e.printStackTrace();
		}
	}
}
