package cn.classfun.bigfootbot.hooks.github;
import java.io.IOException;
import static java.util.Objects.requireNonNull;
public abstract class GitHubEvent{
	public boolean isMatch(GitHubHookProcess d){
		return requireNonNull(getEventName())
			.equalsIgnoreCase(d.getEvent());
	}
	public String getEventName(){return null;}
	public abstract void process(GitHubHookProcess d)throws IOException;
}