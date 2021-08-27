package cn.classfun.bigfootbot.commands;
import javax.annotation.Nonnull;
import cn.classfun.bigfootbot.core.Translate;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
import static cn.classfun.bigfootbot.core.Utils.buildReplyMsgLong;
public abstract class TranslateCommand extends CommandMain{
	public @Nonnull String getSourceLanguage(){return "auto";}
	public abstract @Nonnull String getTargetLanguage();
	public @Override final void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		final Group g=e.getGroup();
		if(args.length<=1){
			randomStringReplyShortSend("TRANS_MISS",e);
			return;
		}
		final StringBuilder sb=new StringBuilder();
		for(int i=1;i<args.length;i++)sb
			.append(args[i].trim())
			.append(' ');
		final String res=Translate.translate(
			getSourceLanguage(),
			getTargetLanguage(),
			sb.toString().trim()
		);
		g.sendMessage(buildReplyMsgLong(e).append(res).build());
	}
	public @Override boolean isVisible(){return false;}
	public final static class TransZHCommand extends TranslateCommand{
		public @Nonnull @Override String getTargetLanguage(){return "zh";}
		public @Nonnull @Override String getName(){return "transzh";}
	}
	public final static class TransENCommand extends TranslateCommand{
		public @Nonnull @Override String getTargetLanguage(){return "en";}
		public @Nonnull @Override String getName(){return "transen";}
	}
	public final static class TransJPCommand extends TranslateCommand{
		public @Nonnull @Override String getTargetLanguage(){return "ja";}
		public @Nonnull @Override String getName(){return "transjp";}
	}
	public final static class TransHelpCommand extends HelpCommand{
		public @Override @Nonnull String getName(){return "trans";}
		public @Override boolean isShow(@Nonnull CommandMain cmd){return cmd instanceof TranslateCommand;}
	}
}
