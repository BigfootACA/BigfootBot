package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.lang.String.format;
public abstract class SimpleSayCommand extends CommandMain{
	public abstract @Nonnull String sayWhat();
	public @Override final void run(@Nonnull GroupMessageEvent event,@Nonnull String[] args){
		event.getGroup().sendMessage(sayWhat());
	}
	public @Override boolean isVisible(){return false;}
	public final static class PingCommand extends SimpleSayCommand{
		public @Override boolean isVisible(){return true;}
		public @Override @Nonnull String sayWhat(){return "Pong!";}
		public @Override @Nonnull String getName(){return "ping";}
	}
	public static class DateCommand extends SimpleSayCommand{
		private final SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public @Override @Nonnull final String sayWhat(){return s.format(new Date());}
		public @Override @Nonnull String getName(){return "date";}
	}
	public final static class WhoamiCommand extends SimpleSayCommand{
		public @Override @Nonnull String sayWhat(){return "root";}
		public @Override @Nonnull String getName(){return "whoami";}
	}
	public final static class IdCommand extends SimpleSayCommand{
		public @Override @Nonnull String sayWhat(){return "uid=0(root) gid=0(root) groups=0(root)";}
		public @Override @Nonnull String getName(){return "id";}
	}
	public static class ShellCommand extends SimpleSayCommand{
		public @Override @Nonnull final String sayWhat(){return getName()+"-5.1# ";}
		public @Override @Nonnull String getName(){return "shell";}
	}
	public final static class TimeCommand extends DateCommand{public @Override @Nonnull String getName(){return "time";}}
	public final static class BashCommand extends ShellCommand{public @Override @Nonnull String getName(){return "bash";}}
	public final static class FishCommand extends ShellCommand{public @Override @Nonnull String getName(){return "fish";}}
	public final static class ZshCommand extends ShellCommand{public @Override @Nonnull String getName(){return "zsh";}}
	public final static class ShCommand extends ShellCommand{public @Override @Nonnull String getName(){return "sh";}}
	public final static class VersionCommand extends SimpleSayCommand{
		public @Override @Nonnull String sayWhat(){
			final Package pkg=VersionCommand.class.getPackage();
			return format(
				"版本号: %s\n"+
				"编译者: %s\n"+
				"源代码: https://github.com/BigfootACA/BigfootBot\n"+
				"协议  : GPLv3",
				pkg.getImplementationVersion(),
				pkg.getImplementationVendor()
			);
		}
		public @Override @Nonnull String getName(){return "version";}
	}
}
