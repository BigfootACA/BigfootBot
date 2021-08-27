package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.data.Question.ask;
public final class AskCommand extends CommandMain{
	public @Override @Nonnull String getName(){return "ask";}
	public @Override void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		final StringBuilder sb=new StringBuilder();
		for(int i=1;i<args.length;i++)sb
			.append(args[i].trim())
			.append(' ');
		ask(e,sb.toString().trim());
	}
}
