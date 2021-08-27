package cn.classfun.bigfootbot.core;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.Response;
import org.json.JSONArray;
import javax.annotation.Nonnull;
import java.io.IOException;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static java.util.Objects.requireNonNull;
public final class Translate{
	private static final OkHttpClient c=new OkHttpClient();
	private static String api=null;
	public static void setAPI(@Nonnull String api){Translate.api=requireNonNull(api);}
	public static @Nonnull String getAPI(){return requireNonNull(api);}
	public static String translate(
		@Nonnull String source_lang,
		@Nonnull String target_lang,
		@Nonnull String string
	){
		try{
			final Request r=new Request.Builder()
				.url(getAPI())
				.post(new FormBody.Builder()
					.add("sl",source_lang)
					.add("tl",target_lang)
					.add("q",string)
					.build()
				).build();
			final Response p=c.newCall(r).execute();
			if(!p.isSuccessful())throw new IOException("http request failed "+p.code());
			final ResponseBody rb=requireNonNull(p.body());
			return new JSONArray(rb.string())
				.getJSONArray(0)
				.getJSONArray(0)
				.getString(0);
		}catch(Exception e){
			blog.warning("translate failed",e);
		}
		return null;
	}
	private Translate(){throw new RuntimeException();}
}
