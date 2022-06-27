package com.inclusive.finance.jh.app.loadmultidex

import android.util.Log
import com.aice.appstartfaster.executor.TaskExceutorManager
import com.aice.appstartfaster.task.AppStartTask
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.inclusive.finance.jh.app.ToolApplication
import com.inclusive.finance.jh.utils.SoundUtil
import com.luck.picture.lib.app.PictureAppMaster
import com.umeng.commonsdk.UMConfigure
import java.util.concurrent.Executor

internal class AppStartTaskTwo(var toolApplication: ToolApplication) : AppStartTask() {
    override fun run() {
        val start = System.currentTimeMillis()
        try {
            Thread.sleep(1000)
            //子线程初始化第三方组件
            toolApplication.init(true)        //数据库初始化
//        boxStore = MyObjectBox.builder().androidContext(this).build()
            toolApplication.initX5()
            UMConfigure.setLogEnabled(true)
            //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
//        UMConfigure.init(this, "5d101f8a0cafb285430000a7", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "ef5c818094f6924dac1b6f0ea3aab2ee")
            // PushSDK初始化(如使用推送SDK，必须调用此方法)
            //        initUpush()
            /**
             * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
             * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
             * UMConfigure.init调用中appkey和channel参数请置为null）。
             */
            UMConfigure.init(toolApplication, "603f398cb8c8d45c13898050", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
            toolApplication.handleSSLHandshake()
            toolApplication.initSmallVideo()
            /** PictureSelector日志管理配制开始 **/
            // PictureSelector 绑定监听用户获取全局上下文或其他...
            PictureAppMaster.getInstance().app = toolApplication
            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            SDKInitializer.initialize(toolApplication);
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL)
            SoundUtil.initSound(toolApplication)
        } catch (e: Exception) {
        }
        Log.i("Task:", "TestAppStartTaskTwo执行耗时: " + (System.currentTimeMillis() - start))
    }

    override fun runOnExecutor(): Executor {
        return TaskExceutorManager.getInstance().cpuThreadPoolExecutor
    }

    override fun getDependsTaskList(): List<Class<out AppStartTask>> {
        val dependsTaskList: MutableList<Class<out AppStartTask>> = ArrayList()
        dependsTaskList.add(AppStartTaskOne::class.java)
        return dependsTaskList
    }

    override fun isRunOnMainThread(): Boolean {
        return false
    }
}