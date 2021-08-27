package cn.classfun.bigfootbot.events;
import cn.classfun.bigfootbot.core.JoinRequest;
import cn.classfun.encoders.Coder;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import java.sql.Statement;
import java.util.Locale;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static java.lang.String.format;
public final class OnMemberJoinRequestEvent implements Consumer<MemberJoinRequestEvent>{
	private final static Coder b64=Coder.forName("base64");
	@Override
	public void accept(MemberJoinRequestEvent e){
		try{
			final Statement sm=stor.createStatement();
			final NormalMember o=e.getInvitor();
			sm.execute(format(
				"insert into member_join_request"+
				"(group_id,member_id,invitor_id,time,message)VALUES"+
				"(%d,%d,%d,%d,'%s')",
				e.getGroupId(),
				e.getFromId(),
				o==null?0:o.getId(),
				System.currentTimeMillis()/1000,
				b64.encodeString(e.getMessage())
			));
			sm.closeOnCompletion();
		}catch(Exception x){
			blog.error("error while save join request",x);
		}
		JoinRequest.onMemberJoinRequestEvent(e);
	}
}
