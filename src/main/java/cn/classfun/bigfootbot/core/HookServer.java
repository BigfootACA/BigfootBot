package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.hooks.github.GitHubWebHook;
import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import static cn.classfun.bigfootbot.config.Config.cfg;
public final class HookServer{
	private static HttpServer server=null;
	public static void initHookServer(){
		final int port=cfg.getHookServerPort();
		final String listen=cfg.getHookServerListen();
		if(server!=null)server.stop(0);
		if(port<=0)return;
		try{
			final InetAddress ia=InetAddress.getByName(listen);
			final InetSocketAddress isa=new InetSocketAddress(ia,port);
			server=HttpServer.create(isa,0);
			server.createContext("/hook/github",new GitHubWebHook());
			server.start();
			System.out.printf(
				"start hook server with %s:%d\n",
				ia.getHostAddress(),
				port
			);
		}catch(Exception e){
			System.err.println("error while startup hook server");
			e.printStackTrace();
		}
	}
}
