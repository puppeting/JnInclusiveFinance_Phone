package com.inclusive.finance.jh.ui.apply.guarantee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentGuaranteeShareBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseListMenuPop
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act

/**
 * 担保信息-{各种押的共同借款人}
 * */
class GuaranteeBetShareFragment : MyBaseFragment(), PresenterClick, OnRefreshListener,
    OnItemChildClickListener {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGuaranteeShareBinding
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
        viewBind = FragmentGuaranteeShareBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            presenterClick = this@GuaranteeBetShareFragment
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.setOnItemChildClickListener(this)
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        initData()
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("我行业务")
        add("风险探测")
        add("删除")
    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                getListUrl = Urls.getListDB_sunshine_GYQLR
                getUrl = Urls.getEditDB_sunshine_GYQLR
                saveUrl = Urls.saveDB_sunshine_GYQLR
                deleteUrl = Urls.deleteDB_sunshine_GYQLR
            }
            else -> {
                getListUrl = Urls.getListDB_GYQLR
                getUrl = Urls.getEditDB_GYQLR
                saveUrl = Urls.saveDB_GYQLR
                deleteUrl = Urls.deleteDB_GYQLR
            }

        }
        DataCtrlClass.ApplyNet.getApplyDBShareList(context, currentPage, getListUrl, SZWUtils.getJsonObjectString(JsonParser.parseString(viewModel.jsonObject).asJsonObject, "id")) {
            if (it != null) {
                viewBind.mRefreshLayout.finishRefresh()
                mAdapter.setListData(bean = it, list = it.list)
            }
        }
    }

    override fun onClick(v: View?) {
        BaseTypePop(context, this, "新增", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, json = JsonParser.parseString(viewModel.jsonObject).asJsonObject) { adapter, resultStr ->
            viewBind.mRefreshLayout.autoRefresh()
        }.show(childFragmentManager, this.javaClass.name)
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
                        "删除" -> {
                            ConfirmPop(context, "确定删除?") {confirm ->
                                if (confirm) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deleteUrl, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                    viewBind.mRefreshLayout.autoRefresh()
                                    if (isAdded) (activity as BaseActivity).refreshData()
                                }
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        else -> {}
                    } as Unit?
                }.showPopupWindow(v)
            }
            R.id.bt_seeOnly -> {
                BaseTypePop(context, this, "查看", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, json = jsonObject).show(childFragmentManager, this.javaClass.name)
            }
            R.id.bt_change -> {
                BaseTypePop(context, this, "修改", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, json = jsonObject) {_, _ ->
                    viewBind.mRefreshLayout.autoRefresh()
                    if (isAdded) (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)
            }
        }
    }

}