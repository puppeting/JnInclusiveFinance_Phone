package com.inclusive.finance.jh.ui.apply.guarantee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.EmptyViewBinding
import com.inclusive.finance.jh.databinding.FragmentGuaranteePersonalBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseListMenuPop
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act
import java.math.BigDecimal

/**
 * 担保信息-自然人担保分析
 * */
class GuaranteePersonalFragment : MyBaseFragment(), PresenterClick, OnRefreshListener,
    OnItemChildClickListener {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGuaranteePersonalBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>

    private var getListUrl = ""
    private var getUrl = ""
    private var saveUrl = ""
    private var deleteUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentGuaranteePersonalBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            presenterClick = this@GuaranteePersonalFragment
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.setOnItemChildClickListener(this)
        mAdapter.setEmptyView(EmptyViewBinding.inflate(LayoutInflater.from(context)).root.apply { layoutParams = FrameLayout.LayoutParams(ScreenUtils.getScreenWidth() - SizeUtils.dp2px(42f), FrameLayout.LayoutParams.MATCH_PARENT) })
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)


    }
    val listMenuDatas = mutableListOf<String>().apply {
        add("我行业务")
        add("风险探测")
        add("查看征信解析")
        add("删除")
    }
    override fun initData() {
        when (viewModel.title) {
            "自然人担保" -> {
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_APPLY -> {
                        getListUrl = Urls.getListDB_ZRRDB
                        getUrl = Urls.getEditDB_ZRRDB
                        saveUrl = Urls.saveDB_ZRRDB
                        deleteUrl = Urls.deleteDB_ZRRDB
                    }
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                    -> {
                        getListUrl = Urls.getListDB_ZRRDB
                        getUrl = Urls.getEditDB_DC_ZRRDB
                        saveUrl = Urls.saveDB_ZRRDB
                        deleteUrl = Urls.deleteDB_ZRRDB
                    }
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                    -> {
                        getListUrl = Urls.getListDB_sunshine_ZRRDB
                        getUrl = Urls.getEditDB_sunshine_ZRRDB
                        saveUrl = Urls.saveDB_sunshine_ZRRDB
                        deleteUrl = Urls.deleteDB_sunshine_ZRRDB
                    }
                    else -> {
                        getListUrl = Urls.getListDB_ZRRDB
                        getUrl = Urls.getEditDB_DC_ZRRDB
                        saveUrl = Urls.saveDB_ZRRDB
                        deleteUrl = Urls.deleteDB_ZRRDB
                    }
                }

            }
            "企业担保" -> {
                listMenuDatas.remove("我行业务")
                listMenuDatas.remove("风险探测")
                listMenuDatas.remove("查看征信解析")
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_APPLY -> {
                        getListUrl = Urls.getListDB_QYDB
                        getUrl = Urls.getEditDB_QYDB
                        saveUrl = Urls.saveDB_QYDB
                        deleteUrl = Urls.deleteDB_QYDB
                    }
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                        getListUrl = Urls.getListDB_sunshine_QYDB
                        getUrl = Urls.getEditDB_sunshine_QYDB
                        saveUrl = Urls.saveDB_sunshine_QYDB
                        deleteUrl = Urls.deleteDB_sunshine_QYDB
                    }
                    else -> {
                        getListUrl = Urls.getListDB_QYDB
                        getUrl = Urls.getEditDB_DC_QYDB
                        saveUrl = Urls.saveDB_DC_QYDB
                        deleteUrl = Urls.deleteDB_QYDB
                    }
                }

            }
            "担保公司担保" -> {
                listMenuDatas.remove("我行业务")
                listMenuDatas.remove("风险探测")
                listMenuDatas.remove("查看征信解析")
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                        getListUrl = Urls.getListDB_sunshine_GSDB
                        getUrl = Urls.getEditDB_sunshine_GSDB
                        saveUrl = Urls.saveDB_sunshine_GSDB
                        deleteUrl = Urls.deleteDB_sunshine_GSDB
                    }
                    else -> {
                        getListUrl = Urls.getListDB_GSDB
                        getUrl = Urls.getEditDB_GSDB
                        saveUrl = Urls.saveDB_GSDB
                        deleteUrl = Urls.deleteDB_GSDB
                    }
                }

            }
        }
        DataCtrlClass.ApplyNet.getApplyDBList(requireActivity(), currentPage, getListUrl, viewModel.creditId) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    viewBind.mRefreshLayout.setNoMoreData(false)
                    mAdapter.setListData(bean = it, list = it.list)
                } else {
                    mAdapter.addData(it.list)

                }
                if (!it.list.isNullOrEmpty()) {
                    mAdapter.loadMoreModule.loadMoreComplete()
                    currentPage++
                } else {
                    mAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                mAdapter.loadMoreModule.loadMoreFail()
            }
        }
    }

    override fun refreshData(type: Int?) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        super.refreshData(type)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData()
    }

    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit = { adapter, it, view ->

        when (viewModel.title) {
            "自然人担保" -> {
                calculateZRRDB(adapter, it)
            }
            "企业担保" -> {
                calculateQYDB(it)
            }


        }
    }

    private fun calculateZRRDB(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "marriage",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculateZRRDB(it, bean, adapter)
                            }
                        }
                    })
                    calculateZRRDB(it, bean, adapter)
                }
            }
        }
        it.forEach { bean ->
            when (bean.dataKey) {
                "assets",
                "allLiaily",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val assets = SZWUtils.getCalculateCount(it, "assets")
                                val allLiaily = SZWUtils.getCalculateCount(it, "allLiaily")
                                val assetsNet = BigDecimal(assets - allLiaily)
                                //净资产
                                SZWUtils.setCalculateCount(it, "assetsNet", assetsNet)
                            }
                        }
                    })
                }
            }
        }
    }

    private fun calculateZRRDB(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "spouseIdenNo",
                "spouseName",
                "spouseTel",
                -> {
                    if (typeBean.visibility != (bean.valueName != "10")) {
                        typeBean.visibility = bean.valueName != "10"
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }
    }

    private fun calculateQYDB(it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "lastYear100",
                "lastYear101",
                "lastYear104",
                "lastYear107",
                "lastYear108",
                "currentPeriod100",
                "currentPeriod101",
                "currentPeriod104",
                "currentPeriod107",
                "currentPeriod108",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val lastYear100 = SZWUtils.getCalculateCount(it, "lastYear100")
                                val lastYear101 = SZWUtils.getCalculateCount(it, "lastYear101")
                                val lastYear104 = SZWUtils.getCalculateCount(it, "lastYear104")
                                val lastYear107 = SZWUtils.getCalculateCount(it, "lastYear107")
                                val lastYear108 = SZWUtils.getCalculateCount(it, "lastYear108")
                                val currentPeriod100 = SZWUtils.getCalculateCount(it, "currentPeriod100")
                                val currentPeriod101 = SZWUtils.getCalculateCount(it, "currentPeriod101")
                                val currentPeriod104 = SZWUtils.getCalculateCount(it, "currentPeriod104")
                                val currentPeriod107 = SZWUtils.getCalculateCount(it, "currentPeriod107")
                                val currentPeriod108 = SZWUtils.getCalculateCount(it, "currentPeriod108")


                                //资产负债率%
                                SZWUtils.setCalculateCount(it, "lastYear116", if (lastYear100 > 0) BigDecimal(lastYear107 / lastYear100 * 100) else BigDecimal.ZERO)
                                SZWUtils.setCalculateCount(it, "currentPeriod116", if (currentPeriod100 > 0) BigDecimal(currentPeriod107 / currentPeriod100 * 100) else BigDecimal.ZERO)
                                //流动比率%
                                SZWUtils.setCalculateCount(it, "lastYear117", if (lastYear108 > 0) BigDecimal(lastYear101 / lastYear108 * 100) else BigDecimal.ZERO)
                                SZWUtils.setCalculateCount(it, "currentPeriod117", if (currentPeriod108 > 0) BigDecimal(currentPeriod101 / currentPeriod108 * 100) else BigDecimal.ZERO)
                                //速动比率%
                                SZWUtils.setCalculateCount(it, "lastYear118", if (lastYear108 > 0) BigDecimal((lastYear101 - lastYear104) / lastYear108 * 100) else BigDecimal.ZERO)
                                SZWUtils.setCalculateCount(it, "currentPeriod118", if (currentPeriod108 > 0) BigDecimal((currentPeriod101 - currentPeriod104) / currentPeriod108 * 100) else BigDecimal.ZERO)
                            }
                        }
                    })
                }
            }
        }
    }


    override fun onClick(v: View?) {
        BaseTypePop(context, this, "新增", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, subscribe = subscribe) { adapter, resultStr ->
            viewBind.mRefreshLayout.autoRefresh()
            if (isAdded) (activity as BaseActivity).refreshData()
        }.show(childFragmentManager, this.javaClass.name)
    }

    private fun navTo(v: View, title: String) {
        SZWUtils.getJsonObjectBeanFromList(mAdapter.data) {
            IRouter.goF(v, R.id.action_to_navActivity, title, viewModel.creditId, SZWUtils.getJsonObjectString(it, "id"), ApplyModel.BUSINESS_TYPE_APPLY, viewModel.seeOnly)
        }
    }



    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        when (v.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "风险探测" -> {
                            IRouter.goF(v, R.id.action_to_navActivity, "风险探测", viewModel.creditId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "我行业务" -> {
                            IRouter.goF(v, R.id.action_to_navActivity, "我行业务", viewModel.creditId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "查看征信解析" -> {
                            IRouter.goF(v, R.id.action_to_navActivity, "查看征信解析", if (viewModel.businessType < 50) viewModel.creditId else viewModel.dhId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "删除" -> {
                            ConfirmPop(context, "确定删除?") { confirm ->
                                if (confirm) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deleteUrl, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                    viewBind.mRefreshLayout.autoRefresh()
                                    if (isAdded) (activity as BaseActivity).refreshData()
                                }
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        else -> {}
                    }
                }.showPopupWindow(v)
            }
            R.id.bt_seeOnly -> {
                BaseTypePop(context, this, "查看", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, json = jsonObject).show(childFragmentManager, this.javaClass.name)
            }
            R.id.bt_change -> {
                BaseTypePop(context, this, "修改", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, json = jsonObject, subscribe = subscribe) { _, _ ->
                    viewBind.mRefreshLayout.autoRefresh()
                    if (isAdded) (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)

            }
        }
    }

}