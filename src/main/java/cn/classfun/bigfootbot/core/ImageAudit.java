package cn.classfun.bigfootbot.core;
import cn.classfun.bigfootbot.config.Config;
import cn.classfun.bigfootbot.data.ImageAction;
import cn.classfun.bigfootbot.data.ImageType;
import cn.classfun.encoders.Base64;
import cn.classfun.utils.StreamUtils;
import com.tencentcloudapi.ims.v20201229.ImsClient;
import com.tencentcloudapi.ims.v20201229.models.ImageModerationRequest;
import com.tencentcloudapi.ims.v20201229.models.ImageModerationResponse;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.FileMessage;
import net.mamoe.mirai.message.data.FlashImage;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.RemoteFile;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import static cn.classfun.bigfootbot.BigfootBot.blog;
import static cn.classfun.bigfootbot.core.Storage.stor;
import static cn.classfun.bigfootbot.core.Utils.*;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static cn.classfun.bigfootbot.data.ImageAction.*;
import static cn.classfun.bigfootbot.data.ImageType.*;
import static com.tencentcloudapi.ims.v20201229.models.ImageModerationResponse.toJsonString;
public final class ImageAudit extends Thread{
	private static final List<String>formats=Arrays.asList(
		".bmp",".jpg",".jpeg",".png",".gif",".tif",
		".tiff",".svg",".ico",".webp",".wmf",".pcx",
		".tga",".exif",".fpx",".psd",".cdr",".pcd"
	);
	private static final Base64 b64=new Base64();
	private static ImageAction getAction(String sug){
		switch(sug.toLowerCase()){
			case "block":return DENIED;
			case "review":return REJECT;
			case "pass":return ACCEPT;
			default:throw new IllegalArgumentException("invalid suggestion "+sug);
		}
	}
	private static ImageType getType(String lbl){
		switch(lbl.toLowerCase()){
			case "normal":return NORMAL;
			case "porn":return PORN;
			case "abuse":return ABUSE;
			case "ad":return AD;
			default:return OTHER;
		}
	}
	private void doReject(ImageType type,Group g,Member m){
		if(!iamAdmin(g))return;
		switch(type){
			case UNKNOWN:
			case NORMAL:
			case ABUSE:
			case AD:
			case OTHER:return;
		}
		if(canOperate(m))MessageSource.recall(e.getMessage());
		g.sendMessage(format(
			"%s (%d) 尝试发送疑似违规的图片(%s)",
			m.getNick(),
			m.getId(),
			type.name()
		));
		g.sendMessage(new At(g.getOwner().getId()));
	}
	private void doDenied(ImageType type,Group g,Member m){
		boolean can=canOperate(m);
		if(can)MessageSource.recall(e.getMessage());
		g.sendMessage(format(
			"%s (%d) 尝试发送违规的图片(%s)",
			m.getNick(),
			m.getId(),
			type.name()
		));
		if(can){
			boolean mute=true;
			try{
				final Statement s=stor.createStatement();
				final ResultSet c=s.executeQuery(format(
					"select count(message) as counts "+
					"from group_msg "+
					"where group_id=%d and sender_id=%d and time>%d",
					g.getId(),m.getId(),
					System.currentTimeMillis()/1000-7*24*60*60
				));
				long cnt=c.getLong("counts");
				c.close();
				blog.info(format("user send %d messages in last week",cnt));
				if(cnt>10){
					blog.info(format("user %d is active and will not be muted",m.getId()));
					mute=false;
				}
				c.close();
			}catch(SQLException e){
				blog.error("query database failed");
			}
			if(mute)m.mute(60*60);
		}
		if(iamAdmin(g))g.sendMessage(new At(g.getOwner().getId()));
	}
	private void doLarge(Group g,Member m){
		if(canOperate(m))MessageSource.recall(e.getMessage());
		if(iamAdmin(g))e.getGroup().sendMessage(new MessageChainBuilder()
			.append(new At(m.getId()))
			.append(" 请勿发送过大的图片")
			.build()
		);
	}
	private void doAction(File f,ImageAction act,ImageType type){
		try{
			if(act==ACCEPT)return;
			final Group g=e.getGroup();
			final Member m=e.getSender();
			blog.warning(format(
				"user %s (%d) in group %s (%d) "+
				"send picture action %s type %s length %d",
				Utils.getName(m),m.getId(),
				g.getName(),g.getId(),
				act.name(),type.name(),
				f.length()
			));
			switch(act){
				case REJECT:doReject(type,g,m);break;
				case DENIED:doDenied(type,g,m);break;
				case UNKNOWN:if(f.length()>5*1024*1024)doLarge(g,m);break;
			}
		}catch(Exception e){
			blog.error("error while doing image action",e);
		}
	}
	private static final OkHttpClient ohc=new OkHttpClient();
	private File getFile(String id) throws IOException{
		final File file=new File(stor.getAssets(),id);
		if(file.isDirectory())throw new IOException("not a file");
		if(!file.exists()&&!file.createNewFile())throw new IOException("create file");
		return file;
	}
	private boolean checkDownloaded(File file,String id)throws SQLException{
		final Statement sm=stor.createStatement();
		final ResultSet rs=sm.executeQuery(format(
			"select * from image_check where name='%s'",
			b64.encodeString(id)
		));
		if(!rs.next()){
			rs.close();
			sm.close();
			return false;
		}
		if(file.length()<=0){
			blog.warning(format(
				"image %s exists in database but not found in assets",
				file.getName()
			));
			sm.execute(format(
				"delete from image_check where id=%d'",
				rs.getInt("id")
			));
			rs.close();
			sm.close();
			return false;
		}
		blog.info(format("image %s already exists in database",file.getName()));
		doAction(
			file,
			ImageAction.get(rs.getInt("action")),
			ImageType.get(rs.getInt("type"))
		);
		rs.close();
		sm.close();
		return true;
	}
	private void downloadImage(File file,String url) throws IOException{
		Request r=new Request.Builder().url(url).build();
		Response p=ohc.newCall(r).execute();
		if(p.code()!=200)throw new IOException("request image failed "+p.code());
		ResponseBody rb=p.body();
		if(rb==null||rb.contentLength()<=0)throw new IOException("response zero length");
		StreamUtils.stream2stream(
			rb.byteStream(),
			new FileOutputStream(file),
			8192
		);
		blog.info(format(
			"saved image %s (%s) length %d",
			file.getName(),
			rb.contentType(),
			rb.contentLength()
		));
	}
	private void checkImage(File file,String url)throws IOException,SQLException{
		ImsClient ims=Config.cfg.getImsClient();
		ImageAction action=ImageAction.UNKNOWN;
		ImageType type=ImageType.UNKNOWN;
		ImageModerationResponse resp=null;
		if(ims!=null&&file.length()<5*1024*1024)try{
			final ImageModerationRequest req=new ImageModerationRequest();
			req.setFileUrl(url);
			resp=requireNonNull(ims.ImageModeration(req));
			action=getAction(resp.getSuggestion());
			type=getType(resp.getLabel());
			blog.debug(format(
				"tencentcloudapi response %s action %s type %s",
				file.getName(),action.name(),type.name()
			));
			doAction(file,action,type);
		}catch(Exception e){
			blog.error("error while check image",e);
		}
		final Statement sm=stor.createStatement();
		sm.execute(format(
			"insert into image_check(url,save,name,type,action,size,time,response)values"+
			"('%s','%s','%s',%d,%d,%d,%d,'%s')",
			b64.encodeString(url),
			b64.encodeString(file.getCanonicalPath()),
			b64.encodeString(file.getName()),
			type.getID(),
			action.getID(),
			file.length(),
			System.currentTimeMillis()/1000,
			resp==null?"":b64.encodeString(toJsonString(resp))
		));
		sm.close();
	}
	public synchronized void processImage(String id,String url){
		try{
			final File file=getFile(id);
			if(checkDownloaded(file,id))return;
			downloadImage(file,url);
			checkImage(file,url);
		}catch(Exception e){
			blog.error("error while process image",e);
		}
	}
	private final GroupMessageEvent e;
	public ImageAudit(@Nonnull GroupMessageEvent e){this.e=e;}
	public@Override void run(){
		for(SingleMessage s:e.getMessage()){
			String id=null,url=null;
			Image i=null;
			if(s instanceof FlashImage)i=((FlashImage)s).getImage();
			else if(s instanceof Image)i=(Image)s;
			else if(s instanceof FileMessage){
				final FileMessage f=(FileMessage)s;
				final String name=f.getName();
				if(!name.contains("."))continue;
				final String ext=name.substring(name.lastIndexOf("."));
				if(!formats.contains(ext.toLowerCase()))continue;
				RemoteFile r=f.toRemoteFile(e.getGroup());
				if(r==null)continue;
				RemoteFile.DownloadInfo d=r.getDownloadInfo();
				if(d==null)continue;
				url=d.getUrl();
			}else continue;
			if(i!=null){
				id=i.getImageId();
				url=Image.queryUrl(i);
			}
			try{processImage(id,url);}
			catch(Exception x){blog.error(format("cannot get image %s",id),x);}
		}
	}
}
