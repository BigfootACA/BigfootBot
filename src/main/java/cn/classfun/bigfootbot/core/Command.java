package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.commands.*;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
import static java.lang.String.format;
public final class Command{
	public static final List<CommandMain>commands;
	static{
		commands=new ArrayList<>();
		addCommand(ReloadCommand.class);
		addCommand(FuckCommand.class);
		addCommand(AskCommand.class);
		addCommand(QuestionCommand.class);
		addCommand(HelpCommand.DefaultHelpCommand.class);
		addCommand(SummonCommand.SophonCommand.class);
		addCommand(SummonCommand.BigfootCommand.class);
		addCommand(TopCommand.TopAllCommand.class);
		addCommand(TopCommand.TopDayCommand.class);
		addCommand(TopCommand.TopHourCommand.class);
		addCommand(TopCommand.TopWeekCommand.class);
		addCommand(TopCommand.TopMonthCommand.class);
		addCommand(TopCommand.TopHelpCommand.class);
		addCommand(MuteCommand.MuteMeCommand.class);
		addCommand(MuteCommand.RmCommand.class);
		addCommand(MuteCommand.ExitCommand.class);
		addCommand(MuteCommand.QuitCommand.class);
		addCommand(MuteCommand.ResetCommand.class);
		addCommand(MuteCommand.PowerOffCommand.class);
		addCommand(MuteCommand.ShutdownCommand.class);
		addCommand(MuteCommand.RebootCommand.class);
		addCommand(SimpleSayCommand.DateCommand.class);
		addCommand(SimpleSayCommand.ShellCommand.class);
		addCommand(SimpleSayCommand.ShCommand.class);
		addCommand(SimpleSayCommand.BashCommand.class);
		addCommand(SimpleSayCommand.FishCommand.class);
		addCommand(SimpleSayCommand.ZshCommand.class);
		addCommand(SimpleSayCommand.WhoamiCommand.class);
		addCommand(SimpleSayCommand.IdCommand.class);
		addCommand(SimpleSayCommand.TimeCommand.class);
		addCommand(SimpleSayCommand.PingCommand.class);
		addCommand(SimpleSayCommand.VersionCommand.class);
		addCommand(IPCalcCommand.IPCalcDecCommand.class);
		addCommand(IPCalcCommand.IPCalcBinCommand.class);
		addCommand(IPCalcCommand.IPCalcHexCommand.class);
		addCommand(IPCalcCommand.IPCalcHelpCommand.class);
		addCommand(TranslateCommand.TransENCommand.class);
		addCommand(TranslateCommand.TransZHCommand.class);
		addCommand(TranslateCommand.TransJPCommand.class);
		addCommand(TranslateCommand.TransHelpCommand.class);
		addCommand(MutedCommand.class);
	}
	public static void addCommand(@Nonnull Class<? extends CommandMain>ins){
		try{
			final CommandMain main=ins.getDeclaredConstructor().newInstance();
			final String name=main.getName();
			for(CommandMain cmd:commands)if(name.equalsIgnoreCase(cmd.getName()))return;
			commands.add(main);
		}catch(Exception e){
			System.err.println("add command failed");
			e.printStackTrace();
		}
	}
	public static void invokeCommand(@Nonnull GroupMessageEvent e,@Nonnull String arg){
		final String[]args=arg.split(" ");
		if(args.length<1)return;
		final String name=args[0].trim();
		final MemberPermission perm=e.getSender().getPermission();
		for(CommandMain cmd:commands){
			if(!name.equalsIgnoreCase(cmd.getName()))continue;
			if(perm.getLevel()<cmd.getPermission().getLevel()){
				randomStringReplyShortSend("NO_PERM",e);
				return;
			}
			cmd.run(e,args);
			return;
		}
		blog.info(format("command %s unknown",name));
	}
}
