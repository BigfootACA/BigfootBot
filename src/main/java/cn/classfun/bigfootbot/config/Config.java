package cn.classfun.bigfootbot.config;
import cn.classfun.bigfootbot.data.Question;
import com.tencentcloudapi.ims.v20201229.ImsClient;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static cn.classfun.bigfootbot.config.ConfigParser.parseConfig;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;
@SuppressWarnings("unused")
public final class Config{
	public static Config cfg=null;
	private String config;
	private long qq_number=0;
	private String qq_password=null;
	private String msg_database=null;
	private String msg_assets=null;
	private List<Long>group=null;
	private List<Question>questions=null;
	private Map<String,List<String>>strings=null;
	private ImsClient ims=null;
	public ImsClient getImsClient(){return ims;}
	public void setImsClient(ImsClient ims){this.ims=ims;}
	public long getQQNumber(){
		if(qq_number<10000)
			throw new RuntimeException("Invalid QQ Number");
		return qq_number;
	}
	public void setQQNumber(long number){
		if(number<10000)
			throw new RuntimeException("Invalid QQ Number");
		qq_number=number;
	}
	public @Nonnull List<Long> getGroup(){
		return requireNonNull(group);
	}
	public void setGroup(@Nonnull List<Long> group){
		this.group=requireNonNull(group);
	}
	public boolean checkGroup(long number){
		for(long l:group)if(l==number)return true;
		return false;
	}
	public String getQQPassword(){
		if(qq_password==null||qq_password.trim().length()<5)
			throw new RuntimeException("Invalid QQ Password");
		return qq_password;
	}
	public void setQQPassword(String password){
		if(password==null||password.trim().length()<5)
			throw new RuntimeException("Invalid QQ Password");
		qq_password=password;
	}
	private Config(){}
	private Config(@Nonnull String cfg)throws IOException{
		if(Config.cfg!=null)throw new RuntimeException();
		config=cfg;
		parseConfig(this,cfg);
	}
	public @Nonnull List<Question>getQuestions(){return requireNonNull(questions);}
	public void setQuestions(@Nonnull List<Question>questions){this.questions=requireNonNull(questions);}
	public @Nonnull Map<String,List<String>>getStrings(){return requireNonNull(strings);}
	public void setStrings(@Nonnull Map<String,List<String>>strings){this.strings=requireNonNull(strings);}
	public void addStringsItem(@Nonnull String id,@Nonnull String item){getStringsList(id).add(item);}
	public int getStringsCount(@Nonnull String id){return getStringsList(id).size();}
	public String getStringsItem(@Nonnull String id,int num){return getStringsList(id).get(num);}
	public String getStringsRandom(@Nonnull String id){
		final List<String>l=getStringsList(id);
		return l.get((int)round(random()*(l.size()-1)));
	}
	public List<String>getStringsList(@Nonnull String id){
		return requireNonNull(
			getStrings()
			.get(requireNonNull(id))
		);
	}
	public void addStringsList(@Nonnull String id,@Nonnull List<String>list){
		getStrings().put(
			requireNonNull(id),
			requireNonNull(list)
		);
	}
	public void clear(){
		qq_number=0;
		qq_password=null;
		strings=null;
		group=null;
		questions=null;
	}
	public void reload()throws IOException{
		clear();
		parseConfig(this,config);
	}
	public @Nullable Question matchQuestion(String str){
		return Question.matchAll(questions,str);
	}
	public @Nullable Question matchQuestion(long group,String str){
		return Question.matchAll(questions,group,str);
	}
	public @Nonnull String getMessageDatabase(){return requireNonNull(msg_database);}
	public @Nonnull String getMessageAssets(){return requireNonNull(msg_assets);}
	public void setMessageDatabase(@Nonnull String database){msg_database=requireNonNull(database);}
	public void setMessageAssets(@Nonnull String assets){msg_assets=requireNonNull(assets);}
	public @Nonnull String getConfigPath(){return requireNonNull(config);}
	public static void initConfig(String cfg) throws IOException{
		Config.cfg=new Config();
		Config.cfg.config=cfg;
		parseConfig(Config.cfg,cfg);
	}
}
