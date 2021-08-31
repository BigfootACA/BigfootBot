package cn.classfun.bigfootbot.hooks.github;
import cn.classfun.bigfootbot.data.GitHubNotifier;
import net.mamoe.mirai.contact.Group;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.io.IOException;
import static cn.classfun.bigfootbot.BigfootBot.bot;
public final class GitHubPushEvent extends GitHubEvent{
	public@Override@Nonnull String getEventName(){return "push";}
	public@Override void process(GitHubHookProcess d) throws IOException{
		final JSONObject json=d.getJSON();
		final String name=json.getJSONObject("repository").getString("full_name");
		final GitHubNotifier ghn=GitHubNotifier.findRule(d,name);
		if(ghn==null)return;
		try{
			final StringBuilder sb=new StringBuilder();
			final JSONArray commits=json.getJSONArray("commits");
			sb.append("GitHub: ");
			sb.append(json.getJSONObject("pusher").getString("name"));
			sb.append("推送了").append(commits.length());
			sb.append("个提交到").append(name);
			int cmt=0;
			for(Object o:commits){
				final JSONObject jo=(JSONObject)o;
				String msg=jo.getString("message").replaceAll("\n\n","\n");
				if(msg.contains("\nSigned-off-by:"))msg=msg
					.substring(0,msg.indexOf("\nSigned-off-by:")).trim();
				sb.append("\n").append(jo.getString("id"),0,8).append(": ");
				if(msg.length()>40)sb.append(msg,0,40).append("...");
				else sb.append(msg);
				sb.append(" (来自");
				sb.append(jo.getJSONObject("author").getString("username"));
				sb.append(")");
				if(cmt++>5){
					sb.append("\n等");
					break;
				}
			}
			sb.append("\n").append(json.getString("compare"));
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
