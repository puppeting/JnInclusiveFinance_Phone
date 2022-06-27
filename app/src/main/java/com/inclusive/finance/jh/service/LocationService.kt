package com.inclusive.finance.jh.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.LocationBean
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.ui.MainActivity


/**
 * 定位服务
 * by  Swain
 * @author Bananv
 */
class LocationService : Service() {
    var notification: Notification? = null
    var manager: NotificationManager? = null
    var notifyBuilder: NotificationCompat.Builder? = null
    private val NOTIFY_ID = 200

    override fun onCreate() {
        super.onCreate()
        //初始化通知栏
        showNotification()
        //初始化定位
        initLocationOption()
    }


    /**
     * 初始化定位参数配置
     */

    private fun initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        val locationClient = LocationClient(applicationContext)
        //声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        val myLocationListener = MyLocationListener(this)
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener)
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(300000)
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = false
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(false)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
//        locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
//        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.locOption = locationOption
        //开始定位
        locationClient.start()
    }

    /**
     * 实现定位回调
     */
    class MyLocationListener(var locationService: LocationService) : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            val latitude = location.latitude
            //获取经度信息
            val longitude = location.longitude
            //获取定位精度，默认值为0.0f
            val radius = location.radius
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            val coorType = location.coorType
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            val errorCode = location.locType
            if (161 == errorCode || 61 == errorCode) {
                // 定位成功
                val intent = Intent()
                intent.action = Constants.Location.INTENT_ACTION_LOCATION
                intent.putExtra(Constants.Location.INTENT_DATA_LOCATION_CITY, location.city)
                intent.putExtra(Constants.Location.INTENT_DATA_LOCATION_LONGITUDE, longitude.toString() + "")
                intent.putExtra(Constants.Location.INTENT_DATA_LOCATION_LATITUDE, latitude.toString() + "")
                locationService.sendBroadcast(intent)
                RxBus.get().register(this)
                val entity = LocationBean()
                entity.city = location.city
                entity.latitude = longitude.toString()
                entity.longitude = latitude.toString()
                RxBus.get().post(Constants.Receiver_Location, entity)
                DataCtrlClass.upLocation(locationService,lon = longitude.toString(),lat = latitude.toString(),address = location.addrStr){
//                    if (it==null){
//                        locationService.stopForeground(true)
//                        locationService.stopSelf()
//                    }
                }
                // 定位失败
                Log.v("locationRegister", "定位成功:${location.locTypeDescription}$errorCode")
            } else {
                // 定位失败
                Log.v("locationRegister", "定位失败:${location.locTypeDescription}$errorCode")
            }
        }
    }
    /**
     * 创建通知
     */
    private fun showNotification() {
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val hangIntent = Intent(this, MainActivity::class.java)
        val hangPendingIntent = PendingIntent.getActivity(this, 1001, hangIntent, PendingIntent.FLAG_IMMUTABLE)
        val CHANNEL_ID = "your_custom_id" //应用频道Id唯一值， 长度若太长可能会被截断，
        val CHANNEL_NAME = "your_custom_name" //最长40个字符，太长会被截断
        notifyBuilder = NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("定位中")
            .setContentText("持续上传位置").setSmallIcon(R.mipmap.app_logo1)
            .setContentIntent(hangPendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.app_logo))
            .setAutoCancel(true)
        notification = notifyBuilder?.build()

        //Android 8.0 以上需包添加渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            manager?.createNotificationChannel(notificationChannel)
        }
//        manager?.notify(NOTIFY_ID, notification)
        startForeground(NOTIFY_ID,notification);
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


}