package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.core.Utils;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import static cn.classfun.utils.StringUtils.passedTimeToChinese;
import static java.lang.String.format;
public final class MutedCommand extends CommandMain{
	public @Override @Nonnull String getName(){return "muted";}
	public @Override void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		final Group g=e.getGroup();
		final List<NormalMember>ms=new ArrayList<>();
		for(NormalMember m:g.getMembers())if(m.isMuted())ms.add(m);
		final MessageChainBuilder b=Utils.buildReplyMsgShort(e);
		if(ms.size()>20)b.append("\n有超过二十人被禁言，只显示二十人");
		int c=0;
		for(NormalMember m:ms){
			if(c++>=20)break;
			b.append(format(
				"\n%s (%d) %s",
				Utils.getName(m),m.getId(),
				passedTimeToChinese(m.getMuteTimeRemaining())
			));
		}
		g.sendMessage(b.build());
	}
}
