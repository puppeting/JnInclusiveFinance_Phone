//package com.base.base.receiver
//
//import android.app.Notification
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import androidx.core.app.NotificationCompat
//import android.util.Log
//import android.widget.RemoteViews
//import cn.jpush.android.api.JPushInterface
//import com.base.base.R
//import com.base.base.module.MainActivity
//import com.hwangjr.rxbus.RxBus
//import org.json.JSONException
//import org.json.JSONObject
//
//
///**
// * 自定义接收器
// *
// *
// * 如果不定义这个 Receiver，则：
// * 1) 默认用户会打开主界面
// * 2) 接收不到自定义消息
// */
//class MyReceiver : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        RxBus.get().register(this)
//
//        val bundle = intent.extras?:Bundle()
//        Log.d(TAG, "[MyReceiver] onReceive - " + intent.action + ", extras: " + printBundle(bundle))
//
//        when {
//            JPushInterface.ACTION_REGISTRATION_ID == intent.action -> {
//                val regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID)
//                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId!!)
//                //send the Registration Id to your server...
//
//            }
//            JPushInterface.ACTION_MESSAGE_RECEIVED == intent.action -> {
//                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE)!!)
//                val mNotificationManager = context
//                        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                // 第二步：实例化通知栏构造器NotificationCompat.Builder：
//                val msgIntent = Intent("com.yunmo.mypushdemo.permission.JPUSH_MESSAGE")
//                val mBuilder = NotificationCompat.Builder(context,"JPUSH").setSmallIcon(R.mipmap.ic_launcher)
//
//                // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                mBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.FLAG_ONGOING_EVENT)
//                val rv = RemoteViews(context.packageName,
//                        R.layout.customer_notitfication_layout)
//                mBuilder.setContent(rv)// 设置自定义notification布局
//                rv.setImageViewResource(R.id.icon, R.mipmap.ic_launcher)
//                rv.setTextViewText(R.id.title, context.resources.getString(R.string.app_name))
//                rv.setTextViewText(R.id.text, bundle.getString(JPushInterface.EXTRA_MESSAGE))
//
//                mBuilder.setLargeIcon(BitmapFactory.decodeResource(
//                        context.resources, R.mipmap.ic_launcher))// 设置下拉图标
//                mBuilder.setAutoCancel(false)
//                mBuilder.setTicker(bundle.getString(JPushInterface.EXTRA_MESSAGE))
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
//
//
//                val notification = mBuilder.build()// API
//                val it = Intent(context, NotificationClickReceiver::class.java)
//                val pi = PendingIntent.getBroadcast(context, notification.flags, it, notification.flags)
//                mNotificationManager.cancel(notification.flags)
//                mBuilder.setFullScreenIntent(pi, true)
//                mNotificationManager.notify((System.currentTimeMillis() / 100).toInt(), notification)
//                context.sendBroadcast(msgIntent)
//                //			play(context);
//
//            }
//            JPushInterface.ACTION_NOTIFICATION_RECEIVED == intent.action -> {
//                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知")
//                val notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
//                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId)
//                //			play(context);
//
//            }
//            JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action -> {
//                Log.d(TAG, "[MyReceiver] 用户点击打开了通知")
//
//                //打开自定义的Activity
//                val intent1 = Intent(
//                        context, MainActivity::class.java)
//                intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//                context.startActivity(intent1)
//            }
//            JPushInterface.ACTION_RICHPUSH_CALLBACK == intent.action -> Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA)!!)
//            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
//            JPushInterface.ACTION_CONNECTION_CHANGE == intent.action -> {
//                val connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
//                Log.w(TAG, "[MyReceiver]" + intent.action + " connected state change to " + connected)
//            }
//            else -> Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.action)
//        }
//    }
//
//    companion object {
//        private val TAG = "JPush"
//        //	MediaPlayer mpMediaPlayer;
//        //	protected void play(final Context context){
//        //			if (mpMediaPlayer==null)
//        //			mpMediaPlayer= MediaPlayer.create(context,R.raw.pikaqiu);
//        //		if (mpMediaPlayer.isPlaying())
//        //			mpMediaPlayer.reset();
//        //			mpMediaPlayer.start();
//        //	}
//
//        // 打印所有的 intent extra 数据
//        private fun printBundle(bundle: Bundle): String {
//            val sb = StringBuilder()
//            for (key in bundle.keySet()) {
//                if (key == JPushInterface.EXTRA_NOTIFICATION_ID) {
//                    sb.append("\nkey:" + key + ", value:" + bundle.getInt(key))
//                } else if (key == JPushInterface.EXTRA_CONNECTION_CHANGE) {
//                    sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key))
//                } else if (key == JPushInterface.EXTRA_EXTRA) {
//                    if (bundle.getString(JPushInterface.EXTRA_EXTRA)!!.isEmpty()) {
//                        Log.i(TAG, "This message has no Extra data")
//                        continue
//                    }
//
//                    try {
//                        val json = JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA))
//                        val it = json.keys()
//
//                        while (it.hasNext()) {
//                            val myKey = it.next().toString()
//                            sb.append("\nkey:" + key + ", value: [" +
//                                    myKey + " - " + json.optString(myKey) + "]")
//                        }
//                    } catch (e: JSONException) {
//                        Log.e(TAG, "Get message extra JSON error!")
//                    }
//
//                } else {
//                    sb.append("\nkey:" + key + ", value:" + bundle.getString(key))
//                }
//            }
//            return sb.toString()
//        }
//    }
//
//
//}
