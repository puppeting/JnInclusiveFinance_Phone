package com.inclusive.finance.jh.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.*
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.VersionBean
import com.inclusive.finance.jh.utils.SZWUtils
import ren.yale.android.intremetalupdate.EasyIncrementalUpdate
import java.io.File

/**
 * 后台下载
 */
class DownloadService : Service() {
    private var mNotificationManager: NotificationManager? = null
    private val binder = DownloadBinder()
    private var mBuilder: NotificationCompat.Builder? = null
    override fun onCreate() {
        super.onCreate()
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder? {
        // 返回自定义的DownloadBinder实例
        return binder
    }

    override fun onDestroy() {
        mNotificationManager = null
        super.onDestroy()
    }

    /**
     * 创建通知
     */
    private fun setUpNotification() {
        mBuilder = NotificationCompat.Builder(this)
        mBuilder?.setContentTitle("开始下载")?.setContentText("正在连接服务器")
            ?.setSmallIcon(R.mipmap.lib_update_app_update_icon)
            ?.setLargeIcon(ImageUtils.drawable2Bitmap(resources.getDrawable(R.mipmap.app_logo)))
            ?.setOngoing(true)?.setWhen(System.currentTimeMillis())
        mNotificationManager?.notify(NOTIFY_ID, mBuilder?.build())
    }

    /**
     * 下载模块
     */
    private fun startDownload(act: Activity, updateBean: VersionBean?, callback: DownloadCallback) {
        val apkUrl: String = if (updateBean?.patchUrl.isNullOrEmpty()) updateBean?.packageUrl?:"" else updateBean?.patchUrl?:""
        if (TextUtils.isEmpty(apkUrl)) {
            val contentText = "新版本下载路径错误"
            stop(contentText)
            return
        }
        val appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length)
        if (!appName.endsWith(".apk") && !appName.endsWith(".patch")) {
            val contentText = "下载包格式有错误"
            stop(contentText)
            return
        }
        val appDir: File = File(Environment.getExternalStorageDirectory()
            .toString() + File.separator + "download" + File.separator)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        //        //可以利用md5验证是否重复下载
        //        updateApp.getHttpManager().download(apkUrl, target, appName, new FileDownloadCallBack(callback));
        starDownloadApk(act,updateBean, apkUrl, appDir.absolutePath, appName, callback)
    }

    private fun stop(contentText: String) {
        mBuilder?.setContentTitle(resources.getString(R.string.app_name))
            ?.setContentText(contentText)
        val notification = mBuilder?.build()
        notification?.flags = Notification.FLAG_AUTO_CANCEL
        mNotificationManager?.notify(NOTIFY_ID, notification)
        close()
    }

    private fun close() {
        stopSelf()
        isRunning = false
    }

    /**
     * 进度条回调接口
     */
    interface DownloadCallback {
        fun onStart()
        fun onProgress(progress: Float)
        fun setMax(total: Float)
        fun onFinish()
        fun onError(msg: String?)
    }

    /**
     * DownloadBinder中定义了一些实用的方法
     *
     * @author user
     */
    inner class DownloadBinder : Binder() {
        /**
         * 开始下载
         */
        fun start(act: Activity, updateApp: VersionBean?, callback: DownloadCallback) {
            //初始化通知栏
            setUpNotification()
            //下载
            startDownload(act, updateApp, callback)
        }
    }

