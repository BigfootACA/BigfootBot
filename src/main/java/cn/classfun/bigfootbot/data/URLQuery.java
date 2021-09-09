package cn.classfun.bigfootbot.data;
import cn.classfun.encoders.Coder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import static java.lang.String.format;
@SuppressWarnings("unused")
public class URLQuery extends HashMap<String,String>{
	private static final Coder c=Coder.forName("url");
	public URLQuery add(@Nonnull String key,@Nullable String value){
		put(key,value);
		return this;
	}
	public URLQuery del(@Nonnull String key){
		remove(key);
		return this;
	}
	public URLQuery parse(@Nonnull URL url){return parse(url.getQuery());}
	public URLQuery parse(@Nullable String query){
		clear();
		if(query==null)return this;
		query=query.trim();
		if(query.length()<=0)return this;
		final String[]s;
		if(query.contains("&"))s=query.split("&");
		else s=new String[]{query};
		for(final String x:s)parseSingle(x);
		return this;
	}
	public URLQuery parseSingle(String query){
		String key,value;
		int i=query.indexOf('=');
		if(i<0){
			key=query;
			value=null;
		}else{
			key=c.decodeString(query.substring(0,i));
			value=c.decodeString(query.substring(i+1));
		}
		add(key,value);
		return this;
	}
	public URL toURLString(String url) throws MalformedURLException{
		final String q=toQueryString();
		return new URL(q.length()<=0?url:format("%s?%s",url,q));
	}
	public String toQueryString(){
		final StringBuilder sb=new StringBuilder();
		for(final String k:keySet()){
			final String v=get(k);
			sb.append(c.encodeString(k));
			if(get(k)!=null)sb.append('=').append(c.encodeString(get(k)));
			sb.append("&");
		}
		if(sb.length()>0)sb.setLength(sb.length()-1);
		return sb.toString();
	}
	public@Override String toString(){
		final String q=toQueryString();
		return (q.length()<=0)?super.toString():q;
	}
	public URLQuery(){super();}
	public URLQuery(String query){super();parse(query);}
	public URLQuery(URL url){super();parse(url);}
}
