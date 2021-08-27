package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.data.ImageAction;
import cn.classfun.bigfootbot.data.ImageType;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static cn.classfun.bigfootbot.config.Config.cfg;
import static cn.classfun.bigfootbot.core.Utils.canOperate;
import static cn.classfun.bigfootbot.core.Utils.iamAdmin;
import static java.util.Objects.requireNonNull;
public final class MessagePolicy{
	private final List<ImageAction>actions=new ArrayList<>();
	private final List<ImageType>types=new ArrayList<>();
	private String msg_id;
	private int mute=0;
	private boolean atowner=false,warn=false,recall=false;
	public boolean isRecall(){return recall;}
	public String getMsgID(){return msg_id;}
	public String getMsg(){return cfg.getStringsRandom(getMsgID());}
	public long getMute(){return mute;}
	public boolean isWarn(){return warn;}
	public boolean isAtOwner(){return atowner;}
	public void mute(@Nonnull Member m){
		if(mute!=0&&canOperate(m))m.mute(mute);
	}
	public void atOwner(@Nonnull Group g){
		if(!atowner||!iamAdmin(g))return;
		g.sendMessage(new At(g.getOwner().getId()));
	}
	public void send(@Nonnull GroupMessageEvent e,@Nonnull ImageType type){
		final Group g=e.getGroup();
		MessageChainBuilder b=new MessageChainBuilder();
		if(warn)b.append(new At(e.getSender().getId())).append(" ");
		if(msg_id!=null)b.append(stringReplace(e,getMsg(),type));
		if(b.size()>0)g.sendMessage(b.build());
		atOwner(g);
	}
	public void doAction(@Nonnull GroupMessageEvent e,@Nonnull ImageType type){
		if(recall)MessageSource.recall(e.getMessage());
		mute(e.getSender());
		send(e,type);
	}
	public static String stringReplace(
		@Nonnull GroupMessageEvent e,
		@Nonnull String str,
		@Nonnull ImageType type
	){
		final Member m=e.getSender();
		final Group g=e.getGroup();
		return str
			.replaceAll("%NAME%",Utils.getName(m))
			.replaceAll("%ID%",String.valueOf(m.getId()))
			.replaceAll("%GNAME%",g.getName())
			.replaceAll("%GID",String.valueOf(g.getId()))
			.replaceAll("%TYPE%",type.name());
	}
	public List<ImageType>getTypes(){return requireNonNull(types);}
	public List<ImageAction>getActions(){return requireNonNull(actions);}
	public ImageType getType(int i){return getTypes().get(i);}
	public ImageAction getAction(int i){return getActions().get(i);}
	public void addType(@Nonnull ImageType a){getTypes().add(requireNonNull(a));}
	public void addAction(@Nonnull ImageAction a){getActions().add(requireNonNull(a));}
	public void clearType(){getTypes().clear();}
	public void clearAction(){getActions().clear();}
	public int countType(){return getTypes().size();}
	public int countAction(){return getActions().size();}
	public static MessagePolicy addPolicy(@Nonnull JSONObject jo){
		final MessagePolicy mp=new MessagePolicy();
		if(jo.has("action"))
			for(Object o:jo.getJSONArray("action"))
				mp.actions.add(ImageAction.valueOf(((String)o).toUpperCase()));
		if(jo.has("type"))
			for(Object o:jo.getJSONArray("type"))
				mp.types.add(ImageType.valueOf(((String)o).toUpperCase()));
		if(jo.has("atowner"))mp.atowner=jo.getBoolean("atowner");
		if(jo.has("recall"))mp.recall=jo.getBoolean("recall");
		if(jo.has("warn"))mp.warn=jo.getBoolean("warn");
		if(jo.has("mute"))mp.mute=jo.getInt("mute");
		if(jo.has("msg"))mp.msg_id=jo.getString("msg");
		return mp;
	}
}
