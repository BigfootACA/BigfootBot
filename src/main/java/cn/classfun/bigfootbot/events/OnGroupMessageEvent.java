package cn.classfun.bigfootbot.events;
import cn.classfun.bigfootbot.core.ImageAudit;
import cn.classfun.bigfootbot.core.Utils;
import cn.classfun.encoders.Base64;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import java.sql.Statement;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static cn.classfun.bigfootbot.core.Command.invokeCommand;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static cn.classfun.bigfootbot.core.UserChangeName.checkMemberName;
import static cn.classfun.bigfootbot.core.UserChangeName.conflictName;
import static cn.classfun.bigfootbot.data.Question.ask;
import static java.lang.String.format;
public final class OnGroupMessageEvent implements Consumer<GroupMessageEvent>{
	private final static Base64 b64=new Base64();
	@Override
	public void accept(GroupMessageEvent e){
		final Group grp=e.getGroup();
		final Bot bot=e.getBot();
		final String msg=e.getMessage().contentToString().trim();
		final Member send=e.getSender();
		try{
			final Statement sm=stor.createStatement();
			final MessageChain mc=e.getMessage();
			sm.execute(format(
				"insert into group_msg"+
				"(group_id,sender_id,time,message)VALUES"+
				"(%d,%d,%d,'%s')",
				grp.getId(),
				send.getId(),
				e.getTime(),
				b64.encodeString(mc.toString())
			));
			sm.closeOnCompletion();
			new ImageAudit(e).start();
		}catch(Exception x){
			blog.error("error while save message",x);
		}
		if(!cfg.checkGroup(grp.getId()))return;
		if(grp.getBotAsMember().isMuted())return;
		String myname=Utils.getName(grp.getBotAsMember());
		String atmename="@"+myname,atmeid="@"+bot.getId();
		if(send.getId()==bot.getId())return;
		if(send instanceof NormalMember){
			final NormalMember s=(NormalMember)send;
			if(conflictName(grp,s))return;
			if(checkMemberName(grp,s))return;
		}
		if(msg.startsWith(atmename))ask(e,msg.substring(atmename.length()));
		else if(msg.startsWith(atmeid))ask(e,msg.substring(atmeid.length()));
		else if(msg.startsWith("/"))invokeCommand(e,msg.substring(1));
	}
}
