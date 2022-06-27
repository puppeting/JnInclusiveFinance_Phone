package com.inclusive.finance.jh.app

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.aice.appstartfaster.dispatcher.AppStartTaskDispatcher
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.common.BaiduMapSDKException
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.loadmultidex.AppStartTaskOne
import com.inclusive.finance.jh.app.loadmultidex.AppStartTaskTwo
import com.inclusive.finance.jh.app.loadmultidex.multidex.MultidexUtils
import com.inclusive.finance.jh.glide.PictureSelectorEngineImp
import com.inclusive.finance.jh.utils.SZWUtils
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.engine.PictureSelectorEngine
import com.mabeijianxi.smallvideorecord2.JianXiCamera
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import dagger.hilt.android.HiltAndroidApp
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * Created by 史忠文
 * on 2018/04/16.
 */
@HiltAndroidApp
class ToolApplication : MyApplication(), IApp,CameraXConfig.Provider {
    val TAG: String? = MyApplication::class.java.simpleName
//    lateinit var engineGroup: FlutterEngineGroup //flutter 引擎
    override fun getSaltStr(): String = ""

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        val isMainProcess: Boolean = MultidexUtils.isMainProcess(base)
        if (isMainProcess && !MultidexUtils.isVMMultidexCapable) {
            MultidexUtils.loadMultiDex(base)
        } else {
            MultidexUtils.preNewActivity()
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (MultidexUtils.isMainProcess(this)) {
            AppStartTaskDispatcher.create().setShowLog(true).setAllTaskWaitTimeOut(1000)
                .addAppStartTask(AppStartTaskOne(this))
                .addAppStartTask(AppStartTaskTwo(this))
                .start().await()
        }
        // 是否同意隐私政策，默认为false
        // 是否同意隐私政策，默认为false
//        SDKInitializer.setAgreePrivacy(applicationContext, true)
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(applicationContext)
            SDKInitializer.setCoordType(CoordType.BD09LL)
        } catch (e: BaiduMapSDKException) {
        }
//        SDKInitializer.initialize(applicationContext())
    }

    /**
     *
     * 初始化flutter 加载引擎，但路由会固定。
     * */
      fun initFlutterEngineCache() {
        // Instantiate a FlutterEngine.
//        val flutterEngine =   FlutterEngine(this);
//        engineGroup=FlutterEngineGroup(this)
        // Start executing Dart code to pre-warm the FlutterEngine.
//        flutterEngine.dartExecutor.executeDartEntrypoint(
//            DartEntrypoint.createDefault()
//        );
//        // Cache the FlutterEngine to be used by FlutterActivity.
//        FlutterEngineCache.getInstance()
//            .put("flutterEngineId", flutterEngine);
    }

    /**
     *
     * 初始化压缩组件地址
     * */
    fun initSmallVideo() {
        // 设置拍摄视频缓存路径
        val movies = File(SZWUtils.createCustomMoviesOutPath(this))
        JianXiCamera.setVideoCachePath(movies.path)
        // 初始化拍摄，遇到问题可选择开启此标记，以方便生成日志
        JianXiCamera.initialize(false, null)
    }
    init {

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(object : DefaultRefreshHeaderCreator {
            override fun createRefreshHeader(context: Context, layout: RefreshLayout): RefreshHeader {
                layout.setPrimaryColorsId(R.color.colorAccent, R.color.AntiqueWhite)//全局设置主题颜色
                var classicsHeader:ClassicsHeader =ClassicsHeader(context)
                return classicsHeader////指定为经典Header，默认是 贝塞尔雷达Header
            }
        })
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(object :  DefaultRefreshFooterCreator{
            //            @Override public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                ClassicsFooter classicsFooter = new ClassicsFooter(context)
//                classicsFooter.setDrawableSize(20)
//                //底部加载动画和布局
//                classicsFooter.setVisibility(View.GONE)
//                //内容不满一页时不能开启上拉加载功能
//                layout.setEnableLoadMoreWhenContentNotFull(false)
//                return classicsFooter
//            }
            override fun createRefreshFooter(context: Context, layout: RefreshLayout): RefreshFooter {
                var classicsFooter:ClassicsFooter =  ClassicsFooter(context)
                classicsFooter.setDrawableSize(20F)
                //底部加载动画和布局
                classicsFooter.setVisibility(View.GONE)
                //内容不满一页时不能开启上拉加载功能
                layout.setEnableLoadMoreWhenContentNotFull(false)
                return classicsFooter
            }
        })
    }


    /**
     *
     * 信任所有证书
     * */
    fun handleSSLHandshake() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

                override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            })
            val sc: SSLContext = SSLContext.getInstance("TLS")
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        } catch (ignored: Exception) {
        }
    }


    override fun getAppContext(): Context? {
        return this
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine? {
        return PictureSelectorEngineImp()
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    //    private fun initUpush() {
    //        //获取消息推送代理示例
    //        val mPushAgent = PushAgent.getInstance(this)
    //        mPushAgent.setNotificaitonOnForeground(true)
    //        InAppMessageManager.getInstance(this).setInAppMsgDebugMode(true)
    //
    //
    //        val messageHandler = object : UmengMessageHandler() {
    //            override fun dealWithNotificationMessage(p0: Context?, p1: UMessage?) {
    //                UTrack.getInstance(applicationContext).trackMsgClick(p1)
    //                super.dealWithNotificationMessage(p0, p1)
    //            }
    //
    //            //            override fun getNotification(p0: Context?, p1: UMessage?): Notification {
    //            //                when (p1?.builder_id) {
    //            //                    1 -> {
    //            //                        val id = "my_channel_01";
    //            //                        val name="我是渠道名字";
    //            //                        val notificationManager = getSystemService(NOTIFICATION_SERVICE)as NotificationManager
    //            //                        val notification :Notification
    //            //                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //            //                            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
    //            //                            Toast.makeText(p0, mChannel.toString(), Toast.LENGTH_SHORT).show();
    //            //                            notificationManager.createNotificationChannel(mChannel);
    //            //                            notification = Notification.Builder(p0)
    //            //                                    .setChannelId(id)
    //            //                                    .setContentTitle("5 new messages")
    //            //                                    .setContentText("hahaha")
    //            //                                    .setSmallIcon(R.mipmap.ic_launcher).build();
    //            //                        } else {
    //            //                            val notificationBuilder = NotificationCompat.Builder(p0)
    //            //                                    .setContentTitle("5 new messages")
    //            //                                    .setContentText("hahaha")
    //            //                                    .setSmallIcon(R.mipmap.ic_launcher)
    //            //                                    .setOngoing(true)
    //            //                                    .setChannelId(id)
    //            //                            notification = notificationBuilder.build();
    //            //                        }
    //            //                        return notification
    //            //                    }
    //            //                    else ->
    //            //                        //默认为0，若填写的builder_id并不存在，也使用默认。
    //            //                        return super.getNotification(p0, p1)
    //            //                }
    //            //
    //            //            }
    //        }
    //        mPushAgent.messageHandler = messageHandler
    //
    //
    //        //注册推送服务，每次调用register方法都会回调该接口
    //        mPushAgent.register(object : IUmengRegisterCallback {
    //            override fun onSuccess(deviceToken: String) {
    //                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
    //                Log.i("deviceToken", "注册成功：deviceToken：-------->  $deviceToken")
    //
    //            }
    //
    //            override fun onFailure(s: String, s1: String) {
    //                Log.e("deviceToken", "注册失败：-------->  s:$s,s1:$s1")
    //            }
    //        })
    //        //各个平台的配置，建议放在全局Application或者程序入口
    //        PlatformConfig.setWeixin("wx0974b5883005adec", "edd40368cb7cde135e3a9392f4abb512")
    //    }

      fun initX5(){
//        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        val cb = object : QbSdk.PreInitCallback {

            override fun onViewInitFinished(arg0: Boolean) {
                Log.i("打印日志", "View是否初始化完成:$arg0")
            }

            override fun onCoreInitFinished() {
                Log.i("打印日志", "X5内核初始化完成")
            }
        }

        QbSdk.setTbsListener(object : TbsListener {
            override fun onDownloadFinish(i: Int) {
                Log.i("打印日志", "腾讯X5内核 下载结束")
            }

            override fun onInstallFinish(i: Int) {
                Log.i("打印日志", "腾讯X5内核 安装完成")
            }

            override fun onDownloadProgress(i: Int) {
                Log.i("打印日志", "腾讯X5内核 下载进度:%$i")
            }
        })
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(applicationContext, cb)
        QbSdk.setDownloadWithoutWifi(true)

    }


      fun initAutoSize() {
        AutoSizeConfig.getInstance().setBaseOnWidth(true).unitsManager.setSupportDP(false)
            .setSupportSP(false).supportSubunits = Subunits.MM
    }

    companion object {
        fun getAPP(app: Application): ToolApplication = app as ToolApplication
        const val appDebug = false
    }
}
