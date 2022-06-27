package com.inclusive.finance.jh.ui.apply.guarantee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
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
import com.inclusive.finance.jh.databinding.FragmentGuaranteeBaseBinding
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
 * 担保信息-{各种押}
 * */
class GuaranteeBetFragment : MyBaseFragment(), PresenterClick, OnRefreshListener,OnItemChildClickListener {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGuaranteeBaseBinding
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
        viewBind = FragmentGuaranteeBaseBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            presenterClick = this@GuaranteeBetFragment
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var jsonObject: JsonObject? = null
    override fun initView() {
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.setOnItemChildClickListener(this)
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)
    }

//    override fun refreshData(type: Int?) {
//        super.refreshData(type)
//        jsonObject = null
//        initShareData(this.jsonObject)
//    }
    override fun refreshData(type: Int?) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        super.refreshData(type)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData()
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("共有人权利")
        add("我行业务")
        add("风险探测")
        add("删除")
    }
    override fun initData() {
        when (viewModel.title) {
            "房产抵押" -> {
               when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_APPLY -> {
                        getListUrl = Urls.getListDB_FCDY
                        getUrl = Urls.getEditDB_FCDY
                        saveUrl = Urls.saveDB_FCDY
                        deleteUrl = Urls.deleteDB_FCDY
                    }
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                    -> {
                        getListUrl = Urls.getListDB_FCDY
                        getUrl = Urls.getEditDB_DC_FCDY
                        saveUrl = Urls.saveDB_FCDY
                        deleteUrl = Urls.deleteDB_FCDY
                    }
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                    -> {
                        getListUrl = Urls.getListDB_sunshine_FCDY
                        getUrl = Urls.getEditDB_sunshine_FCDY
                        saveUrl = Urls.saveDB_sunshine_FCDY
                        deleteUrl = Urls.deleteDB_sunshine_FCDY
                    }
                    else -> {
                        getListUrl = Urls.getListDB_FCDY
                        getUrl = Urls.getEditDB_DC_FCDY
                        saveUrl = Urls.saveDB_FCDY
                        deleteUrl = Urls.deleteDB_FCDY
                    }
                }
            }
            "存单质押" -> {
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY-> {
                        getListUrl = Urls.getListDB_sunshine_ZYDB + "?type=CD"
                        getUrl = Urls.getEditDB_sunshine_ZYDB + "?type=CD"
                        saveUrl = Urls.saveDB_sunshine_ZYDB + "?type=CD"
                        deleteUrl = Urls.deleteDB_sunshine_ZYDB
                    }
                    else -> {
                         getListUrl = Urls.getListDB_ZYDB + "?type=CD"
                         getUrl = Urls.getEditDB_ZYDB + "?type=CD"
                         saveUrl = Urls.saveDB_ZYDB + "?type=CD"
                         deleteUrl = Urls.deleteDB_ZYDB
                    }

                }

            }
            "国债质押" -> {
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY-> {
                        getListUrl = Urls.getListDB_sunshine_ZYDB + "?type=GZ"
                        getUrl = Urls.getEditDB_sunshine_ZYDB + "?type=GZ"
                        saveUrl = Urls.saveDB_sunshine_ZYDB + "?type=GZ"
                        deleteUrl = Urls.deleteDB_sunshine_ZYDB
                    }
                    else -> {
                        getListUrl = Urls.getListDB_ZYDB + "?type=GZ"
                        getUrl = Urls.getEditDB_ZYDB + "?type=GZ"
                        saveUrl = Urls.saveDB_ZYDB + "?type=GZ"
                        deleteUrl = Urls.deleteDB_ZYDB
                    }

                }

            }
        }
        DataCtrlClass.ApplyNet.getApplyDBList(requireActivity(), currentPage, getListUrl, viewModel.creditId) {
            if (it != null) {
                viewBind.mRefreshLayout.finishRefresh()
                mAdapter.setListData(bean = it, list = it.list)

            }
        }
    }
    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>,rootView:View) -> Unit = { adapter, it, view->

        when (viewModel.title) {
            "房产抵押" -> {
                calculateZRRDB(adapter, it)
            }


        }
    }
    private fun calculateZRRDB(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "mortgagePartGuaraAmt",
                "mortgageEvalAmt",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
//                                mortgageRatio=mortgagePartGuaraAmt/mortgageEvalAmt
                                val mortgagePartGuaraAmt= SZWUtils.getCalculateCount(it, "mortgagePartGuaraAmt")
                                val mortgageEvalAmt= SZWUtils.getCalculateCount(it, "mortgageEvalAmt")
                                //抵押比例
                                SZWUtils.setCalculateCount(it, "mortgageRatio", if (mortgageEvalAmt > 0) BigDecimal(mortgagePartGuaraAmt / mortgageEvalAmt) else BigDecimal.ZERO)
                            }
                        }
                    })
                }
            }
        }
    }
    override fun onClick(v: View?) {
        BaseTypePop(context, this, "新增", getUrl = getUrl, saveUrl = saveUrl, keyId = viewModel.keyId, subscribe = subscribe) {adapter,resultStr->
            viewBind.mRefreshLayout.autoRefresh()
            if (isAdded) (activity as BaseActivity).refreshData()
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
                        "共有人权利" -> {
                            IRouter.goF(v, R.id.action_to_navActivity, "共有人权利", viewModel.creditId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "删除" -> {
                            ConfirmPop(context, "确定删除?") { confirm->
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