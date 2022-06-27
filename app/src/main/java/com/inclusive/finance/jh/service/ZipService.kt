package com.inclusive.finance.jh.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.UriUtils
import com.hw.videoprocessor.VideoProcessor
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.pop.LoadingPop
import com.inclusive.finance.jh.ui.MainActivity
import com.inclusive.finance.jh.utils.SZWUtils
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig
import com.umeng.umcrash.UMCrash
import org.jetbrains.anko.runOnUiThread
import java.io.File


/**
 * 后台压缩
 */
class ZipService : Service() {
    private val binder = ZipBinder()
    var loadingPop: LoadingPop? = null
    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(intent: Intent): IBinder? {
        // 返回自定义的DownloadBinder实例
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    var notification: Notification? = null
    var manager: NotificationManager? = null
    var notifyBuilder: NotificationCompat.Builder? = null
    private val NOTIFY_ID = 100

    /**
     * 创建通知
     */
    private fun showNotification() {
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val hangIntent = Intent(this, MainActivity::class.java)
        val hangPendingIntent =
            PendingIntent.getActivity(this, 1001, hangIntent, PendingIntent.FLAG_IMMUTABLE)

        val CHANNEL_ID = "your_custom_id" //应用频道Id唯一值， 长度若太长可能会被截断，
        val CHANNEL_NAME = "your_custom_name" //最长40个字符，太长会被截断
        notifyBuilder = NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("开始压缩")
            .setContentText("正在上传中").setSmallIcon(R.mipmap.app_logo1)
            .setContentIntent(hangPendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.app_logo))
            .setAutoCancel(true)
        notification = notifyBuilder?.build()

        //Android 8.0 以上需包添加渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            manager?.createNotificationChannel(notificationChannel)
        }
        manager?.notify(NOTIFY_ID, notification)

    }


    /**
     * 下载模块
     */
    private fun startZip(act: BaseActivity?, filePath: Uri, keyId: String, businessType: Int) {
        loadingPop?.loadPath(filePath)
        loadingPop?.showPopupWindow()
        val outPutPath = File(SZWUtils.createCustomMoviesOutPath(act))
        if (!outPutPath.exists()) {
            outPutPath.mkdirs()
        }
        val filePrefix = "zip_video"
        val fileExtn = ".mp4"
        var dest: File = File(outPutPath, filePrefix + fileExtn)
        var fileNo = 0
        while (dest.exists()) {
            fileNo++
            dest = File(outPutPath, filePrefix + fileNo + fileExtn)
        }
        val outfilePath = dest.path
        Thread {
            //            val buidler = LocalMediaConfig.Buidler()
            //            val config = buidler.setVideoPath(filePath).captureThumbnailsTime(1)
            //                .doH264Compress(AutoVBRMode(30)).setFramerate(0).setScale(0f).build()
            //            val localMediaCompress = LocalMediaCompress(config)
            //            val startCompress = localMediaCompress.startCompress()

            var success = true
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(act, filePath)
                val originWidth =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
                        .toInt()
                val originHeight =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                        .toInt()
                val bitrate =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)!!
                        .toInt()
                val outWidth = originWidth / 2
                val outHeight = originHeight / 2
                VideoProcessor.processor(applicationContext).input(filePath)
                    .output(outfilePath).outWidth(outWidth).outHeight(outHeight)
                    //                    .bitrate(bitrate / 4)
                    .frameRate(30).progressListener {
                        runOnUiThread {
                            loadingPop?.dataBind?.progressBar?.progress = (it * 100).toInt()
                        }
                    }.process()
            } catch (e: Exception) {
                UMCrash.generateCustomLog(e, "UmengException")
                FileUtils.deleteAllInDir(outPutPath)
                success = false
                stop("视频压缩错误")
                e.printStackTrace()
                return@Thread
            }
            runOnUiThread { loadingPop?.setMsg("上传中...") }
            val buidler = LocalMediaConfig.Buidler()
            val config = buidler.setVideoPath(outfilePath).captureThumbnailsTime(1)
                .doH264Compress(AutoVBRMode(0)).setFramerate(0).setScale(0f).build()
            val localMediaCompress = LocalMediaCompress(config)
            val startCompress = localMediaCompress.startCompress()
            if (success && startCompress.isSucceed) {
                var type = "nswd"
                var addUrl = Urls.saveVodUrl
                var videoPath = startCompress.videoPath
                when (businessType) {
                    ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER -> {
                        type = ""
                        addUrl = Urls.savelylxVodUrl
                        videoPath = filePath.path
                    }
                    ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                        type = ""
                        addUrl = Urls.saveyxLgfkLylxVodUrl
                    }
                    else -> {
                        type = "nswd"
                        addUrl = Urls.saveVodUrl
                        videoPath = act?.mContext?.let {
                            if (UriUtils.uri2File(filePath)
                                    .length() > 70 * 1024 * 1024
                            ) {
                                startCompress.videoPath
                            } else {
                                UriUtils.uri2File(filePath).absolutePath
                            }
                        }
                    }
                }

                DataCtrlClass.uploadFiles(
                    act,
                    keyId,
                    type,
                    arrayListOf(FileUtils.getFileByPath(videoPath))
                ) { urls ->
                    if (urls != null) {
                        DataCtrlClass.SXDCNet.saveVodUrl(
                            act,
                            addUrl,
                            keyId,
                            urls[0].filePath ?: ""
                        ) {
                            runOnUiThread {
                                loadingPop?.dismiss()
                            }
                            if (it != null) {
                                val url = urls[0].filePath ?: ""
                                //                                            initView()
                                runOnUiThread {
                                    RxBus.get().post("refreshVod", url)
                                }
                                //上传完自杀
                                manager?.cancel(NOTIFY_ID)
                                close()
                            } else {
                                stop("视频保存错误")
                            }
                        }
                        FileUtils.deleteAllInDir(act?.getExternalFilesDir(Environment.DIRECTORY_MOVIES))
                    } else {
                        FileUtils.deleteAllInDir(act?.getExternalFilesDir(Environment.DIRECTORY_MOVIES))
                        stop("视频上传错误")
                    }

                }
            } else {
                //错误自杀
                runOnUiThread {
                    loadingPop?.dismiss()
                    manager?.cancel(NOTIFY_ID)
                }
                stop("压缩失败")
            }
            //            if (startCompress.isSucceed) {
            //                DataCtrlClass.uploadFiles(act, "creditId", "nswd", arrayListOf(FileUtils.getFileByPath(startCompress.videoPath))) { urls ->
            //                    if (urls != null) {
            //                        DataCtrlClass.SXDCNet.saveVodUrl(act, creditId, urls[0].filePath ?: "") {
            //                            runOnUiThread {
            //                                loadingPop?.dismiss()
            //                            }
            //                            if (it != null) {
            //                                val url = urls[0].filePath ?: ""
            //                                //                                            initView()
            //                                runOnUiThread {
            //                                    RxBus.get().post("refreshVod", url)
            //                                }
            //                                //上传完自杀
            //                                manager?.cancel(NOTIFY_ID)
            //                                close()
            //                            }else{
            //                                stop("视频保存错误")
            //                            }
            //                        }
            //
            //                    } else {
            //                        runOnUiThread {
            //                            loadingPop?.dismiss()
            //                        }
            //                        stop("视频上传错误")
            //                    }
            //
            //                }
            //            } else {
            //                //错误自杀
            //                runOnUiThread {
            //                    loadingPop?.dismiss()
            //                    manager?.cancel(NOTIFY_ID)
            //                }
            //                stop("压缩失败")
            //            }


        }.start()


    }

    private fun stop(contentText: String) {
        runOnUiThread {
            loadingPop?.dismiss()
        }
        notifyBuilder?.setContentTitle(resources.getString(R.string.app_name))
            ?.setContentText(contentText)
        val notification = notifyBuilder?.build()
        notification?.flags = Notification.FLAG_AUTO_CANCEL
        manager?.notify(NOTIFY_ID, notification)
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
    inner class ZipBinder : Binder() {
        /**
         * 开始下载
         */
        fun start(act: BaseActivity?, filePath: Uri, keyId: String, businessType: Int) {
            loadingPop = LoadingPop(act)
            //初始化通知栏
            showNotification()
            //下载
            startZip(act, filePath, keyId, businessType)
        }
    }


    companion object {
        private const val NOTIFY_ID = 0
        private val TAG: String = ZipService::class.java.simpleName
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
            val intent = Intent(context, ZipService::class.java)
            context.startService(intent)
            context.bindService(intent, connection, BIND_AUTO_CREATE)
            isRunning = true
        }
    }
}