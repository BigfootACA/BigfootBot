package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.data.URLQuery;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
@SuppressWarnings("unused")
public final class ArchPackage{
	private final static String api="https://archlinux.org/packages/search/json/";
	private final URL url;
	private final int epoch;
	private final long compressed_size,installed_size;
	private final String pkgname,pkgbase,repo,arch,pkgver;
	private final String pkgrel,pkgdesc,filename,packager;
	private final Date build_date,last_update,flag_date;
	private final List<String>maintainers,groups,licenses,conflicts,provides;
	private final List<String>replaces,depends,optdepends,makedepends,checkdepends;
	private final static OkHttpClient h=new OkHttpClient();
	private final static SimpleDateFormat s1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private final static SimpleDateFormat s2=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static String httpQuery(URL url)throws IOException{
		final Response p=h.newCall(new Request.Builder().url(url).build()).execute();
		if(!p.isSuccessful())throw new IOException("http request failed "+p.code());
		return requireNonNull(p.body()).string();
	}
	public static String repoMapping(String name){
		switch(name.toLowerCase()){
			case "community":        return "Community";
			case "community-testing":return "Community-Testing";
			case "core":             return "Core";
			case "extra":            return "Extra";
			case "kde-unstable":     return "KDE-Unstable";
			case "multilib":         return "Multilib";
			case "multilib-testing": return "Multilib-Testing";
			case "testing":          return "Testing";
			default:return name;
		}
	}
	public static List<ArchPackage>search(@Nonnull String keyword)throws IOException,ParseException{return search(keyword,null);}
	public static List<ArchPackage>search(@Nonnull String keyword,@Nullable String repo)throws IOException,ParseException{
		final List<ArchPackage>str=new ArrayList<>();
		final URLQuery u=new URLQuery();
		u.add("q",keyword);
		if(repo!=null)u.add("repo",repoMapping(repo));
		for(Object o:new JSONObject(httpQuery(u.toURLString(api))).getJSONArray("results")){
			if(!(o instanceof JSONObject))continue;
			str.add(new ArchPackage((JSONObject)o));
		}
		str.sort((o1,o2)->{
			final boolean e1=o1.pkgname.equalsIgnoreCase(keyword);
			final boolean e2=o2.pkgname.equalsIgnoreCase(keyword);
			final boolean x1=o1.pkgname.contains(keyword);
			final boolean x2=o2.pkgname.contains(keyword);
			if(e1&&!e2)return -1;
			if(!e1&&e2)return 1;
			if(x1&&!x2)return -1;
			if(!x1&&x2)return 1;
			return 0;
		});
		return str;
	}
	public static ArchPackage query(@Nonnull String name)throws IOException,ParseException{return query(name,null);}
	public static ArchPackage query(@Nonnull String name,@Nullable String repo)throws IOException,ParseException{
		final URLQuery u=new URLQuery();
		u.add("name",name);
		if(repo!=null)u.add("repo",repoMapping(repo));
		final JSONArray ja=new JSONObject(httpQuery(u.toURLString(api))).getJSONArray("results");
		if(ja.length()<=0)return null;
		return new ArchPackage((JSONObject)ja.get(ja.length()-1));
	}
	private static Date parseDate(JSONObject jo,String key,SimpleDateFormat s)throws ParseException{
		if(!jo.has(key))return null;
		final Object o=jo.get(key);
		return o instanceof String?s.parse((String)o):null;
	}
	private static List<String>parseArray(JSONObject jo,String key){
		final List<String>lst=new ArrayList<>();
		if(!jo.has(key))return lst;
		final Object o=jo.get(key);
		if(o instanceof JSONArray)for(Object x:(JSONArray)o)lst.add((String)x);
		return lst;
	}
	private ArchPackage(JSONObject jo)throws ParseException,MalformedURLException{
		pkgname=requireNonNull(jo.getString("pkgname"));
		pkgbase=requireNonNull(jo.getString("pkgbase"));
		repo=requireNonNull(jo.getString("repo"));
		arch=requireNonNull(jo.getString("arch"));
		pkgver=requireNonNull(jo.getString("pkgver"));
		pkgrel=requireNonNull(jo.getString("pkgrel"));
		epoch=jo.getInt("epoch");
		pkgdesc=requireNonNull(jo.getString("pkgdesc"));
		url=new URL(requireNonNull(jo.getString("url")));
		filename=requireNonNull(jo.getString("filename"));
		compressed_size=jo.getLong("compressed_size");
		installed_size=jo.getLong("installed_size");
		build_date=parseDate(jo,"build_date",s1);
		last_update=parseDate(jo,"last_update",s2);
		flag_date=parseDate(jo,"flag_date",s2);
		maintainers=parseArray(jo,"maintainers");
		packager=requireNonNull(jo.getString("packager"));
		groups=parseArray(jo,"groups");
		licenses=parseArray(jo,"licenses");
		conflicts=parseArray(jo,"conflicts");
		provides=parseArray(jo,"provides");
		replaces=parseArray(jo,"replaces");
		depends=parseArray(jo,"depends");
		optdepends=parseArray(jo,"optdepends");
		makedepends=parseArray(jo,"makedepends");
		checkdepends=parseArray(jo,"checkdepends");
	}
	public int getEpoch(){return epoch;}
	public long getCompressedSize(){return compressed_size;}
	public long getInstalledSize(){return installed_size;}
	public @Nonnull String getPackageName(){return pkgname;}
	public @Nonnull String getPackageBase(){return pkgbase;}
	public @Nonnull String getRepository(){return repo;}
	public @Nonnull String getArchitecture(){return arch;}
	public @Nonnull String getPackageVersion(){return pkgver;}
	public @Nonnull String getPackageRelease(){return pkgrel;}
	public @Nonnull String getPackageDescription(){return pkgdesc;}
	public @Nonnull URL getURL(){return url;}
	public @Nonnull String getVersion(){return pkgver+"-"+pkgrel;}
	public @Nonnull String getFileName(){return filename;}
	public @Nonnull String getPackager(){return packager;}
	public @Nullable Date getBuildDate(){return build_date;}
	public @Nullable Date getLastUpdate(){return last_update;}
	public @Nullable Date getFlagDate(){return flag_date;}
	public @Nonnull List<String>getMaintainers(){return maintainers;}
	public @Nonnull List<String>getGroups(){return groups;}
	public @Nonnull List<String>getLicenses(){return licenses;}
	public @Nonnull List<String>getConflicts(){return conflicts;}
	public @Nonnull List<String>getProvides(){return provides;}
	public @Nonnull List<String>getReplaces(){return replaces;}
	public @Nonnull List<String>getDepends(){return depends;}
	public @Nonnull List<String>getOptionalDepends(){return optdepends;}
	public @Nonnull List<String>getMakeDepends(){return makedepends;}
	public @Nonnull List<String>getCheckDepends(){return checkdepends;}
	public @Nonnull URL getWebURL(){
		try{return new URL(format("https://archlinux.org/packages/%s/%s/%s/",repo,arch,pkgname));}
		catch(MalformedURLException e){throw new RuntimeException(e);}
	}
}
