package cn.classfun.bigfootbot.events;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.MemberMuteEvent;
import java.sql.Statement;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static java.lang.String.format;
public final class OnMemberMuteEvent implements Consumer<MemberMuteEvent>{
	@Override
	public void accept(MemberMuteEvent e){
		try{
			final Statement sm=stor.con.createStatement();
			final Member o=e.getOperator();
			sm.execute(format(
				"insert into group_mute"+
				"(group_id,member_id,operator_id,time,remaining,type)VALUES"+
				"(%d,%d,%d,%d,%d,'MEMBER_MUTE')",
				e.getGroupId(),
				e.getMember().getId(),
				o==null?0:o.getId(),
				System.currentTimeMillis()/1000,
				e.getDurationSeconds()
			));
			sm.closeOnCompletion();
		}catch(Exception x){
			blog.error("error while save mute",x);
		}
	}
}
