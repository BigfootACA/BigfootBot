package cn.classfun.bigfootbot.events;
import cn.classfun.bigfootbot.core.MemberWelcome;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import java.sql.Statement;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static java.lang.String.format;
public final class OnMemberJoinEvent implements Consumer<MemberJoinEvent>{
	@Override
	public void accept(MemberJoinEvent e){
		final Group g=e.getGroup();
		final NormalMember m=e.getMember();
		try{
			final Statement sm=stor.con.createStatement();
			sm.execute(format(
				"insert into member_join"+
				"(group_id,member_id,time)VALUES"+
				"(%d,%d,%d)",
				g.getId(),m.getId(),
				System.currentTimeMillis()/1000
			));
			sm.closeOnCompletion();
		}catch(Exception x){
			blog.error("error while save join event",x);
		}
		blog.info(format("found %s(%d) join",m.getNick(),m.getId()));
		MemberWelcome.onMemberJoinEvent(e);
	}
}
