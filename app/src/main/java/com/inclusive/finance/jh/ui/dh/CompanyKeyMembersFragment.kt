package com.inclusive.finance.jh.ui.dh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.RegexUtils
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.*
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act

/**
 * 法人及管件成员
 *
 * */
class CompanyKeyMembersFragment : MyBaseFragment(),PresenterClick, OnLoadMoreListener,
    OnRefreshListener {
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCompanyKeyMembersBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    var getUrl=""
    var getPopUrl=""
    var savePopUrl=""
    var deletePopUrl=""
    var businessType=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCompanyKeyMembersBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CompanyKeyMembersFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data=viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        viewBind.layoutBaseList.mRecyclerView.setItemViewCacheSize(30)
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)
    }
    override fun initData() {
//                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
//                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
//                    mainData,
//                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
//                )
//
//                adapter.setNewInstance(list)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL, -> {
                getUrl=Urls.get_jnj_frjgjrcy
                getPopUrl=Urls.get_jnj_frjgjrcy_pop
                savePopUrl=Urls.save_jnj_frjgjrcy_pop
                deletePopUrl=Urls.delete_jnj_frjgjrcy_pop
                businessType="03"
            }
        }
        DataCtrlClass.ApplyNet.getFamilyListInfo(requireActivity(),getUrl,keyId = viewModel.keyId,businessType = businessType) {
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


    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>,rootView:View) -> Unit = { adapter, it, view->
        calculateJTCY(adapter, it,view)
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
                                if (RegexUtils.isMatch(iDCardBean?.regex,iDCardBean?.valueName)){
                                    DataCtrlClass.ApplyNet.getDskhjdxx(context,iDCardBean?.valueName,view){jsonObject->
                                        if (jsonObject!=null){
                                            it.firstOrNull { item -> item.dataKey == "relativeName" }?.valueName =SZWUtils.getJsonObjectString(jsonObject,"custName")
                                            it.firstOrNull { item -> item.dataKey == "relativeTel" }?.valueName = SZWUtils.getJsonObjectString(jsonObject,"mobilephone1")
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
        if (v != null) when (v) {
            viewBind.chipAdd -> {
                BaseTypePop(context, this, "新增", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, subscribe = subscribe,businessType = businessType) {adapter,resultStr->
                    refreshData()
                    if (isAdded)
                        (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)
            }
            viewBind.chipEdit -> {
                SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
                    BaseTypePop(context, this, "编辑", getUrl = getPopUrl, saveUrl = savePopUrl, keyId = viewModel.keyId, json = jsonObject, subscribe = subscribe,businessType = businessType) {adapter,resultStr->
                        refreshData()
                        if (isAdded)
                            (activity as BaseActivity).refreshData()
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }
            viewBind.chipDelete -> {
                SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
                    ConfirmPop(context, "确定删除?") {
                        if (it) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deletePopUrl, SZWUtils.getJsonObjectString(jsonObject, "id"),keyId = viewModel.keyId,businessType = businessType) {
                            refreshData()
                            if (isAdded)
                                (activity as BaseActivity).refreshData()
                        }
                    }.show(childFragmentManager,this.javaClass.name)
                }
            }
            viewBind.chipDetail -> {
                SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
                    IRouter.goF(v, R.id.action_to_navActivity, "信息概况", if (viewModel.businessType<50)viewModel.creditId else viewModel.dhId,jsonObject, viewModel.businessType, viewModel.seeOnly)
                }
            }
        }

    }

}