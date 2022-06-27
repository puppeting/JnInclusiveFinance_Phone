package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.databinding.FragmentTrackBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.inclusive.finance.jh.utils.clusterutil.clustering.ClusterItem
import com.inclusive.finance.jh.utils.clusterutil.clustering.ClusterManager
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 *点聚合
 * */
class PointClusterFragment : MyBaseFragment() {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentTrackBinding

    // 地图View实例
    private var mMapView: MapView? = null

    private var mBaiduMap: BaiduMap? = null

    private var mClusterManager: ClusterManager<MyItem>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentTrackBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "聚合" //构建折线点坐标

        initMap()
    }

    private fun initMap() {
        mMapView = viewBind.mapView
        if (null == mMapView) {
            return
        }
        mBaiduMap = mMapView?.map
        if (null == mBaiduMap) {
            return
        }
        mBaiduMap?.setViewPadding(30, 0, 30, 20)
        mBaiduMap?.setOnMapLoadedCallback(OnMapLoadedCallback { // 添加marker
            initCluster()
            DataCtrlClass.MainNet.get_main_cluster_list(context){
                if (!it.isNullOrEmpty()){
                    //设置折线的属性
                    val points: MutableList<LatLng> = ArrayList()
                    val items: MutableList<MyItem> = ArrayList<MyItem>()
                    it.forEach {jsonObject->

                        //定义Maker坐标点
                        val point = LatLng(SZWUtils.getJsonObjectString(jsonObject,"lat").toDouble(), SZWUtils.getJsonObjectString(jsonObject,"lng").toDouble()) //构建Marker图标
                        points.add(point)
                        items.add(MyItem(point))
//                        //构建Marker图标
//                        val bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.map_point) //构建MarkerOption，用于在地图上添加Marker
//                        //构建MarkerOption，用于在地图上添加Marker
//                        val option: OverlayOptions = MarkerOptions().position(point)
//                            .icon(bitmap) //在地图上添加Marker，并显示
//                        //                       .animateType(MarkerOptions.MarkerAnimateType.jump)
//                        //在地图上添加Marker，并显示
//                        viewBind.mapView.map.addOverlay(option)

                    } //定义Maker坐标点
                    /**
                     * 向地图添加Marker点
                     */
                    mClusterManager?.addItems(items)
                    setBounds(points)

                }
            }

            // 设置初始中心点为北京
            val center = LatLng(39.963175, 116.400244)
            val mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(center, 10f)
            mBaiduMap?.setMapStatus(mapStatusUpdate)
        })
    }

    private fun initCluster() { // 定义点聚合管理类ClusterManager
        mClusterManager = ClusterManager<MyItem>(context, viewBind.mapView.map)
        viewBind.mapView.map.setOnMapStatusChangeListener(mClusterManager)
        viewBind.mapView.map.setOnMarkerClickListener(mClusterManager)
        mClusterManager?.setOnClusterClickListener { cluster ->
            Toast.makeText(context, "有" + cluster.getSize().toString() + "个点", Toast.LENGTH_SHORT)
                .show()
            false
        }
        mClusterManager?.setOnClusterItemClickListener {
            Toast.makeText(context, "点击单个Item", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * 最佳视野内显示所有点标记
     */
    fun setBounds(mLatLngs: List<LatLng>?, paddingBottom: Int? = 0) {
        val padding = 80 // 构造地理范围对象
        val builder = LatLngBounds.Builder() // 让该地理范围包含一组地理位置坐标
        builder.include(mLatLngs) // 设置显示在指定相对于MapView的padding中的地图地理范围
        val mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding, padding, padding, paddingBottom
            ?: 0)

        // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
        viewBind.mapView.map?.setViewPadding(0, 0, 0, paddingBottom ?: 0) // 更新地图
        viewBind.mapView.map?.setMapStatus(mapStatusUpdate)
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    class MyItem internal constructor(var index: LatLng) : ClusterItem {
        override fun getPosition() = index
        override fun getBitmapDescriptor() =
            BitmapDescriptorFactory.fromResource(R.mipmap.map_point)

    }
}