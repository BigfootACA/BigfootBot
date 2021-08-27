package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.core.Command.commands;
import static cn.classfun.bigfootbot.core.Utils.buildReplyMsgShort;
public abstract class HelpCommand extends CommandMain{
	public boolean isShow(@Nonnull CommandMain cmd){return cmd.isVisible();}
	public @Override final void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		final MessageChainBuilder b=buildReplyMsgShort(e);
		for(CommandMain c:commands){
			if(!isShow(c))continue;
			b.append('\n').append('/').append(c.getName());
			try{
				final String desc=c.getDescription();
				if(desc==null||desc.trim().length()<=0)
					throw new NullPointerException();
				b.append(" - ").append(desc);
			}catch(Exception ignore){}
		}
		e.getGroup().sendMessage(b.build());
	}
	public final static class DefaultHelpCommand extends HelpCommand{
		public @Override @Nonnull String getName(){return "help";}
	}
}
