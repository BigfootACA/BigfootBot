package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.core.Utils;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
public abstract class MuteCommand extends CommandMain{
	public int getMaxTime(){return 30*24*60*60;}
	public int getDefaultTime(){return 60*10;}
	public @Override final void run(@Nonnull GroupMessageEvent event,@Nonnull String[] args){
		int time=getDefaultTime();
		if(args.length==2)try{time=min(getMaxTime(),max(1,parseInt(args[1])));}catch(Exception ignore){}
		Utils.iFeelNoisy(event.getSender(),time);
	}
	public abstract static class HideMuteCommand extends MuteCommand{public @Override boolean isVisible(){return false;}}
	public final static class MuteMeCommand      extends MuteCommand{public @Override @Nonnull String getName(){return "muteme";}}
	public final static class RmCommand          extends HideMuteCommand{public @Override @Nonnull String getName(){return "rm";}}
	public final static class ExitCommand        extends HideMuteCommand{public @Override @Nonnull String getName(){return "exit";}}
	public final static class QuitCommand        extends HideMuteCommand{public @Override @Nonnull String getName(){return "quit";}}
	public final static class ResetCommand       extends HideMuteCommand{public @Override @Nonnull String getName(){return "reset";}}
	public final static class RebootCommand      extends HideMuteCommand{public @Override @Nonnull String getName(){return "reboot";}}
	public final static class PowerOffCommand    extends HideMuteCommand{public @Override @Nonnull String getName(){return "poweroff";}}
	public final static class ShutdownCommand    extends HideMuteCommand{public @Override @Nonnull String getName(){return "shutdown";}}
}
