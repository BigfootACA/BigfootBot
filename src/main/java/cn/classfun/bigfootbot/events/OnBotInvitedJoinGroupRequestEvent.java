package cn.classfun.bigfootbot.events;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.config.Config.cfg;
public final class OnBotInvitedJoinGroupRequestEvent implements Consumer<BotInvitedJoinGroupRequestEvent>{
	@Override
	public void accept(BotInvitedJoinGroupRequestEvent e){
		if(cfg.checkGroup(e.getGroupId()))e.accept();
	}
}