    private fun starDownloadApk(act: Activity, updateBean: VersionBean?, url: String, apkPath: String, fileName: String, callback: DownloadCallback?) {
        OkGo.get<File>(SZWUtils.getIntactUrl(url)).tag(this).execute(object : FileCallback(apkPath, fileName) {
            override fun onStart(request: Request<File?, out Request<*, *>?>?) {
                callback?.onStart()
            }

            override fun onSuccess(response: Response<File>) {
//                callback?.onFinish()


                //
                //
                //                        if (AppUtils.isAppForeground()) {
                //                            //App前台运行
                //                            mNotificationManager.cancel(NOTIFY_ID);
                //                            Intent intent = new Intent(Intent.ACTION_VIEW);
                //                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //                                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                //                            } else {
                //                                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                //                            }
                //                            if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                //                                startActivity(intent);
                //                            }
                //                        } else {
                //                            //App后台运行
                //                            Intent intent = new Intent(Intent.ACTION_VIEW);
                //                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //                                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                //                            } else {
                //                                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                //                            }
                //                            //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                //                            PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //                            mBuilder.setContentIntent(contentIntent)
                //                                    .setContentTitle(getResources().getString(com.shaoyue.weizhegou.R.string.app_name))
                //                                    .setContentText("下载完成，请点击安装")
                //                                    .setProgress(0, 0, false)
                //                                    .setAutoCancel(true)
                //                                    .setDefaults((Notification.DEFAULT_ALL));
                //                            mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
                //                        }
                //=======
                val apkFile = response.body()
                when {
                    response.code()!=200 -> {
                        error(callback, response)
                        return
                    }
                    fileName.endsWith(".patch") -> {
                        val newPath = apkPath + File.separator + fileName.replace(".patch",".apk")
                        val patch = EasyIncrementalUpdate.patch(act, newPath, apkFile.absolutePath)
                        if (patch) {
                            if (updateBean?.md5!=FileUtils.getFileMD5ToString(newPath)) {
                                LogUtils.i("更新Log:"+"MD5-"+FileUtils.getFileMD5ToString(newPath))
                                SZWUtils.showSnakeBarError("文件校验失败，正在全量更新")
                                starDownloadApk(act, updateBean, updateBean?.packageUrl?:"", apkPath, fileName.replace(".patch",".apk"), callback)
                                return
                            }
                            AppUtils.installApp(FileUtils.getFileByPath(newPath))
                        } else{
                            SZWUtils.showSnakeBarError("增量更新合并失败，正在全量更新")
                            starDownloadApk(act, updateBean, updateBean?.packageUrl?:"", apkPath, fileName.replace(".patch",".apk"), callback)
                        }
                    }
                    else -> AppUtils.installApp(apkFile)
                }
                //                AndPermission.with(this@DownloadService).install().file(apkFile)
                //                    .onGranted(object : Action<File?>() {
                //                        fun onAction(data: File?) {
                //                            ToastUtil.showSuccToast("同意了安装权限")
                //                        }
                //                    }).onDenied(object : Action<File?>() {
                //                        fun onAction(data: File?) {
                //                            ToastUtil.showErrorToast("拒绝了安装权限")
                //                        }
                //                    }).start()

                //下载完自杀

                close()
            }

            override fun downloadProgress(progress: Progress) {
                val rate = Math.round(progress.fraction * 100)
                if (callback != null) {
                    callback.setMax(100f)
                    callback.onProgress(progress.fraction * 100)
                }
                mBuilder?.setContentTitle("正在下载：" + resources.getString(R.string.app_name))
                    ?.setContentText("$rate%")?.setProgress(100, rate, false)
                    ?.setWhen(System.currentTimeMillis())
                mNotificationManager?.notify(NOTIFY_ID, mBuilder?.build())
            }

            override fun onError(response: Response<File>) {
                error(callback, response)
            }
        })
    }

    private fun error(callback: DownloadCallback?, response: Response<File>) {
        SZWUtils.showSnakeBarError("更新版本出错")
        //App前台运行
        callback?.onError(response.exception.message)
        try {
            mNotificationManager?.cancel(NOTIFY_ID)
            close()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    companion object {
        private const val NOTIFY_ID = 0
        private val TAG: String = DownloadService::class.java.simpleName
        var isRunning = false

        //    /**
        //     * 开启服务方法
        //     *
        //     * @param context
        //     */
        //    public static void startService(Context context) {
        //        Intent intent = new Intent(context, DownloadService.class);
        //        context.startService(intent);
        //    }
        fun bindService(context: Context, connection: ServiceConnection) {
            val intent = Intent(context, DownloadService::class.java)
            context.startService(intent)
            context.bindService(intent, connection, BIND_AUTO_CREATE)
            isRunning = true
        }
    }
}