package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.core.Utils;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
import static java.lang.String.format;
public abstract class LspTopCommand extends CommandMain{
	public abstract int getTopCount();
	public abstract int getSince();
	public abstract @Nonnull String getPrefix();
	public @Override final void run(@Nonnull GroupMessageEvent event,@Nonnull String[] args){
		try{
			final MessageChainBuilder m=new MessageChainBuilder().append(getPrefix());
			final Group g=event.getGroup();
			final Statement s=stor.con.createStatement();
			final ResultSet messages=s.executeQuery(format(
				"select count(id) as counts "+
				"from image_violation "+
				"where group_id=%d and time>%d",
				g.getId(),
				getSince()
			));
			final long msg_counts=messages.getLong("counts");
			m.append(format("\n数据库中总共保存了%d条涩图记录",msg_counts));
			messages.close();
			if(msg_counts<=0)return;
			final ResultSet members=s.executeQuery(format(
				"select count(distinct(member_id)) as counts "+
				"from image_violation "+ // type: 2=PORN, 9=SEXY
				"where group_id=%d and time>%d and ( type=2 or type=9 )",
				g.getId(),
				getSince()
			));
			final long member_counts=members.getLong("counts");
			m.append(format("\n共有%d人发涩图",member_counts));
			members.close();
			if(member_counts<=0)return;
			final ResultSet top=s.executeQuery(format(
				"select count(member_id) as counts,member_id "+
				"from image_violation "+
				"where group_id=%d and time>%d "+
				"group by member_id "+
				"order by counts "+
				"desc limit %d ",
				g.getId(),
				getSince(),
				getTopCount()
			));
			int p=0;
			while(top.next()){
				final long sender=top.getLong("member_id");
				final long count=top.getLong("counts");
				if(count<1||sender<10000)continue;
				p++;
				String name=String.valueOf(sender);
				try{name=Utils.getName(g.getOrFail(sender));}catch(Exception ignore){}
				m.append(format(
					"\n第%d名 %d张 %s (%d)",
					p,count,name,sender
				));
			}
			top.close();
			g.sendMessage(m.build());
		}catch(SQLException t){
			blog.error("error while query database",t);
			randomStringReplyShortSend("ERROR",event);
		}
	}
	public @Override boolean isVisible(){return false;}
	public final static class LspTopAllCommand extends LspTopCommand{
		public @Override int getTopCount(){return 5;}
		public @Override int getSince(){return 0;}
		public @Override @Nonnull String getPrefix(){return "有史以来";}
		public @Override @Nonnull String getName(){return "lsptopall";}
	}
	public abstract static class LspTopDurationCommand extends LspTopCommand{
		public abstract int getDuration();
		public @Override int getTopCount(){return 3;}
		public @Override final int getSince(){return (int)((System.currentTimeMillis()/1000)-getDuration());}
		public @Override @Nonnull String getPrefix(){return "在过去的一天里";}
		public @Override @Nonnull String getName(){return "lsptopday";}
	}
	public final static class LspTopDayCommand extends LspTopDurationCommand{
		public @Override int getTopCount(){return 3;}
		public @Override int getDuration(){return 24*60*60;}
		public @Override @Nonnull String getPrefix(){return "在过去的一天里";}
		public @Override @Nonnull String getName(){return "lsptopday";}
	}
	public final static class LspTopHourCommand extends LspTopDurationCommand{
		public @Override int getTopCount(){return 3;}
		public @Override int getDuration(){return 60*60;}
		public @Override @Nonnull String getPrefix(){return "在过去的一个小时里";}
		public @Override @Nonnull String getName(){return "lsptophour";}
	}
	public final static class LspTopWeekCommand extends LspTopDurationCommand{
		public @Override int getTopCount(){return 3;}
		public @Override int getDuration(){return 7*24*60*60;}
		public @Override @Nonnull String getPrefix(){return "在过去的一周里";}
		public @Override @Nonnull String getName(){return "lsptopweek";}
	}
	public final static class LspTopMonthCommand extends LspTopDurationCommand{
		public @Override int getTopCount(){return 4;}
		public @Override int getDuration(){return 30*24*60*60;}
		public @Override @Nonnull String getPrefix(){return "在过去的一个月里";}
		public @Override @Nonnull String getName(){return "lsptopmonth";}
	}
	public final static class LspTopHelpCommand extends HelpCommand{
		public @Override @Nonnull String getName(){return "lsptop";}
		public @Override boolean isShow(@Nonnull CommandMain cmd){return cmd instanceof LspTopCommand;}
	}
}
