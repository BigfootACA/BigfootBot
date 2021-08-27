plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

dependencies {
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

group = "cn.classfun.bigfootbot"
version = "0.1"

repositories {
    maven("/mnt/maven")
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
