package cn.classfun.bigfootbot.upgrade;
import cn.classfun.bigfootbot.BigfootBot;
import cn.classfun.encoders.Base64;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static java.lang.String.format;
public final class AddImageViolation{
	private static long icnt=0,pcnt=0;
	private static void doUpgrade()throws Exception{
		final Base64 b64=new Base64();
		final Statement s1=stor.con.createStatement();
		final Statement s2=stor.con.createStatement();
		final ResultSet msg=s1.executeQuery("select * from group_msg");
		while(msg.next()){
			final String text=b64.decodeString(msg.getString("message"));
			for(String p:text.split("\\[mirai:")){
				int x1,x2;
				if((x1=p.indexOf(":"))<0)continue;
				switch(p.substring(0,x1)){
					case "image":case "flash":break;
					default:continue;
				}
				if((x2=p.indexOf("]"))<0)continue;
				final String img=p.substring(x1+1,x2).trim();
				if(img.length()<=0)continue;
				icnt++;
				final ResultSet imc=s2.executeQuery(format(
					"select * from image_check "+
					"where name='%s'",
					b64.encodeString(img)
				));
				if(!imc.next())continue;
				final int act=imc.getInt("action"),type=imc.getInt("type");
				pcnt++;
				if(act<=1||type<=1)continue;
				s2.execute(format(
					"insert into image_violation"+
					"(check_id,group_id,member_id,msg_id,type,action,time)values"+
					"(%d,%d,%d,%d,%d,%d,%d)",
					imc.getInt("id"),
					msg.getLong("group_id"),
					msg.getLong("sender_id"),
					msg.getInt("id"),
					type,act,
					msg.getInt("time")
				));
				imc.close();
			}
		}
		msg.close();
		s1.closeOnCompletion();
		s2.closeOnCompletion();
		System.out.printf("found %d, processed %d, ignored %d images\n",icnt,pcnt,icnt-pcnt);
	}
	public static void main(String[]argv)throws IOException, SQLException{
		int r=0;
		BigfootBot.earlyInit(argv);
		final Statement s=stor.con.createStatement();
		final ResultSet top=s.executeQuery("select count(id) as count from image_violation");
		if(!top.next())throw new SQLException("cannot get next");
		if(top.getInt("count")!=0)throw new IllegalStateException("image_violation is not empty");
		final WatchThread wt=new WatchThread();
		wt.start();
		try{doUpgrade();}catch(Exception e){
			System.err.println("upgrade failed");
			e.printStackTrace();
			System.err.println("cleanup...");
			s.execute("drop table image_violation");
			r=1;
		}
		wt.interrupt();
		s.closeOnCompletion();
		stor.con.close();
		System.exit(r);
	}
	private static class WatchThread extends Thread{
		@SuppressWarnings("BusyWait")
		public@Override void run(){
			long old=0;
			while(true)try{
				System.out.printf("\rfound %d, processed %d, %d images/sec\r",icnt,pcnt,icnt-old);
				old=icnt;
				Thread.sleep(1000);
			}catch(InterruptedException ignore){break;}
		}
	}
}
