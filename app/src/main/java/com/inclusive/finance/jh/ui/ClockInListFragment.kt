package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentClockInListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 打卡列表
 * */
class ClockInListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentClockInListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentClockInListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@ClockInListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "打卡列表"


        mAdapter = ItemBaseListAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        //        val mainData = SZWUtils.getJson(context, "listData.json")
        //        val data = Gson().fromJson<BaseListBean>(mainData, BaseListBean::class.java)
        //        adapter.titleList = data.titleList
        //        adapter.setNewInstance(data.list)

        //                val mainData = SZWUtils.getJson(context, "待办事项.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //                mAdapter.titleList = list[0].listBean?.titleList
        //                mAdapter.setNewInstance(list[0].listBean?.list)
    }
    override fun initData() {

        DataCtrlClass.VisitNet.getClockInList(requireActivity(), currentPage, viewBind.etZhmc.text.toString(), viewBind.etKhjl.text.toString()) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it) {
                        mAdapter.setNewInstance(it.list)
                    }
                } else {
                    mAdapter.addData(it.list)

                }
                if (!it.list.isNullOrEmpty()) {
                    viewBind.mRefreshLayout.finishLoadMore()
                    currentPage++
                } else {
                    viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
                }
            } else {
                viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
            }

        }
    }
    /**
     * 初始化定位参数配置
     */

    private fun initLocationOption(list: ArrayList<BaseTypeBean>) {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        val locationClient = LocationClient(context)
        //声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        val myLocationListener = MyLocationListener{
            list.firstOrNull { item -> item.dataKey == "clockinLocation" }?.valueName =it.addrStr
            list.firstOrNull { item -> item.dataKey == "clockinLongitude" }?.valueName =it.longitude.toString()
            list.firstOrNull { item -> item.dataKey == "clockinLatitude" }?.valueName =it.latitude.toString()
            list.firstOrNull { item -> item.dataKey == "clockoffLocation" }?.valueName =it.addrStr
            list.firstOrNull { item -> item.dataKey == "clockoffLongitude" }?.valueName =it.longitude.toString()
            list.firstOrNull { item -> item.dataKey == "clockoffLatitude" }?.valueName =it.latitude.toString()
        }
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
    class MyLocationListener(var listener:(location:BDLocation)->Unit) : BDAbstractLocationListener() {
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
                listener.invoke(location)
                Log.v("locationRegister", "定位成功:${location.locTypeDescription}$errorCode")
            } else {
                // 定位失败
                Log.v("locationRegister", "定位失败:${location.locTypeDescription}$errorCode")
            }
        }
    }

    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>,rootView:View) -> Unit = { adapter, it, view->
        initLocationOption(it)
    }

    override fun refreshData(type: Int?) {
        viewBind.mRefreshLayout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        viewBind.root.postDelayed({ initData() }, 0)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun onClick(v: View?) {

        //        PictureSelector.create(this@ApplyListFragment).externalPictureVideo("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4");

        if (v == viewBind.btSearch) {
            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                viewBind.mRefreshLayout.autoRefresh()
                mLastClickTime = System.currentTimeMillis()
            }
            return
        }
        if (v ==  viewBind.chipNew) {
            BaseTypePop(context, this, "新增", getUrl = Urls.get_clockin_pop_new, saveUrl = Urls.save_clockin_pop_new,subscribe =subscribe ) {adapter,resultStr->
                refreshData()
                if (isAdded)
                    (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")
            val currentLink = SZWUtils.getJsonObjectString(jsonObject, "currentLink")
            when (v) {
                viewBind.chipEdit -> {
                    BaseTypePop(context, this, "编辑", getUrl = Urls.get_clockin_pop_edit, saveUrl = Urls.save_clockin_pop_edit,jsonObject,keyId = SZWUtils.getJsonObjectString(jsonObject, "id") ,subscribe =subscribe) {adapter,resultStr->
                        viewBind.mRefreshLayout.autoRefresh()
                    }.show(childFragmentManager, this.javaClass.name)
                }
                viewBind.chipEnd -> {
                    BaseTypePop(context, this, "结束", getUrl = Urls.get_clockin_pop_end, saveUrl = Urls.save_clockin_pop_end,jsonObject,keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), subscribe =subscribe) {adapter,resultStr->
                        viewBind.mRefreshLayout.autoRefresh()
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }
        }

    }

    /**
    返回后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }


}