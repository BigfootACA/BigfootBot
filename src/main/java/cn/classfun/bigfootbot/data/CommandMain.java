package cn.classfun.bigfootbot.data;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static java.lang.String.format;
import static net.mamoe.mirai.contact.MemberPermission.MEMBER;
public abstract class CommandMain{
	public abstract @Nonnull String getName();
	public boolean isVisible(){return true;}
	public @Nullable String getDescriptionID(){
		return format(
			"CMD_%s",
			getName().toUpperCase(Locale.ROOT)
		);
	}
	public @Nullable String getDescription(){
		final String id=getDescriptionID();
		return id==null?null:cfg.getStringsRandom(id);
	}
	public @Nonnull MemberPermission getPermission(){return MEMBER;}
	public abstract void run(
		@Nonnull GroupMessageEvent event,
		@Nonnull String[]args
	);
}