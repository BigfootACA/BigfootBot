package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import cn.classfun.bigfootbot.data.Question;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static cn.classfun.bigfootbot.core.Utils.buildReplyMsgLong;
public final class QuestionCommand extends CommandMain{
	public @Override @Nonnull String getName(){return "question";}
	public @Override void run(@Nonnull GroupMessageEvent e,@Nonnull String[]args){
		MessageChainBuilder b=buildReplyMsgLong(e).append(cfg.getStringsRandom("ASK_GUESS"));
		int i=0;
		for(Question c:cfg.getQuestions()){
			final String desc=c.getDescription();
			if(desc==null||desc.trim().length()<=0)continue;
			i++;
			b.append('\n').append(desc);
		}
		if(i>0)e.getGroup().sendMessage(b.build());
	}
}
