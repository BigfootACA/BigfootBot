import java.util.Date
import java.text.SimpleDateFormat
import java.net.InetAddress
plugins{
	val kotlinVersion="1.5.10"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.serialization") version kotlinVersion
}
dependencies{
	api("cn.classfun",           "utils",                  "1.1")
	api("cn.classfun",           "encoders",               "1.0")
	api("cn.classfun",           "ipcalculator",           "1.1")
	api("com.squareup.okhttp3",  "okhttp",                 "4.9.1")
	api("com.tencentcloudapi",   "tencentcloud-sdk-java",  "3.1.217")
	api("com.google.guava",      "guava",                  "29.0-jre")
	api("org.json",              "json",                   "20210307")
	api("org.xerial",            "sqlite-jdbc",            "3.36.0.1")
	api("net.mamoe",             "mirai-core-api",         "2.6.7")
	runtimeOnly("net.mamoe",     "mirai-core",             "2.6.7")
}
val ver:String=ProcessBuilder("git describe --tags --always".split(" "))
	.redirectOutput(ProcessBuilder.Redirect.PIPE)
	.redirectError(ProcessBuilder.Redirect.PIPE)
	.start()
	.apply{waitFor(20,TimeUnit.SECONDS)}
	.run{inputStream.bufferedReader().readText().trim()}
if(ver.isEmpty())error("cannot get version")
val date:String=SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
val builder:String=System.getenv("USER")+"@"+InetAddress.getLocalHost().hostName
println("build version: $ver")
println("build date:    $date")
println("builder:       $builder")
group="cn.classfun.bigfootbot"
version=ver
repositories{
	maven("/mnt/maven")
	maven("https://maven.aliyun.com/repository/public")
	mavenCentral()
}
tasks.withType<Jar>{
	manifest{
		attributes["Main-Class"]="cn.classfun.bigfootbot.BigfootBot"
		attributes["Implementation-Version"]="$ver-$date"
		attributes["Implementation-Vendor"]=builder
		attributes["Created-By"]=builder
	}
}
