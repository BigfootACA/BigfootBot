package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.core.Utils.iFeelNoisy;
import static cn.classfun.bigfootbot.config.Config.cfg;
public abstract class SummonCommand extends CommandMain{
	private long last=0;
	private int times=0;
	public abstract long getTargetNumber();
	public abstract @Nonnull String getSummonString();
	public @Override final void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		final Group g=e.getGroup();
		final Member s=e.getSender();
		if(!(s instanceof NormalMember))return;
		if(!cfg.checkGroup(g.getId()))return;
		final long cur=System.currentTimeMillis();
		if(cur-last>10*60*1000)times=0;
		last=cur;
		if(times++>=2){
			iFeelNoisy(s,5*60);
			return;
		}
		g.sendMessage(
			new MessageChainBuilder()
				.append(new QuoteReply(e.getSource()))
				.append(new At(getTargetNumber()))
				.append(' ')
				.append(getSummonString())
				.build()
		);
	}
	public static final class SophonCommand extends SummonCommand{
		public @Nonnull @Override String getName(){return "sophon";}
		public @Override long getTargetNumber(){return 1013554829;}
		public @Nonnull @Override String getSummonString(){return "SOPHON GOD";}
		public @Override boolean isVisible(){return false;}
	}
	public static final class BigfootCommand extends SummonCommand{
		public @Nonnull @Override String getName(){return "bigfoot";}
		public @Override long getTargetNumber(){return 859220819;}
		public @Nonnull @Override String getSummonString(){return "Hey SB";}
		public @Override boolean isVisible(){return false;}
	}
	public static final class SunflowerCommand extends SummonCommand{
		public @Nonnull @Override String getName(){return "sunflower";}
		public @Override long getTargetNumber(){return 2918296917;}
		public @Nonnull @Override String getSummonString(){return "GOD Sunflower2333";}
		public @Override boolean isVisible(){return false;}
	}
	public static final class SummonHelpCommand extends HelpCommand{
		public @Override @Nonnull String getName(){return "summon";}
		public @Override boolean isShow(@Nonnull CommandMain cmd){return cmd instanceof SummonCommand;}
	}

}
