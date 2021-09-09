package cn.classfun.bigfootbot.core;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.json.JSONArray;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static cn.classfun.bigfootbot.config.Messager.randomStringAtWhoShortSend;
import static net.mamoe.mirai.contact.MemberPermission.*;
@SuppressWarnings("unused")
public final class Utils{
	public static List<Long>longArrayParse(@Nonnull JSONArray array){return longArrayParse(new ArrayList<>(),array);}
	public static List<Long>longArrayParse(@Nonnull List<Long>arr,@Nonnull JSONArray array){
		arr.clear();
		array.forEach(x->{
			long n=0;
			if(x instanceof Integer)n=(int)x;
			else if(x instanceof Long)n=(long)x;
			if(n<10000)throw new RuntimeException("Invalid Number");
			arr.add(n);
		});
		return arr;
	}
	public static boolean isAdmin(MemberPermission p){return p==ADMINISTRATOR||p==OWNER;}
	public static boolean iamAdmin(@Nonnull Group grp){return isAdmin(grp.getBotPermission());}
	public static boolean isAdmin(@Nonnull Member m){return isAdmin(m.getPermission());}
	public static boolean canOperate(@Nonnull Member m){return iamAdmin(m.getGroup())&&!isAdmin(m);}
	public static MessageChainBuilder buildAtMsgLong(@Nonnull Member m){return buildAtMsg(m,'\n');}
	public static MessageChainBuilder buildAtMsgShort(@Nonnull Member m){return buildAtMsg(m,' ');}
	public static MessageChainBuilder buildReplyMsgLong(@Nonnull GroupMessageEvent m){return buildReplyMsg(m,'\n');}
	public static MessageChainBuilder buildReplyMsgShort(@Nonnull GroupMessageEvent m){return buildReplyMsg(m,' ');}
	public static MessageChainBuilder buildAtMsg(@Nonnull Member m,char sep){
		return new MessageChainBuilder()
			.append(new At(m.getId()))
			.append(sep);
	}
	public static MessageChainBuilder buildReplyMsg(@Nonnull GroupMessageEvent e,char sep){
		return new MessageChainBuilder()
			.append(new QuoteReply(e.getSource()))
			.append(new At(e.getSender().getId()))
			.append(sep);
	}
	public static void iFeelNoisy(@Nonnull Member m,int time){
		if(canOperate(m))m.mute(time);
		randomStringAtWhoShortSend("NOISY",m.getGroup(),m);
	}
	public static String getName(@Nonnull Member m){
		String name=m.getNameCard();
		if(name.trim().length()<=0)name=m.getNick();
		if(name.trim().length()<=0)name=String.valueOf(m.getId());
		return name;
	}
	public static String byte2hex(final byte[]b){
		final StringBuilder sb=new StringBuilder();
		for(byte v:b){
			String s=Integer.toHexString(v&0xFF);
			if(s.length()==1)sb.append('0');
			sb.append(s);
		}
		return sb.toString();
	}
	public static String array2string(Collection<String>lst,String sep){
		final StringBuilder sb=new StringBuilder();
		lst.forEach(s->sb.append(s).append(sep));
		if(sb.length()>0)sb.setLength(sb.length()-sep.length());
		return sb.toString();
	}
	public static String array2string(String[]lst,String sep){
		return array2string(Arrays.asList(lst),sep);
	}
	private Utils(){throw new RuntimeException();}
}
