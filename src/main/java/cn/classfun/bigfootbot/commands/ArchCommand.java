package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.core.ArchPackage;
import cn.classfun.bigfootbot.core.Utils;
import cn.classfun.bigfootbot.data.CommandMain;
import cn.classfun.bigfootbot.data.URLQuery;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nonnull;
import java.util.List;
import static cn.classfun.bigfootbot.BigfootBot.sdf;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
public final class ArchCommand extends HelpCommand{
	public @Override @Nonnull String getName(){return "arch";}
	public @Override boolean isShow(@Nonnull CommandMain cmd){
		final String name=cmd.getName();
		return name.startsWith("arch")&&!name.equals(getName());
	}
	public static final class ArchPackageCommand extends CommandMain{
		public@Override boolean isVisible(){return false;}
		public@Override @Nonnull String getName(){return "archpkg";}
		public@Override void run(@NotNull GroupMessageEvent e,@NotNull String[]args){
			if(args.length<2||args.length>3){
				randomStringReplyShortSend("ARCHPKG_INVALID",e);
				return;
			}
			final String name=args[1];
			final String repo=args.length>2?args[2]:null;
			try{
				final ArchPackage pkg=ArchPackage.query(name,repo);
				if(pkg==null){
					randomStringReplyShortSend("ARCHPKG_NONE",e);
					return;
				}
				e.getGroup().sendMessage(Utils.buildReplyMsgShort(e).
					append("\n仓库：").append(pkg.getRepository()).
					append("\n包名：").append(pkg.getPackageName()).
					append("\n版本：").append(pkg.getVersion()).
					append("\n描述：").append(pkg.getPackageDescription()).
					append("\n维护者：").append(Utils.array2string(pkg.getMaintainers()," ")).
					append("\n更新日期：").append(sdf.format(pkg.getLastUpdate())).
					append("\n上游：").append(pkg.getURL().toString()).
					append("\nURL：").append(pkg.getWebURL().toString()).
					build()
				);
			}catch(Exception er){
				randomStringReplyShortSend("ERROR",e);
				er.printStackTrace();
			}
		}
	}
	public static final class ArchSearchCommand extends CommandMain{
		public@Override boolean isVisible(){return false;}
		public@Override @Nonnull String getName(){return "archsearch";}
		public@Override void run(@NotNull GroupMessageEvent e,@NotNull String[] args){
			if(args.length<2||args.length>3){
				randomStringReplyShortSend("ARCHPKG_INVALID",e);
				return;
			}
			final String name=args[1];
			final String repo=args.length>2?args[2]:null;
			try{
				final List<ArchPackage>pkgs=ArchPackage.search(name,repo);
				if(pkgs.size()<=0){
					randomStringReplyShortSend("ARCHPKG_NONE",e);
					return;
				}
				final MessageChainBuilder mcb=Utils.buildReplyMsgShort(e);
				if(pkgs.size()>10)mcb.append("\n有超过十个包，只显示十个");
				int c=0;
				for(final ArchPackage pkg:pkgs){
					if(c++>=10)break;
					mcb.append('\n')
						.append(pkg.getRepository())
						.append(' ')
						.append(pkg.getPackageName())
						.append(' ')
						.append(pkg.getVersion());
				}
				final URLQuery q=new URLQuery();
				q.add("q",name);
				if(repo!=null)q.add("repo",ArchPackage.repoMapping(repo));
				e.getGroup().sendMessage(mcb
					.append("\nhttps://archlinux.org/packages/?")
					.append(q.toQueryString())
					.build()
				);
			}catch(Exception er){
				randomStringReplyShortSend("ERROR",e);
				er.printStackTrace();
			}
		}
	}
}
