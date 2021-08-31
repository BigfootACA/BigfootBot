package cn.classfun.bigfootbot.hooks.github;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static cn.classfun.bigfootbot.core.Utils.byte2hex;
public final class GitHubHookProcess{
	private OutputStream out;
	private final InputStream in;
	private final Headers hs;
	private final HttpExchange e;
	private String data,ev;
	private String s1,s256;
	private JSONObject json;
	GitHubHookProcess(HttpExchange e){
		this.e=e;
		in=e.getRequestBody();
		out=e.getResponseBody();
		hs=e.getRequestHeaders();
	}
	boolean init()throws IOException{
		if(
			!hs.containsKey("X-GitHub-Event")||
			!hs.containsKey("Content-Type")||
			!hs.containsKey("User-Agent")||
			!hs.containsKey("X-Hub-Signature")||
			!hs.containsKey("X-Hub-Signature-256")
		){
			response(404,null);
			return false;
		}
		ev=hs.getFirst("X-GitHub-Event").trim().toLowerCase();
		if(!hs.getFirst("User-Agent").trim().startsWith("GitHub-Hookshot/")){
			response(404,null);
			return false;
		}
		if(!hs.getFirst("Content-Type").trim().equalsIgnoreCase("application/json")){
			response(400,"invalid type");
			return false;
		}
		data=new String(in.readAllBytes());
		json=new JSONObject(data);
		final String[]ss1=hs.getFirst("X-Hub-Signature").split("=");
		final String[]ss256=hs.getFirst("X-Hub-Signature-256").split("=");
		if(
			ss1.length!=2||ss256.length!=2||
			!ss1[0].equalsIgnoreCase("sha1")||
			!ss256[0].equalsIgnoreCase("sha256")
		){
			response(400,"invalid signature");
			return false;
		}
		s1=ss1[1];
		s256=ss256[1];
		return true;
	}
	public String getEvent(){return ev;}
	public JSONObject getJSON(){return json;}
	public String getSign(Mac mac){return byte2hex(mac.doFinal(data.getBytes()));}
	public boolean checkSign1(Mac mac){return s1.equalsIgnoreCase(getSign(mac));}
	public boolean checkSign256(Mac mac){return s256.equalsIgnoreCase(getSign(mac));}
	public void response(int code,@Nullable String string)throws IOException{
		final int len=string==null?0:string.length();
		if(out==null)return;
		e.sendResponseHeaders(code,len);
		if(len>0)out.write(string.getBytes());
		close();
	}
	public void close()throws IOException{
		if(out==null)return;
		out.close();
		out=null;
	}
}