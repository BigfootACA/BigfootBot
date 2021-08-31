package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.data.CommandMain;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.json.JSONArray;
import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
import static java.lang.String.format;
public final class Command{
	public static final List<CommandMain>commands;
	static{
		commands=new ArrayList<>();
	}
	public static void initCommands(){
		try{
			final InputStream in=Command.class.getClassLoader().getResourceAsStream("commands.json");
			if(in==null)throw new NullPointerException("cannot read commands.json");
			for(Object o:new JSONArray(new String(in.readAllBytes())))addCommand((String)o);
			in.close();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public static void addCommand(@Nonnull String ins)throws ClassNotFoundException{
		addCommand((Class<? extends CommandMain>)Class.forName(ins));
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
