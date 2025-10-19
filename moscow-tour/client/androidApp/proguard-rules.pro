-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

-keep class org.xmlpull.v1.* {*;}
-dontwarn org.xmlpull.v1.**
-dontwarn javax.xml.namespace.**

-keep class ru.krirll.moscowtour.shared.domain.RemoteEvent { *; }
-keep class ru.krirll.moscowtour.shared.domain.RemoteEvent$OnSaved { *; }
-keep class ru.krirll.moscowtour.shared.domain.RemoteEvent$OnRecent { *; }
-keep class ru.krirll.moscowtour.shared.domain.RemoteEvent$OnSearch { *; }
