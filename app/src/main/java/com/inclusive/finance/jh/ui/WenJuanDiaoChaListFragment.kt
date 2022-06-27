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
import com.inclusive.finance.jh.databinding.FragmentWenjuandiaochaListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 问券调查列表
 * */
class WenJuanDiaoChaListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentWenjuandiaochaListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentWenjuandiaochaListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@WenJuanDiaoChaListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "问卷调查"


        mAdapter = ItemBaseListAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this) //        mAdapter.itemRecyclerViewBackGroundColor={ item, holder ->
        //            when {
        //                SZWUtils.getJsonObjectBoolean(item, "isCheck") -> R.color.color_main_orangeAlpha
        //                SZWUtils.getJsonObjectString(item, "color")=="1" -> R.color.color_main_redAlpha
        //                holder.adapterPosition % 2 == 0 -> R.color.white
        //                else -> R.color.line2
        //            }
        //        }
        initStatusView()
        DataCtrlClass.YXNet.getRWMC(requireActivity(),Urls.get_wjdc_rw) {
            if (it != null) {
                rwmcList = it //                if (it.size > 0) {
                //                    status_rwmc = it[0].keyName
                //                    viewBind.downRwmc.text = it[0].valueName
                //                }
                //                viewBind.mRefreshLayout.autoRefresh()
            }
        }
    }

    var rwmcList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        rwmcList.clear() //        状态（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
        //        rwmcList.add(BaseTypeBean.Enum12().apply {
        //            valueName = "检验中"
        //            keyName = "01"
        //        })

        val listener: (v: View) -> Unit = {
            DownPop(context, enums12 = when (it) {
                viewBind.downRwmc -> rwmcList
                else -> arrayListOf()
            }, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                when (it) {
                    viewBind.downRwmc -> status_rwmc = k
                }
            }.showPopupWindow(it)
        }
        viewBind.downRwmc.text = ""
        viewBind.downRwmc.setOnClickListener(listener)
    }

    private var status_rwmc = ""
    override fun initData() {
        DataCtrlClass.YXNet.getUnityList(context = requireActivity(),url=Urls.get_wjdc_list, pageNum = currentPage, status_rwmc) {
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
            ConfirmPop(context, "确定生成问卷？") { b ->
                if (b) {
                    DataCtrlClass.YXNet.newWJDC(requireActivity(), Urls.new_wjdc_list, status_rwmc) {
                        if (it != null) {
                            viewBind.mRefreshLayout.autoRefresh()
                        }
                    }
                }
            }.show(childFragmentManager, this.javaClass.name)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val status = SZWUtils.getJsonObjectString(jsonObject, "status")
            val editFlag = SZWUtils.getJsonObjectString(jsonObject, "editFlag")
            val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
            when (v) {
                viewBind.chipDelete -> {
                    when {
                        !editFlag.contains("1") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                        else -> ConfirmPop(context, "确定删除问卷？") { b ->
                            if (b) {
                                DataCtrlClass.YXNet.deleteWJDC(requireActivity(), Urls.delete_wjdc_list, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                    if (it != null) {
                                        viewBind.mRefreshLayout.autoRefresh()
                                    }
                                }
                            }
                        }.show(childFragmentManager, this.javaClass.name)
                    }

                }
                viewBind.chipStart -> {
                    when {
                        !status.contains("调查中") || !editFlag.contains("1") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                        else -> IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE, false)
                    }
                }
                viewBind.chipSee -> {
                    IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE, true)
                }
                viewBind.chipCheckProgress -> {
                    CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = SZWUtils.getBusinessType(ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE), businessType = ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE).show(childFragmentManager, this.javaClass.name)
                }
                viewBind.chipSubmit -> {
                    when {
                        !flag.contains("1") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                        else -> DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE, type = SZWUtils.getBusinessType(ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE)) { configurationBean ->
                            if (configurationBean != null) {
                                ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE) {
                                    viewBind.mRefreshLayout.autoRefresh()
                                }.show(childFragmentManager, this.javaClass.name)
                            }
                        }
                    }

                }

                viewBind.chiphz -> {
                    IRouter.goF(v, R.id.action_to_navActivity, "问卷汇总", SZWUtils.getJsonObjectString(jsonObject, "id"), jsonObject, ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE, true)
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