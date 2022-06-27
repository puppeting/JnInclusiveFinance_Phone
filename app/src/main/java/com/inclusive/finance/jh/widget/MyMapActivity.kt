package com.inclusive.finance.jh.widget

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.hwangjr.rxbus.RxBus
 import com.inclusive.finance.jh.adapter.ItemMapTextAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.permissionLocationWithPermissionCheck
import com.inclusive.finance.jh.databinding.ActivityMapBinding
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.tencent.smtt.sdk.*
import java.util.*
import kotlin.math.abs
import com.inclusive.finance.jh.R


@Route(path = "/com/MyMapActivity")
class MyMapActivity : BaseActivity(), SensorEventListener, OnMapLoadedCallback {

    @Autowired
    @JvmField
    var position = 0

    private var mMapView: MapView? = null
    private var mBaiduMap: BaiduMap? = null
    private var isFirstLoc = true
    private var lastX = Float.MIN_VALUE
    private var mCurrentDirection = 0f
    private var mCurrentLat = 0.0
    private var mCurrentLon = 0.0
    private var myLocationData: MyLocationData? = null
    private var mCurrentAccracy = 0f
    private var mSensorManager: SensorManager? = null
    private var mLocationClient: LocationClient? = null
    private var mAdapter: ItemMapTextAdapter<HashMap<String, String>>? = null
    private var mBehavior: BottomSheetBehavior<*>? = null
    private var mLatLng: LatLng? = null
    private val mBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding)
    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.toolbar.title = "地图选址"
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    lateinit var viewBind: ActivityMapBinding
    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityMapBinding>(this, R.layout.activity_map)
            .apply {
                mMapView = mapView
                mBaiduMap = mapView.map
                lifecycleOwner = this@MyMapActivity
            }

    }

    /**
     * 强制帮用户打开GPS
     */
    private fun openGPS() {
        AlertDialog.Builder(this).setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setTitle("提示")
            .setMessage("没有开启定位").setNegativeButton("取消"){ _, _ ->
                finish()
            }
            .setPositiveButton("设置") { dialogInterface, _ ->
                val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 887)
                dialogInterface.dismiss()
            }.show()
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    private fun isLocServiceEnable(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }

    override fun init() {
        permissionLocationWithPermissionCheck {
            if (isLocServiceEnable(this)) {
                // 开启定位图层
                mBaiduMap?.isMyLocationEnabled = true
                val myLocationConfiguration = MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null)
                // 设置定位图层配置信息
                mBaiduMap?.setMyLocationConfiguration(myLocationConfiguration)
                // 获取传感器管理服务
                mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                // 为系统的方向传感器注册监听器
                mSensorManager?.registerListener(this, mSensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI)

                // 定位初始化
                mLocationClient = LocationClient(this)
                mLocationClient?.registerLocationListener(mListener)
                val locationClientOption = LocationClientOption()
                // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
                locationClientOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
                // 可选，设置返回经纬度坐标类型，默认GCJ02
                locationClientOption.setCoorType("bd09ll")
                // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
                // 如果设置非0，需设置1000ms以上才有效
                locationClientOption.setScanSpan(1000)
                //可选，设置是否使用gps，默认false
                locationClientOption.isOpenGps = true
                // 可选，是否需要地址信息，默认为不需要，即参数为false
                // 如果开发者需要获得当前点的地址信息，此处必须为true
                locationClientOption.setIsNeedAddress(true)
                // 可选，默认false，设置是否需要POI结果，可以在BDLocation
                locationClientOption.setIsNeedLocationPoiList(true)
                // 设置定位参数
                mLocationClient?.locOption = locationClientOption
                // 开启定位
                mLocationClient?.start()
            } else {
                openGPS()
            }

        }
        initBottomSheet()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mAdapter = ItemMapTextAdapter()
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter?.setOnItemClickListener { _, _, position ->
            RxBus.get().post("location", mAdapter?.data?.get(position))
            finish()
        }
    }

    private val mListener: BDAbstractLocationListener = object : BDAbstractLocationListener() {
        /**
         * 定位请求回调函数
         *
         * @param location 定位结果
         */
        override fun onReceiveLocation(location: BDLocation?) {
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return
            }
            if (!isLocServiceEnable(this@MyMapActivity)) {
                // 取消注册传感器监听
                mLocationClient?.unRegisterLocationListener(this)
                openGPS()
            }
            mAdapter?.setNewInstance(arrayListOf(HashMap<String, String>().apply {
                put("position", position.toString())
                put("latLng", "${location.latitude},${location.longitude}")
                val name = location.poiRegion?.name?:""
                put("name", name)
                put("address", (location.addrStr ?: "" )+ name)
            }))
            mCurrentLat = location.latitude
            mCurrentLon = location.longitude
            mCurrentAccracy = location.radius
            myLocationData = MyLocationData.Builder().accuracy(mCurrentAccracy) // 设置定位数据的精度信息，单位：米
                .direction(mCurrentDirection) // 此处设置开发者获取到的方向信息，顺时针0-360
                .latitude(mCurrentLat).longitude(mCurrentLon).build()
            mBaiduMap?.setMyLocationData(myLocationData)
            if (location.locType == BDLocation.TypeGpsLocation || location.locType == BDLocation.TypeNetWorkLocation || location.locType == BDLocation.TypeOffLineLocation) {
                //                if (isFirstLoc) {
                isFirstLoc = false
                mLatLng = LatLng(location.latitude, location.longitude)
                //                    addMarker(mLatLng)
                val builder = MapStatus.Builder()
                builder.target(mLatLng).zoom(18.0f)
                mBaiduMap?.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                //                }
            }
        }
    }

    /**
     * 初始化 BottomSheet 控件
     */
    private fun initBottomSheet() {
        mBehavior = BottomSheetBehavior.from(viewBind.bottomSheet)
        mBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (null == mLatLng) {
                            return
                        }
                        setBounds(mLatLng, mBehavior?.peekHeight)
                        viewBind.imageView.setImageResource(R.mipmap.showout)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (null == mLatLng) {
                            return
                        }
                        setBounds(mLatLng, viewBind.bottomSheet.height)
                        viewBind.imageView.setImageResource(R.mipmap.showin)
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })
        mBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        mBehavior?.isDraggable = false
        viewBind.imageView.post {
            //            val height: Int = viewBind.imageView.drawable.bounds.height()
            mBehavior?.peekHeight = SizeUtils.dp2px(80f)
            // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
            mBaiduMap?.setViewPadding(0, 0, 0, SizeUtils.dp2px(80f))
        }
        viewBind.imageView.setOnClickListener {
            if (mBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED) {
                mBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else if (mBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                mBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    /**
     * 添加Marker
     */
    private fun addMarker(mLatLng: LatLng?) {
        val overlayOptions = ArrayList<OverlayOptions>()
        val markerOptions = MarkerOptions().position(mLatLng).icon(mBitmap) // 设置 Marker 覆盖物的图标
            .zIndex(9)
        overlayOptions.add(markerOptions)
        // 设置 marker 覆盖物的 zIndex
        mBaiduMap?.addOverlays(overlayOptions)
    }

    /**
     * 最佳视野内显示所有点标记
     */
    private fun setBounds(mLatLng: LatLng?, paddingBottom: Int?) {
        val padding = 80
        // 构造地理范围对象
        val builder = LatLngBounds.Builder()
        // 让该地理范围包含一组地理位置坐标
        builder.include(mLatLng)
        // 设置显示在指定相对于MapView的padding中的地图地理范围
        val mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding, padding, padding, paddingBottom
            ?: 0)

        // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
        mBaiduMap?.setViewPadding(0, 0, 0, paddingBottom ?: 0)
        // 更新地图
        mBaiduMap?.setMapStatus(mapStatusUpdate)
    }

    /**
     * 传感器方向信息回调
     */
    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val x: Float = sensorEvent?.values?.get(SensorManager.DATA_X) ?: Float.MIN_VALUE
        if (abs(x - lastX) > 1.0) {
            mCurrentDirection = x
            // 构造定位图层数据
            myLocationData = MyLocationData.Builder()
                .accuracy(mCurrentAccracy) // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build()
            // 设置定位图层数据
            mBaiduMap?.setMyLocationData(myLocationData)
        }
        lastX = x
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册传感器监听
        mSensorManager?.unregisterListener(this)
        // 退出时销毁定位
        mLocationClient?.stop()
        // 关闭定位图层
        mBaiduMap?.isMyLocationEnabled = false
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onMapLoaded() {
        if (null == mLatLng || null == mBehavior) {
            return
        }
        setBounds(mLatLng, mBehavior?.peekHeight)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 887) {
            init()
        }
    }
}
