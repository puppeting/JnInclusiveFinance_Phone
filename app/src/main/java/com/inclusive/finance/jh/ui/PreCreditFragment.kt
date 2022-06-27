package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentPrecreditListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 预授信列表
 * */
class PreCreditFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentPrecreditListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentPrecreditListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@PreCreditFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "预授信列表"


        mAdapter = ItemBaseListAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initStatusView()
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

    val lcztList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        lcztList.clear()
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "流程中"
            keyName = "流程中"
        })
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "待处理"
            keyName = "待处理"
        })
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "已完成"
            keyName = "已完成"
        })
        lcztList.add(BaseTypeBean.Enum12().apply {
            valueName = "已终止"
            keyName = "已终止"
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
        viewBind.downLczt.text = "待处理"
        viewBind.downLczt.setOnClickListener(listener)
    }

    private var processStatus = "待处理"
    override fun initData() {

        DataCtrlClass.VisitNet.getPreCreditList(requireActivity(), currentPage, viewBind.etSearch.text.toString(), processStatus) {
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
        if (v ==  viewBind.chipNew) {
            ApplyCheckPop(context, this, businessType = ApplyModel.BUSINESS_TYPE_PRECREDIT) {
                refreshData()
                IRouter.goF(v, R.id.action_to_applyActivity, it, ApplyModel.BUSINESS_TYPE_PRECREDIT, false)
            }.show(childFragmentManager, this.javaClass.name)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")
            val currentLink = SZWUtils.getJsonObjectString(jsonObject, "currentLink")
            when (v) {
                viewBind.chipEdit -> {
                    when {
                        jsonObjectString.contains("1") &&currentLink=="申请"-> IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_PRECREDIT, false)
                        else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                    }

                }


                viewBind.chipSee -> {
                    IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_PRECREDIT, true)
                }
                viewBind.chipCheckProgress -> {
                    CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = "6",businessType = ApplyModel.BUSINESS_TYPE_PRECREDIT).show(childFragmentManager,this.javaClass.name)
                }
                viewBind.chipSubmit -> {
                    when {
                        jsonObjectString.contains("0") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                        else -> DataCtrlClass.SXSPNet.getSXSPById(context,keyId =  SZWUtils.getJsonObjectString(jsonObject, "id"),businessType = ApplyModel.BUSINESS_TYPE_PRECREDIT, type = "6") { configurationBean ->
                            if (configurationBean != null) {
                                ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"),businessType = ApplyModel.BUSINESS_TYPE_PRECREDIT) {
                                    viewBind.mRefreshLayout.autoRefresh()
                                }.show(childFragmentManager,this.javaClass.name)
                            }
                        }
                    }
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