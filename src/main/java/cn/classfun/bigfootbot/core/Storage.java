package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.config.Config;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.NotDirectoryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static cn.classfun.bigfootbot.config.Config.cfg;
import static java.lang.String.format;
import static java.sql.DriverManager.getConnection;
public class Storage{
	public static Storage stor=null;
	private File assets;
	public Connection con;
	public File getAssets(){return assets;}
	public void parseStorage(@Nonnull Config c,@Nonnull JSONObject stor){
		c.setMessageAssets(stor.getString("assets"));
		c.setMessageDatabase(stor.getString("database"));
		initStorage();
	}
	public void initDatabase()throws SQLException,ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		con=getConnection(format(
			"jdbc:sqlite:%s",
			cfg.getMessageDatabase()
		));
		initTables();
	}
	public void initAssets()throws IOException{
		assets=new File(cfg.getMessageAssets());
		if(!assets.exists()&&!assets.mkdirs())
			throw new IOException("mkdir assets failed");
		if(assets.isFile())
			throw new NotDirectoryException("assets folder is a file");
		final File test=new File(assets,".test");
		if(!test.createNewFile())
			throw new IOException("failed to create file in assets folder");
		final OutputStream o=new FileOutputStream(test);
		o.write(new byte[]{1,2,3,4});
		o.flush();
		o.close();
		if(test.length()!=4)
			throw new IOException("failed to write file in assets folder");
		if(!test.delete())
			throw new IOException("failed to delete file in assets folder");
	}
	public void initStorage(){
		try{
			initAssets();
			initDatabase();
		}catch(Exception e){
			System.err.println("Failed to load storage backend");
			throw new RuntimeException(e);
		}
	}
	private Storage(){}
	public static @Nonnull Storage getStorage(){
		if(stor==null)stor=new Storage();
		return stor;
	}
	private void initTables()throws SQLException{
		final Statement sm=con.createStatement();
		sm.execute(
			"create table if not exists group_msg("+
				"id integer primary key autoincrement,"+
				"group_id integer not null,"+
				"sender_id integer not null,"+
				"time integer not null,"+
				"message text"+
			")"
		);
		sm.execute(
			"create table if not exists group_rename("+
				"id integer primary key autoincrement,"+
				"group_id integer not null,"+
				"operator_id integer not null,"+
				"time integer not null,"+
				"origin text,"+
				"new text"+
			")"
		);
		sm.execute(
			"create table if not exists member_join("+
				"id integer primary key autoincrement,"+
				"group_id integer not null,"+
				"member_id integer not null,"+
				"time integer not null"+
			")"
		);
		sm.execute(
			"create table if not exists member_leave("+
				"id integer primary key autoincrement,"+
				"group_id integer not null,"+
				"member_id integer not null,"+
				"operator_id integer not null,"+
				"time integer not null"+
			")"
		);
		sm.execute(
			"create table if not exists member_join_request("+
				"id integer primary key autoincrement,"+
				"group_id integer not null,"+
				"member_id integer not null,"+
				"invitor_id integer not null,"+
				"time integer not null,"+
				"message text"+
			")"
		);
		sm.execute(
			"create table if not exists group_mute("+
				"id integer primary key autoincrement,"+
				"group_id integer not null,"+
				"member_id integer not null,"+
				"operator_id integer not null,"+
				"time integer not null,"+
				"remaining integer not null,"+
				"type text"+
			")"
		);
		sm.execute(
			"create table if not exists image_check("+
				"id integer primary key autoincrement,"+
				"url text,"+
				"save text not null,"+
				"name text not null,"+
				"type integer not null,"+
				"action integer not null,"+
				"size integer not null,"+
				"time integer not null,"+
				"response text"+
			")"
		);
		sm.execute(
			"create table if not exists image_violation("+
				"id integer primary key autoincrement,"+
				"check_id integer not null,"+
				"group_id integer not null,"+
				"member_id integer not null,"+
				"msg_id integer not null,"+
				"type integer not null,"+
				"action integer not null,"+
				"time integer not null"+
			")"
		);
		sm.closeOnCompletion();
	}
}
