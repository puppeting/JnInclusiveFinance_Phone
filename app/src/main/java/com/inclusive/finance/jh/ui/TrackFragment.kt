package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.databinding.FragmentTrackBinding
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 轨迹
 * */
class TrackFragment : MyBaseFragment(){
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentTrackBinding
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
        viewBind.actionBarCustom.mTitle.text = "轨迹" //构建折线点坐标
        viewBind.mapView.map.isMyLocationEnabled = true
    }

    override fun initData() {
        DataCtrlClass.MainNet.get_main_track_list(requireActivity()){
                if (!it.isNullOrEmpty()){
                    //设置折线的属性
                    val points: MutableList<LatLng> = ArrayList()
                    it.forEach {jsonObject->
                        points.add(LatLng(SZWUtils.getJsonObjectString(jsonObject,"lat").toDouble(), SZWUtils.getJsonObjectString(jsonObject,"lng").toDouble()))
                    }
                    if (points.size<2){
                        SZWUtils.showSnakeBarMsg("暂无轨迹")
                        return@get_main_track_list
                    }
                    val mOverlayOptions: OverlayOptions = PolylineOptions().width(10).color(-0x55010000)
                        .points(points) //在地图上绘制折线
                    //mPloyline 折线对象
                    //在地图上绘制折线
                    //mPloyline 折线对象
                    val mPolyline: Overlay = viewBind.mapView.map.addOverlay(mOverlayOptions)



                    //定义Maker坐标点
                    val startBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.map_point_start) //构建MarkerOption，用于在地图上添加Marker
                    val endBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.map_point_end) //构建MarkerOption，用于在地图上添加Marker
                    //构建MarkerOption，用于在地图上添加Marker
                    val optionStrat: OverlayOptions = MarkerOptions().position(points.first()).icon(startBitmap) //在地图上添加Marker，并显示
                    //在地图上添加Marker，并显示
                    viewBind.mapView.map.addOverlay(optionStrat)
                    //构建MarkerOption，用于在地图上添加Marker
                    val optionEnd: OverlayOptions = MarkerOptions().position(points.last()).icon(endBitmap) //在地图上添加Marker，并显示
                    //在地图上添加Marker，并显示
                    viewBind.mapView.map.addOverlay(optionEnd)
                    setBounds(points)
                }
        }

    }
    /**
     * 最佳视野内显示所有点标记
     */
    fun setBounds(mLatLngs: List<LatLng>?, paddingBottom: Int?=0) {
        val padding = 80
        // 构造地理范围对象
        val builder = LatLngBounds.Builder()
        // 让该地理范围包含一组地理位置坐标
        builder.include(mLatLngs)
        // 设置显示在指定相对于MapView的padding中的地图地理范围
        val mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding, padding, padding, paddingBottom
            ?: 0)

        // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
        viewBind.mapView.map?.setViewPadding(0, 0, 0, paddingBottom ?: 0)
        // 更新地图
        viewBind.mapView.map?.setMapStatus(mapStatusUpdate)
    }
}