package com.inclusive.finance.jh.ui.apply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.RegexUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
import com.inclusive.finance.jh.databinding.FragmentApplyFamilyListBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act

/**
 * 家庭成员
 * */
class FamilyListFragment : MyBaseFragment(), PresenterClick, OnLoadMoreListener, OnRefreshListener,
    OnItemChildClickListener {
    private lateinit var listMenuDatas: MutableList<String>
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentApplyFamilyListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    var getUrl = ""
    var getPopUrl = ""
    var savePopUrl = ""
    var deletePopUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentApplyFamilyListBinding.inflate(inflater, container, false).apply {
            presenterClick = this@FamilyListFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.setOnItemChildClickListener(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)

    }


    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        if(viewModel.businessType==ApplyModel.BUSINESS_TYPE_FUPIN){
            viewBind?.btMore?.visibility = View.VISIBLE
            viewBind?.btSeeOnly?.visibility = View.VISIBLE
            viewBind?.btChange?.visibility = View.VISIBLE
            if(viewModel.seeOnly == true){
                viewBind?.btMore?.visibility = View.GONE
                 viewBind?.btChange?.visibility = View.GONE
                viewBind?.btSeeOnly?.visibility = View.VISIBLE
            }
            listMenuDatas = mutableListOf<String>().apply {
                add("删除")
            }
        }else{
             listMenuDatas = mutableListOf<String>().apply {
                add("信息概况")
                add("我行业务")
                add("风险探测")
                add("删除")
            }
        }


    }
    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            -> {
                getUrl = Urls.getFamilyList
                getPopUrl = Urls.getFamilyPreAdd
                savePopUrl = Urls.saveFamilyNumber
                deletePopUrl = Urls.deleteFamilyNumber
            }
            ApplyModel.BUSINESS_TYPE_FUPIN,
            ->{
                getUrl = Urls.get_fpf_listJtApp
//                savePopUrl = Urls.saveFamilyNumber
                getPopUrl = Urls.get_fpf_getJtxx
                savePopUrl = Urls.save_fpf_addHxx
                deletePopUrl=Urls.save_fpf_delJtxx
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            -> {
                getUrl = Urls.get_jnj_cj_personal_jtcy
                getPopUrl = Urls.get_jnj_cj_personal_jtcy_pop
                savePopUrl = Urls.save_jnj_cj_personal_jtcy_pop
                deletePopUrl = Urls.delete_jnj_cj_personal_jtcy_pop
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl = Urls.get_visit_jtcy
                getPopUrl = Urls.get_visit_jtcy_pop
                savePopUrl = Urls.save_visit_jtcy_pop
                deletePopUrl = Urls.delete_visit_jtcy_pop
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getUrl = Urls.get_sunshine_jtcy
                getPopUrl = Urls.get_sunshine_jtcy_pop
                savePopUrl = Urls.save_sunshine_jtcy_pop
                deletePopUrl = Urls.delete_sunshine_jtcy_pop
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.getFamilyListInfo(requireActivity(), getUrl, keyId = viewModel.keyId, businessType = businessType) {
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


    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit = { adapter, it, view ->
        calculateJTCY(adapter, it, view)
    }

    private fun calculateJTCY(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>, view: View) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "relativeType",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculate(it, bean, adapter)
                            }
                        }
                    })
                    calculate(it, bean, adapter)
                }
                "relativeIdenNo",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val iDCardBean = it.firstOrNull { item -> item.dataKey == "relativeIdenNo" && item.valueName.isNotEmpty() }
                                if (RegexUtils.isMatch(iDCardBean?.regex, iDCardBean?.valueName)) {
                                    DataCtrlClass.ApplyNet.getDskhjdxx(context, iDCardBean?.valueName, view) { jsonObject ->
                                        if (jsonObject != null) {
                                            it.firstOrNull { item -> item.dataKey == "relativeName" }?.valueName = SZWUtils.getJsonObjectString(jsonObject, "custName")
                                            it.firstOrNull { item -> item.dataKey == "relativeTel" }?.valueName = SZWUtils.getJsonObjectString(jsonObject, "mobilephone1")
                                        }
                                    }

                                }
                            }
                        }
                    })
                }
            }
        }
    }

    private fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "relSpoIdenNo",
                "relSpoName",
                "relSpoTel",
                -> {
                    if (typeBean.visibility != (bean.valueName != "2")) {
                        typeBean.visibility = bean.valueName != "2"
                        adapter.notifyItemChanged(index)
                    }

                }
            }
        }
    }


    override fun onClick(v: View?) {
        if(businessType=="600"){
            BaseTypePop(context, this, "新增", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, mId = "", subscribe = subscribe, businessType = businessType) { adapter, resultStr ->
                refreshData()
                if (isAdded) (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
        }else{
            BaseTypePop(context, this, "新增", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, subscribe = subscribe, businessType = businessType) { adapter, resultStr ->
                refreshData()
                if (isAdded) (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
        }

    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "信息概况" ,
                        "风险探测" ,
                        "我行业务" -> {
                            IRouter.goF(view, R.id.action_to_navActivity, listMenuDatas[it], viewModel.keyId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "删除" -> {
                            ConfirmPop(context, "确定删除?") { confirm ->
                                if (confirm) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deletePopUrl, SZWUtils.getJsonObjectString(jsonObject, "id"), keyId = viewModel.keyId, businessType = businessType) {
                                    refreshData()
                                    if (isAdded) (activity as BaseActivity).refreshData()
                                }
                            }.show(childFragmentManager, this.javaClass.name)

                        }
                        else -> {}
                    }
                }.showPopupWindow(view)
            }
            R.id.bt_seeOnly -> {
                BaseTypePop(context, this, "查看", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, json = jsonObject, subscribe = subscribe, businessType = businessType) { adapter, resultStr ->
                    refreshData()
                    if (isAdded) (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)
            }
            R.id.bt_change -> {
                if(businessType=="600"){
                    BaseTypePop(context, this, "编辑", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, mId = SZWUtils.getJsonObjectString(jsonObject, "id"), json = jsonObject, subscribe = subscribe, businessType = businessType) { adapter, resultStr ->
                        refreshData()
                        if (isAdded) (activity as BaseActivity).refreshData()
                    }.show(childFragmentManager, this.javaClass.name)
                }else {
                    BaseTypePop(context, this, "编辑", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, mId = viewModel.keyId, json = jsonObject, subscribe = subscribe, businessType = businessType) { adapter, resultStr ->
                        refreshData()
                        if (isAdded) (activity as BaseActivity).refreshData()
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }
        }
    }


}