package cn.classfun.bigfootbot.events;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMuteAllEvent;
import java.sql.Statement;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static java.lang.String.format;
public final class OnGroupMuteAllEvent implements Consumer<GroupMuteAllEvent>{
	@Override
	public void accept(GroupMuteAllEvent e){
		try{
			final Statement sm=stor.createStatement();
			final Member o=e.getOperator();
			sm.execute(format(
				"insert into group_mute"+
				"(group_id,member_id,operator_id,time,remaining,type)VALUES"+
				"(%d,0,%d,%d,0,'ALL_%sMUTE')",
				e.getGroupId(),
				o==null?0:o.getId(),
				System.currentTimeMillis()/1000,
				e.getNew()?"":"UN"
			));
			sm.closeOnCompletion();
		}catch(Exception x){
			blog.error("error while save group all mute",x);
		}
	}
}
