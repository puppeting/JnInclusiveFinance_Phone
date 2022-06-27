package com.inclusive.finance.jh.app.loadmultidex

import android.os.Process
import android.util.Log
import com.aice.appstartfaster.task.AppStartTask
import com.inclusive.finance.jh.app.ToolApplication

class AppStartTaskOne(var toolApplication: ToolApplication) : AppStartTask() {
    override fun run() {
        val start = System.currentTimeMillis()
        try {
            //设置线程的优先级，不与主线程抢资源
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)

            toolApplication.initARouter(false)
            toolApplication.initAutoSize()
            toolApplication.initFlutterEngineCache()

        } catch (e: java.lang.Exception) {
        }
        Log.i("Task:", "TestAppStartTaskOne执行耗时: " + (System.currentTimeMillis() - start))
    }

    override fun getDependsTaskList(): MutableList<Class<out AppStartTask>>? {
        return null
    }

    override fun isRunOnMainThread(): Boolean {
        return true
    }
}