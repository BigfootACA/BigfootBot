package cn.classfun.bigfootbot.data;
import cn.classfun.bigfootbot.config.Config;
import cn.classfun.bigfootbot.core.Utils;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Utils.buildReplyMsgShort;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static cn.classfun.bigfootbot.config.Messager.randomStringBuild;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.String.format;
@SuppressWarnings("unused")
public final class Question{
	private String desc=null;
	private final List<Long> scopes=new ArrayList<>();
	private final List<String>answers=new ArrayList<>();
	private final List<String>keywords=new ArrayList<>();
	public @Nonnull List<Long>getScopes(){return scopes;}
	public @Nonnull List<String>getAnswers(){return answers;}
	public @Nonnull List<String>getKeywords(){return keywords;}
	public @Nullable String getDescription(){return desc;}
	public int getScopesCount(){return getScopes().size();}
	public int getAnswersCount(){return getAnswers().size();}
	public int getKeywordsCount(){return getKeywords().size();}
	public @Nullable Long getScope(int i){return getScopes().get(i);}
	public @Nullable String getAnswer(int i){return getAnswers().get(i);}
	public @Nullable String getKeyword(int i){return getKeywords().get(i);}
	public @Nullable Long delScope(int i){return getScopes().remove(i);}
	public @Nullable String delAnswer(int i){return getAnswers().remove(i);}
	public @Nullable String delKeyword(int i){return getKeywords().remove(i);}
	public @Nonnull Question clearScope(){getScopes().clear();return this;}
	public @Nonnull Question clearAnswer(){getAnswers().clear();return this;}
	public @Nonnull Question clearKeyword(){getKeywords().clear();return this;}
	public @Nonnull Question addAllAnswer(@Nonnull List<String>answers){getAnswers().addAll(answers);return this;}
	public @Nonnull Question addAllKeyword(@Nonnull List<String>keywords){getKeywords().addAll(keywords);return this;}
	public @Nonnull Question addAllScope(@Nonnull List<Long>scopes){getScopes().addAll(scopes);return this;}
	public @Nonnull Question setAnswers(@Nonnull List<String>answers){return clearAnswer().addAllAnswer(answers);}
	public @Nonnull Question setKeywords(@Nonnull List<String>keywords){return clearKeyword().addAllKeyword(keywords);}
	public @Nonnull Question setScopes(@Nonnull List<Long>scopes){return clearScope().addAllScope(scopes);}
	public @Nonnull Question setDescription(@Nullable String desc){this.desc=desc;return this;}
	public @Nonnull Question setAnswers(@Nonnull JSONArray ja){
		clearAnswer();
		ja.forEach(x->addAnswer((String)x));
		return this;
	}
	public @Nonnull Question setKeywords(@Nonnull JSONArray ja){
		clearKeyword();
		ja.forEach(x->addKeyword((String)x));
		return this;
	}
	public @Nonnull Question setScopes(@Nonnull JSONArray ja){
		Utils.longArrayParse(getScopes(),ja);
		return this;
	}
	public @Nonnull Question addAnswer(@Nonnull String answer){
		for(String k:getAnswers())if(k.equalsIgnoreCase(answer))return this;
		answers.add(answer);
		return this;
	}
	public @Nonnull Question addKeyword(@Nonnull String keyword){
		for(String k:getKeywords())if(k.equalsIgnoreCase(keyword))return this;
		keywords.add(keyword);
		return this;
	}
	public @Nonnull Question addScope(long group){
		for(long k: getScopes())if(k==group)return this;
		scopes.add(group);
		return this;
	}

	public String getRandomAnswer(){
		final List<String>l=getAnswers();
		final int i=l.size();
		return i<=0?null:l.get((int)round(random()*(i-1)));
	}

	public boolean inScope(long group){
		if(getScopesCount()<=0)return true;
		for(Long g:getScopes())if(group==g)return true;
		return false;
	}

	public boolean match(long group,@Nonnull String str){
		if(!inScope(group))return false;
		return match(str);
	}

	public boolean match(@Nonnull String str){
		final String x=str.toLowerCase(Locale.ROOT);
		for(String k:keywords)if(x.contains(k.toLowerCase(Locale.ROOT)))return true;
		return false;
	}

	public static @Nullable Question matchAll(List<Question>questions,String str){
		for(Question q:questions)if(q.match(str))return q;
		return null;
	}

	public static @Nullable Question matchAll(List<Question>questions,long group,String str){
		for(Question q:questions)if(q.match(group,str))return q;
		return null;
	}

	public static void ask(GroupMessageEvent e,String ostr){
		String str=ostr.trim();
		MessageChain c;
		MessageChainBuilder b=buildReplyMsgShort(e);
		if(str.length()==0)c=null;
		else if(str.length()>64)c=randomStringBuild("LONG",b);
		else{
			blog.info(format("ask %s",str));
			final Question q=cfg.matchQuestion(e.getGroup().getId(),str);
			if(q==null)c=randomStringBuild("ASK_UNKNOWN",b);
			else c=b.append(q.getRandomAnswer()).build();
		}
		if(c==null)c=randomStringBuild("ASK_WHAT",b);
		if(c!=null)e.getSubject().sendMessage(c);
	}
	public static void parseQuestions(@Nonnull Config c,@Nonnull JSONArray ques){
		final List<Question> questions=new ArrayList<>();
		ques.forEach(o->{
			final JSONObject jo=(JSONObject)o;
			final Question q=new Question();
			if(jo.has("answer"))q.setAnswers(jo.getJSONArray("answer"));
			if(jo.has("keyword"))q.setKeywords(jo.getJSONArray("keyword"));
			if(jo.has("scope"))q.setScopes(jo.getJSONArray("scope"));
			if(jo.has("description"))q.setDescription(jo.getString("description"));
			questions.add(q);
		});
		c.setQuestions(questions);
	}
}
