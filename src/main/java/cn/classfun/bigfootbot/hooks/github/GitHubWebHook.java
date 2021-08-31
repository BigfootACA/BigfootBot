package cn.classfun.bigfootbot.hooks.github;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
public final class GitHubWebHook implements HttpHandler{
	public static GitHubEvent[]handlers={
		new GitHubPushEvent(),
		new GitHubReleaseEvent(),
	};
	public@Override void handle(HttpExchange e)throws IOException{
		final GitHubHookProcess d=new GitHubHookProcess(e);
		if(!d.init())return;
		try{
			for(GitHubEvent h:handlers){
				if(!h.isMatch(d))continue;
				h.process(d);
				d.close();
				return;
			}
			d.response(200,"not process");return;
		}catch(Exception x){
			x.printStackTrace();
		}
		d.close();
	}
}