package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
public final class ReloadCommand extends CommandMain{
	public @Nonnull @Override String getName(){return "reload";}
	public @Nonnull @Override MemberPermission getPermission(){return MemberPermission.OWNER;}
	public @Override void run(@Nonnull GroupMessageEvent e,@Nonnull String[]args){
		if(args.length!=1)return;
		try{
			cfg.reload();
			blog.info("reload success");
			randomStringReplyShortSend("RELOAD",e);
		}catch(Exception x){
			x.printStackTrace();
			randomStringReplyShortSend("ERROR",e);
		}
	}
}
