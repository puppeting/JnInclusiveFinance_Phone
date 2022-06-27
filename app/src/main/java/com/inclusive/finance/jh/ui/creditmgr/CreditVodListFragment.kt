package com.inclusive.finance.jh.ui.creditmgr

import android.content.ComponentName
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ObjectUtils
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.base.permissionCameraWithPermissionCheck
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCreditVodListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.service.ZipService
import com.inclusive.finance.jh.ui.ApplyActivity
import com.inclusive.finance.jh.utils.SZWUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 录音录像列表
 * */
class CreditVodListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditVodListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var getUrl = ""
    var getPopUrl = ""
    var savePopUrl = ""
    var deletePopUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(ApplyModel::class.java)

        viewBind = FragmentCreditVodListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@CreditVodListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this) //        val mainData = SZWUtils.getJson(context, "listData.json")
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
        initStatusView()
    }

    val lcztList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        lcztList.clear()
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "是"
            keyName = "1"
        })
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "否"
            keyName = "0"
        })
        val listener: (v: View) -> Unit = {
            DownPop(context, enums12 = when (it) {
                viewBind.downLczt -> lcztList
                else -> arrayListOf()
            }, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                when (it) {
                    viewBind.downLczt -> processStatus = k
                }
            }.showPopupWindow(it)
        }
        viewBind.downLczt.text = "全部"
        viewBind.downLczt.setOnClickListener(listener)
    }

    private var processStatus = ""
    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER -> {
                getUrl = Urls.get_yxLylx_list
                getPopUrl = Urls.get_yxLylx_pop_new
                savePopUrl = Urls.save_yxLylx_pop_new
                deletePopUrl = Urls.delete_yxLylx_pop_new
            }
            else -> {
                getUrl = ""
                getPopUrl = ""
                savePopUrl = ""
                deletePopUrl = ""
            }
        }
        DataCtrlClass.CreditManagementNet.getLYLXList(requireActivity(), getUrl, keyId = viewModel.keyId?:"",currentPage, viewBind.etSearch.text.toString(), processStatus) {
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
        if (v == viewBind.chipNew) {
            BaseTypePop(context, this, "新增", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId) {adapter,resultStr->
                refreshData()
                if (isAdded) (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val videoPath = SZWUtils.getJsonObjectString(jsonObject, "videoPath")
            when (v) {
                viewBind.chipRecord -> {
                    if (ZipService.isRunning) {
                        context?.let { SZWUtils.showSnakeBarMsg("有一项未完成的任务，请稍后重试") }
                        return@getJsonObjectBeanFromList
                    }
                    if (videoPath.isNotEmpty()) {
                        ConfirmPop(context, "注意，重新录制将会覆盖") {
                            if (it) {
                                recodeVideo(jsonObject)

                            }
                        }.show(childFragmentManager, this.javaClass.name)
                    } else {
                        recodeVideo(jsonObject)
                    }
                }
                viewBind.chipDisplay -> {
                    ARouter.getInstance().build("/com/SimplePlayer").withString("url", videoPath)
                        .withString("thumbImage", "https://i0.hdslb.com/bfs/sycp/creative_img/202104/d8920062ffd4c581b4d8f792c5327306.jpg")
                        .navigation()
                }
                viewBind.chipDelete -> {
                    ConfirmPop(context, "确定删除?") {
                        if (it) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deletePopUrl, SZWUtils.getJsonObjectString(jsonObject, "id"), keyId = viewModel.keyId) {
                            refreshData()
                            if (isAdded) (activity as BaseActivity).refreshData()
                        }
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }
        }

    }

    private fun recodeVideo(jsonObject: JsonObject) {
        (act as BaseActivity).permissionCameraWithPermissionCheck(null, 200, false) { //            recordVideo(act,60,0)
            // 录制
            isAdded
            PictureSelector.create(this).openCamera(PictureMimeType.ofVideo()).maxSelectNum(1)
                .isCamera(true).recordVideoSecond(60)
                .videoMaxSecond(60) //                .isUseCustomCamera(true)
                //                .setButtonFeatures(CustomCameraView.BUTTON_STATE_ONLY_RECORDER)//自定相机是否单独拍照、录像
                //            .isPreviewVideo(true)//是否预览视频
                .isSingleDirectReturn(true) //PictureConfig.SINGLE模式下是否直接返回
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: List<LocalMedia?>) {
                        if (ObjectUtils.isEmpty(result) || result.isEmpty()) {
                            return


                        }
                        var picturePath: String? = result[0]?.compressPath
                        if (picturePath == null) {
                            picturePath = result[0]?.realPath
                        }
                        if (picturePath == null) {
                            picturePath = result[0]?.path
                        }


                        ZipService.bindService(act.applicationContext, object : ServiceConnection {
                            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                                val binder = (service as ZipService.ZipBinder)
                                binder.start(act as ApplyActivity, Uri.parse(picturePath), SZWUtils.getJsonObjectString(jsonObject, "id"), viewModel.businessType)
                            }

                            override fun onServiceDisconnected(name: ComponentName) {}
                        })

                    }

                    override fun onCancel() {

                    }
                })
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag("refreshVod")))
    fun refreshVod(url: String) {
        if (isAdded) (activity as BaseActivity).refreshData()
        refreshData()
    }

    /**
    返回后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }


}