package com.inclusive.finance.jh.app.loadmultidex.multidex

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.inclusive.finance.jh.app.loadmultidex.LoadMultiDexActivity
import androidx.multidex.MultiDex
import com.inclusive.finance.jh.ui.login.StartPageTransActivity
import com.inclusive.finance.jh.ui.login.LoginPasswordActivity
import java.io.File
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.regex.Pattern

object MultidexUtils {
    private const val TAG = "MultidexUtils"

    //是否在主进程
    fun isMainProcess(context: Context): Boolean {
        return context.packageName == getProcessName(context)
    }

    fun getProcessName(context: Context): String? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        val myPid = Process.myPid()
        if (appProcesses == null || appProcesses.size == 0) {
            return null
        }
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                if (appProcess.pid == myPid) {
                    return appProcess.processName
                }
            }
        }
        return null
    }

    val isVMMultidexCapable: Boolean
        get() = isVMMultidexCapable(System.getProperty("java.vm.version"))

    //MultiDex 拷出来的的方法，判断VM是否支持多dex
    fun isVMMultidexCapable(versionString: String?): Boolean {
        var isMultidexCapable = false
        if (versionString != null) {
            val matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString)
            if (matcher.matches()) {
                try {
                    val major = matcher.group(1).toInt()
                    val minor = matcher.group(2).toInt()
                    isMultidexCapable = major > 2 || major == 2 && minor >= 1
                } catch (var5: NumberFormatException) {
                }
            }
        }
        Log.i("MultiDex", "VM with version " + versionString + if (isMultidexCapable) " has multidex support" else " does not have multidex support")
        return isMultidexCapable
    }

    fun loadMultiDex(context: Context) {
        newTempFile(context) //创建临时文件
        //启动另一个进程去加载MultiDex
        val intent = Intent(context, LoadMultiDexActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        //检查MultiDex是否安装完（安装完会删除临时文件）
        checkUntilLoadDexSuccess(context)
        //另一个进程以及加载 MultiDex，有缓存了，所以主进程再加载就很快了。
        //为什么主进程要再加载，因为每个进程都有一个ClassLoader
        val startTime = System.currentTimeMillis()
        MultiDex.install(context)
        Log.d(TAG, "第二次 MultiDex.install 结束，耗时: " + (System.currentTimeMillis() - startTime))
        preNewActivity()
    }

    //创建一个临时文件，MultiDex install 成功后删除
    fun newTempFile(context: Context) {
        try {
            val file = File(context.cacheDir.absolutePath, "load_dex.tmp")
            if (!file.exists()) {
                Log.d(TAG, "newTempFile: ")
                file.createNewFile()
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }

    /**
     * 检查MultiDex是否安装完,通过判断临时文件是否被删除
     * @param context
     * @return
     */
    fun checkUntilLoadDexSuccess(context: Context) {
        val file = File(context.cacheDir.absolutePath, "load_dex.tmp")
        var i = 0
        val waitTime = 100 //睡眠时间
        try {
            Log.d(TAG, "checkUntilLoadDexSuccess: >>> ")
            while (file.exists()) {
                Thread.sleep(waitTime.toLong())
                i++
                Log.d(TAG, "checkUntilLoadDexSuccess: sleep count = $i")
                if (i > 400) {
                    Log.d(TAG, "checkUntilLoadDexSuccess: 超时，等待时间： " + waitTime * i)
                    break
                }
            }
            Log.d(TAG, "checkUntilLoadDexSuccess: 轮循结束，等待时间 " + waitTime * i)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun preNewActivity() {
        val startTime = System.currentTimeMillis()
        val splashActivity = StartPageTransActivity()
        val loginPasswordActivity = LoginPasswordActivity()
        Log.d(TAG, "preNewActivity 耗时: " + (System.currentTimeMillis() - startTime))
    }
}