package cn.classfun.bigfootbot.commands;
import cn.classfun.bigfootbot.data.CommandMain;
import cn.classfun.ipcalculator.exceptions.IPInvalidAddressException;
import cn.classfun.ipcalculator.exceptions.IPInvalidNetworkException;
import cn.classfun.ipcalculator.frame.IPAddress;
import cn.classfun.ipcalculator.ipv4.IPv4Address;
import cn.classfun.ipcalculator.ipv4.IPv4Network;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import javax.annotation.Nonnull;
import static cn.classfun.bigfootbot.data.IPCalcChinese.*;
import static cn.classfun.bigfootbot.core.Utils.buildReplyMsgShort;
import static cn.classfun.bigfootbot.config.Messager.randomStringReplyShortSend;
import static java.lang.String.format;
public abstract class IPCalcCommand extends CommandMain{
	public abstract @Nonnull
	String address2string(IPAddress addr);
	private void parseNetwork(GroupMessageEvent e,String net){
		final IPv4Network n=new IPv4Network();
		n.parse(net);
		final IPv4Address a=n.getInternalAddress();
		final IPv4Address m=n.getInternalNetmask();
		if(!a.checkValid())throw new IPInvalidAddressException();
		if(!a.checkValid())throw new IPInvalidNetworkException();
		n.setNetwork(a.toNetworkAddress(m));
		final int p=m.toPrefix();
		e.getGroup().sendMessage(buildReplyMsgShort(e)
			.append(format("\n地址：%s/%d",address2string(a),p))
			.append(format("\n掩码：%s (%d)",address2string(m),p))
			.append(format("\n通配符：%s",address2string(m.toWildcard())))
			.append(format("\n网络地址：%s/%d",address2string(n.getNetworkAddress()),p))
			.append(format("\n广播地址：%s",address2string(n.getBroadcastAddress())))
			.append(format("\n第一个可用地址：%s",address2string(n.getMinAddress())))
			.append(format("\n最后一个可用地址：%s",address2string(n.getMaxAddress())))
			.append(format("\n可用地址总数：%s",n.getAddressCount()))
			.append(format("\n类型：%s",ipv4Type2String(a.getAddressType(n))))
			.append(format("\n类别：%s",ipv4Class2String(a.getAddressClass())))
			.append(format("\n分配：%s",ipv4Group2String(a.getAddressGroup())))
			.build()
		);
	}
	private void parseAddress(GroupMessageEvent e,String net){
		final IPv4Address n=new IPv4Address(net);
		if(!n.checkValid())throw new IPInvalidAddressException();
		e.getGroup().sendMessage(buildReplyMsgShort(e)
			.append(format("\n地址：%s",address2string(n)))
			.append(format("\n类别：%s",ipv4Class2String(n.getAddressClass())))
			.append(format("\n分配：%s",ipv4Group2String(n.getAddressGroup())))
			.build()
		);
	}
	public @Override final void run(@Nonnull GroupMessageEvent e,@Nonnull String[] args){
		new Thread(()->{

		if(args.length<2||args.length>3){
			randomStringReplyShortSend("INVALID_IPCALC",e);
			return;
		}
		try{
			if(args.length==3){
				if(args[1].contains("/")||args[2].contains("/")){
					randomStringReplyShortSend("INVALID_ADDRESS",e);
					return;
				}
				parseNetwork(e,args[1]+"/"+args[2]);
			}else if(args[1].contains("/"))parseNetwork(e,args[1]);
			else parseAddress(e,args[1]);
		}catch(Exception x){
			e.getGroup().sendMessage(
				buildReplyMsgShort(e)
				.append(exception2String(x))
				.build()
			);
		}
		}).start();
	}
	public final static class IPCalcDecCommand extends IPCalcCommand{
		public @Nonnull @Override String address2string(IPAddress addr){return addr.toDecString();}
		public @Nonnull @Override String getName(){return "ipcalcdec";}
		public @Override boolean isVisible(){return false;}
	}
	public final static class IPCalcBinCommand extends IPCalcCommand{
		public @Nonnull @Override String address2string(IPAddress addr){return addr.toBinString();}
		public @Nonnull @Override String getName(){return "ipcalcbin";}
		public @Override boolean isVisible(){return false;}
	}
	public final static class IPCalcHexCommand extends IPCalcCommand{
		public @Nonnull @Override String address2string(IPAddress addr){return addr.toHexString();}
		public @Nonnull @Override String getName(){return "ipcalchex";}
		public @Override boolean isVisible(){return false;}
	}
	public final static class IPCalcHelpCommand extends HelpCommand{
		public @Override @Nonnull String getName(){return "ipcalc";}
		public @Override boolean isShow(@Nonnull CommandMain cmd){return cmd instanceof IPCalcCommand;}
	}
}
