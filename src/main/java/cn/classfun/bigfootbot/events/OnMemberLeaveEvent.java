package cn.classfun.bigfootbot.events;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import java.sql.Statement;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static java.lang.String.format;
public final class OnMemberLeaveEvent implements Consumer<MemberLeaveEvent>{
	@Override
	public void accept(MemberLeaveEvent e){
		try{
			final Statement sm=stor.con.createStatement();
			sm.execute(format(
				"insert into member_leave"+
				"(group_id,member_id,operator_id,time)VALUES"+
				"(%d,%d,0,%d)",
				e.getGroupId(),e.getMember().getId(),
				System.currentTimeMillis()/1000
			));
			sm.closeOnCompletion();
		}catch(Exception x){
			blog.error("error while save leave event",x);
		}
	}
}
