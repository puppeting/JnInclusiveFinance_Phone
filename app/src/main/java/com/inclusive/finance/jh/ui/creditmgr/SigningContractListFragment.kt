package com.inclusive.finance.jh.ui.creditmgr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCreditManagerContractListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.pop.CreditManagerDZYPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act

/**
 * 合同签约列表
 * */
class SigningContractListFragment : MyBaseFragment(), PresenterClick, OnLoadMoreListener,
    OnRefreshListener {
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditManagerContractListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    var getUrl = ""
    var getPopUrl = ""
    var savePopUrl = ""
    var deletePopUrl = ""
    var getDZYPopListUrl = ""
    var saveDZYPopUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCreditManagerContractListBinding.inflate(inflater, container, false)
            .apply {
                presenterClick = this@SigningContractListFragment
                viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
                data = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        viewBind.layoutBaseList.mRecyclerView.setItemViewCacheSize(30)
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)
    }


    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER -> {
                getUrl = Urls.get_signing_contract_list
                getPopUrl = Urls.get_signing_contract_PreAdd
                savePopUrl = Urls.save_signing_contract
                deletePopUrl = Urls.delete_signing_contract
                getDZYPopListUrl = Urls.get_signing_contract_dzy_list
                saveDZYPopUrl = Urls.save_signing_contract_dzy_pop
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.CreditManagementNet.getHTQYListInfo(requireActivity(), getUrl, keyId = viewModel.keyId, businessType = businessType) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    viewBind.mRefreshLayout.setNoMoreData(false)
                    mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it) {
                        mAdapter.setNewInstance(it.list)
                    }
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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData()
    }

    override fun refreshData(type: Int?) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        super.refreshData(type)
    }

    override fun onLoadMore() {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    private fun calculate(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEachIndexed { index, bean ->
            when (bean.dataKey) {
                "startDate",
                "endDate",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val startDate = it.firstOrNull { item -> item.dataKey == "startDate" }?.valueName
                                val endDate = it.firstOrNull { item -> item.dataKey == "endDate" }?.valueName
                                if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
                                    it.firstOrNull { item -> item.dataKey == "timeLimit" }?.valueName = SZWUtils.getMonthSpace(endDate, startDate)
                                        .toString()
                                }
                            }
                        }
                    })
                }
            }
            when (bean.dataKey) {
                "contractType",
                "loanMethod",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                contractCtrl(it, adapter)
                            }
                        }
                    })
                }
            }
        }
        contractCtrl(it, adapter)
    }

    private fun contractCtrl(it: ArrayList<BaseTypeBean>, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        val contractType = it.firstOrNull { item -> item.dataKey == "contractType" }?.valueName
        val loanMethodBean = it.firstOrNull { item -> item.dataKey == "loanMethod" }
        val guaranteeName = it.firstOrNull { item -> item.dataKey == "guaranteeName" }
        val pledgeName = it.firstOrNull { item -> item.dataKey == "pledgeName" }

        when {
            contractType == "01" || contractType == "03" -> {
                if (loanMethodBean?.valueName != "信用") loanMethodBean?.valueName = "信用"
                loanMethodBean?.editable = false
                pledgeName?.visibility = false
                guaranteeName?.requireable = false
                pledgeName?.requireable = false
            }
            (contractType == "02" || contractType == "04" || contractType == "05") && loanMethodBean?.valueName == "担保" -> {
                loanMethodBean.editable = true
                guaranteeName?.visibility = true
                pledgeName?.visibility = false
                guaranteeName?.requireable = true
                pledgeName?.requireable = false
            }
            (contractType == "02" || contractType == "04" || contractType == "05") && loanMethodBean?.valueName?.contains("抵押") == true && loanMethodBean.valueName.contains("质押") && !loanMethodBean.valueName.contains("担保") && !loanMethodBean.valueName.contains("信用") -> {
                loanMethodBean.editable = true
                guaranteeName?.visibility = false
                pledgeName?.visibility = true
                guaranteeName?.requireable = false
                pledgeName?.requireable = true
            }
            else -> {
                loanMethodBean?.editable = true
                guaranteeName?.visibility = true
                pledgeName?.visibility = true
                guaranteeName?.requireable = true
                pledgeName?.requireable = true
            }
        }
        adapter.notifyItemChanged(adapter.data.indexOf(loanMethodBean))
        adapter.notifyItemChanged(adapter.data.indexOf(guaranteeName))
        adapter.notifyItemChanged(adapter.data.indexOf(pledgeName))
    }

    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit = { adapter, it, view ->
        calculate(adapter, it)
    }

    override fun onClick(v: View?) {
        if (v == viewBind.chipAdd) {
            BaseTypePop(context, this, "新增", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, businessType = businessType, subscribe = subscribe) {adapter,resultStr->
                refreshData()
                if (isAdded) (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
            val id = SZWUtils.getJsonObjectString(jsonObject, "id")
            val mainContractNo = SZWUtils.getJsonObjectString(jsonObject, "contractNo")
            when (v) {
                viewBind.chipEdit -> {
                    when (flag) {
                        "1"  -> BaseTypePop(context, this, "编辑", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, json = jsonObject, businessType = businessType, subscribe = subscribe) {adapter,resultStr->
                            refreshData()
                            if (isAdded) (activity as BaseActivity).refreshData()
                        }.show(childFragmentManager, this.javaClass.name)
                        else-> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                    }

                }
                viewBind.chipDzy -> {
                    CreditManagerDZYPop(context, this, true, "抵质押信息", getUrl = getDZYPopListUrl, saveUrl = "", keyId = viewModel.keyId, htId = id, mainContractNo = mainContractNo, json = jsonObject, businessType = businessType) {
                        refreshData()
                    }.show(childFragmentManager, this.javaClass.name)
                }
                viewBind.chipDelete -> {
                    when (flag) {
                        "1" ->ConfirmPop(context, "确定删除?") {
                            if (it) DataCtrlClass.CreditManagementNet.deleteById(context, deletePopUrl, id, keyId = viewModel.keyId) {
                                refreshData()
                                if (isAdded) (activity as BaseActivity).refreshData()
                            }
                        }.show(childFragmentManager, this.javaClass.name)
                        else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                    }

                }
                viewBind.chipGenerateContract -> {
                    BaseTypePop(context, this, "查看", getUrl = Urls.get_signing_contract_ht, saveUrl = "", keyId = viewModel.keyId, json = jsonObject, businessType = businessType) {adapter,resultStr->
                        refreshData()
                        if (isAdded) (activity as BaseActivity).refreshData()
                    }.show(childFragmentManager, this.javaClass.name)

                }
            }
        }

    }
}