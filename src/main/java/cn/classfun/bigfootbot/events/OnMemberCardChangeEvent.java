package cn.classfun.bigfootbot.events;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.MemberCardChangeEvent;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.core.Utils.iFeelNoisy;
import static java.lang.String.format;
import static cn.classfun.bigfootbot.config.Config.cfg;
public final class OnMemberCardChangeEvent implements Consumer<MemberCardChangeEvent>{
	private void prcoessMe(MemberCardChangeEvent e,NormalMember x){
		if(e.getNew().equals(""))return;
		e.getGroup().sendMessage(format(
			cfg.getStringsRandom("CHANGE_NAME"),
			e.getNew()
		));
		x.setNameCard("");
	}
	private void processOthers(MemberCardChangeEvent e,NormalMember x){
		final Group g=e.getGroup();
		final String myname=g.getBotAsMember().getNick();
		if(
			!x.getNick().equalsIgnoreCase(myname)&&
			!x.getNameCard().equalsIgnoreCase(myname)
		)return;
		iFeelNoisy(x,30*60);
	}
	@Override
	public void accept(MemberCardChangeEvent e){
		final NormalMember m=e.getMember();
		if(m.getId()==cfg.getQQNumber())prcoessMe(e,m);
		else processOthers(e,m);
	}
}
