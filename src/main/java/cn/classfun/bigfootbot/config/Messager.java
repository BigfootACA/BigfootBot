package cn.classfun.bigfootbot.config;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static cn.classfun.bigfootbot.core.Utils.*;
import static cn.classfun.bigfootbot.config.Config.cfg;
@SuppressWarnings("unused")
public final class Messager{
	public static @Nullable
	MessageChain randomStringBuild(
		@Nonnull String id,
		@Nonnull MessageChainBuilder builder
	){
		final String str=cfg.getStringsRandom(id);
		return str==null?null:builder.append(str).build();
	}
	private static void xSend(@Nonnull GroupMessageEvent e,@Nullable MessageChain m){
		xSend(e.getGroup(),m);
	}
	private static void xSend(@Nonnull Group g,@Nullable MessageChain m){
		if(m==null||g.getBotAsMember().isMuted())return;
		g.sendMessage(m);
	}
	public static void randomStringBuildSend(
		@Nonnull String id,
		@Nonnull MessageChainBuilder builder,
		GroupMessageEvent event
	){xSend(event,randomStringBuild(id,builder));}
	public static @Nullable MessageChain randomStringReply(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e,
		char sep
	){return randomStringBuild(id,buildReplyMsg(e,sep));}
	public static @Nullable MessageChain randomStringReplyLong(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e
	){return randomStringReply(id,e,'\n');}
	public static @Nullable MessageChain randomStringReplyShort(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e
	){return randomStringReply(id,e,' ');}
	public static void randomStringReplySend(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e,
		char sep
	){xSend(e,randomStringReply(id,e,sep));}
	public static void randomStringReplyLongSend(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e
	){randomStringReplySend(id,e,'\n');}
	public static void randomStringReplyShortSend(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e
	){randomStringReplySend(id,e,' ');}

	public static @Nullable MessageChain randomStringAt(
		@Nonnull String id,
		@Nonnull Member m,
		char sep
	){return randomStringBuild(id,buildAtMsg(m,sep));}
	public static @Nullable MessageChain randomStringAtLong(
		@Nonnull String id,
		@Nonnull Member m
	){return randomStringAt(id,m,'\n');}
	public static @Nullable MessageChain randomStringAtShort(
		@Nonnull String id,
		@Nonnull Member m
	){return randomStringAt(id,m,' ');}
	public static void randomStringAtSend(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e,
		char sep
	){xSend(e,randomStringAt(id,e.getSender(),sep));}
	public static void randomStringAtLongSend(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e
	){randomStringAtSend(id,e,'\n');}
	public static void randomStringAtShortSend(
		@Nonnull String id,
		@Nonnull GroupMessageEvent e
	){randomStringAtSend(id,e,' ');}
	public static void randomStringAtWhoSend(
		@Nonnull String id,
		@Nonnull Group e,
		@Nonnull Member m,
		char sep
	){xSend(e,randomStringAt(id,m,sep));}
	public static void randomStringAtWhoLongSend(
		@Nonnull String id,
		@Nonnull Group e,
		@Nonnull Member m
	){randomStringAtWhoSend(id,e,m,'\n');}
	public static void randomStringAtWhoShortSend(
		@Nonnull String id,
		@Nonnull Group e,
		@Nonnull Member m
	){randomStringAtWhoSend(id,e,m,' ');}
	private Messager(){throw new RuntimeException();}
}
