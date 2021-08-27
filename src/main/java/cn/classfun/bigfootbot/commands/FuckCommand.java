package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
public final class FuckCommand extends CommandMain{
	public @Override @Nonnull String getName(){return "fuck";}
	public @Override void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		randomStringReplyShortSend("FUCK",e);
	}
}
