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
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentJinianjiancaijiListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 季年检采集列表
 * */
class JiNianJianCaiJiListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentJinianjiancaijiListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)
        viewBind = FragmentJinianjiancaijiListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@JiNianJianCaiJiListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "数据采集"
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


    val khlxList = ArrayList<BaseTypeBean.Enum12>()
    val jclxList = ArrayList<BaseTypeBean.Enum12>()
    var rwmcList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        DataCtrlClass.JNJNet.getJNJCJRWMC(requireActivity()){
            if (it!=null){
                rwmcList = it
                if (it.size > 0) {
                    status_rwmc = it[0].keyName
                    viewBind.downRwmc.text = it[0].valueName
                }
                viewBind.mRefreshLayout.autoRefresh()
            }
        }
        khlxList.clear()
        jclxList.clear()
        rwmcList.clear()
        khlxList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        khlxList.add(BaseTypeBean.Enum12().apply {
            valueName = "对私"
            keyName = "1"
        })
        khlxList.add(BaseTypeBean.Enum12().apply {
            valueName = "对公"
            keyName = "2"
        })
        jclxList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        jclxList.add(BaseTypeBean.Enum12().apply {
            valueName = "非现场"
            keyName = "01"
        })
        jclxList.add(BaseTypeBean.Enum12().apply {
            valueName = "现场"
            keyName = "02"
        })
        jclxList.add(BaseTypeBean.Enum12().apply {
            valueName = "现场(非现场转现场)"
            keyName = "03"
        })
        val listener: (v: View) -> Unit = {
            DownPop(context, enums12 = when (it) {
                viewBind.downKhlx -> khlxList
                viewBind.downJclx -> jclxList
                viewBind.downRwmc -> rwmcList
                else -> arrayListOf()
            }, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                when (it) {
                    viewBind.downKhlx -> status_khlx = k
                    viewBind.downJclx -> status_jclx = k
                    viewBind.downRwmc -> status_rwmc = k
                }
            }.showPopupWindow(it)
        }
        viewBind.downKhlx.text="全部"
        viewBind.downJclx.text="全部"
        viewBind.downKhlx.setOnClickListener(listener)
        viewBind.downJclx.setOnClickListener(listener)
        viewBind.downRwmc.setOnClickListener(listener)
    }

    private var status_khlx = ""
    private var status_jclx = ""
    private var status_rwmc = ""
    override fun initData() {

        DataCtrlClass.JNJNet.getJNJCJList(requireActivity(), currentPage, viewBind.etSearch.text.toString(), status_khlx, status_jclx, status_rwmc) {
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
//        viewBind.mRefreshLayout.autoRefresh()
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
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val custType = SZWUtils.getJsonObjectString(jsonObject, "custType")
            val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")
            when (v) {
                viewBind.chipCj -> {
                    when {
                        jsonObjectString.contains("1") -> IRouter.goF(v, R.id.action_to_applyActivity,SZWUtils.getJsonObjectString(jsonObject, "dhId"), if (custType=="对私")ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL else ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY, false)
                        else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                    }

                }
                viewBind.chipSeeCj -> {
                    IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "dhId"), if (custType=="对私")ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL else ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY, true)
                }
                viewBind.chipSubmit -> {
                    when {
                        jsonObjectString.contains("1") ->ConfirmPop(context, "确认提交?") {
                            if (it) {
                                DataCtrlClass.JNJNet.submit(requireActivity(), Urls.get_jnj_cj_submit,SZWUtils.getJsonObjectString(jsonObject, "dhId")) {
                                    onRefresh(viewBind.mRefreshLayout)
                                }
                            }
                        }.show(childFragmentManager,this.javaClass.name)
                        else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                    }

                }
                viewBind.chipGhqyj->{
                    GHQYJPop(context,jsonObject).show(childFragmentManager, this.javaClass.name)
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