package cn.classfun.bigfootbot.events;
import cn.classfun.encoders.Coder;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupNameChangeEvent;
import java.sql.Statement;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static java.lang.String.format;
public final class OnGroupNameChangeEvent implements Consumer<GroupNameChangeEvent>{
	private final static Coder b64=Coder.forName("base64");
	@Override
	public void accept(GroupNameChangeEvent e){
		try{
			final Statement sm=stor.createStatement();
			final NormalMember o=e.getOperator();
			sm.execute(format(
				"insert into group_rename"+
				"(group_id,operator_id,time,origin,new)VALUES"+
				"(%d,%d,%d,'%s','%s')",
				e.getGroupId(),
				o==null?0:o.getId(),
				System.currentTimeMillis()/1000,
				b64.encodeString(e.getOrigin()),
				b64.encodeString(e.getNew())
			));
			sm.closeOnCompletion();
		}catch(Exception x){
			blog.error("error while save group rename",x);
		}
	}
}
