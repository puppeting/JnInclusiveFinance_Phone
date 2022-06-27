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
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentLeaveApprovalListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 请假审批
 * */
class LeaveApprovalListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentLeaveApprovalListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentLeaveApprovalListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@LeaveApprovalListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "请假审批"


        mAdapter = ItemBaseListAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initDownView()
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

    private fun initDownView() {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        viewBind.tvApproveRet.isClickable = true
        dataList.clear()
        //        审批结果(approveRet)：全部 | 通过 | 否决  默认全部/
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "通过"
            keyName = "通过"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "否决"
            keyName = "否决"
        })
        viewBind.tvApproveRet.text = "全部"
        viewBind.tvApproveRet.setOnClickListener {
            DownPop(context, enums12 = dataList, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                tvApproveRet = k
            }.showPopupWindow(it)
        }
    }

    private fun initStatusView() {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        viewBind.tvStatus.isClickable = true
        dataList.clear()
        //        审批状态(status)：全部 | 待审批 | 已审批   默认待审批
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "待审批"
            keyName = "待审批"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "已审批"
            keyName = "已审批"
        })
        viewBind.tvStatus.text = "待审批"
        viewBind.tvStatus.setOnClickListener {
            DownPop(context, enums12 = dataList, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                processStatus = k
            }.showPopupWindow(it)
        }
    }

    var tvApproveRet = ""
    var processStatus = "待审批"
    override fun initData() {
        DataCtrlClass.SystemManagerNet.get_main_qingjia_list(requireActivity(), currentPage, processStatus, tvApproveRet) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                viewBind.chipApproval.visibility=if (it[0].button==true)View.VISIBLE else View.GONE
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it[1].listBean) {
                        mAdapter.setNewInstance(it[1].listBean?.list)
                    }
                } else {
                    mAdapter.addData(it[1].listBean?.list?: arrayListOf())

                }
                if (!it[1].listBean?.list.isNullOrEmpty()) {
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
                refreshData()
                mLastClickTime = System.currentTimeMillis()
            }
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanListFromList(mAdapter.data) { jsonObjects ->
            when (v) {
                viewBind.chipApproval -> {
                    if (SZWUtils.isSingleCheck(jsonObjects))
                    LeaveApprovalPop(context, SZWUtils.getJsonObjectStringList(jsonObjects, "id")[0]) {
                        refreshData()
                    }.show(childFragmentManager,this.javaClass.name)
                }
                viewBind.chipDelete -> {
                    ConfirmPop(context, "确定删除?") {
                        if (it) {
                            DataCtrlClass.SystemManagerNet.delete_main_leaveApprove(context,  SZWUtils.getJsonObjectStringList(jsonObjects, "id")[0]) {
                                viewBind.mRefreshLayout.autoRefresh()
                            }
                        }
                    }.show(childFragmentManager,this.javaClass.name)
                }
                else -> {
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