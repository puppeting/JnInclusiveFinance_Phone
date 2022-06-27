package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentContarctInfoBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.widget.MyWebActivity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act
import java.util.ArrayList


/**
 * 合同详情列表
 * */
class ContractFragment : MyBaseFragment(), PresenterClick, OnLoadMoreListener, OnRefreshListener,
    OnItemChildClickListener {
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentContarctInfoBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    private var getListUrl = Urls.getList_CreditManager
    private var getUrl = Urls.getEdit_CreditManager
    private var saveUrl = Urls.save_CreditManager
    private var deleteUrl = Urls.delete_CreditManager
    private var submitUrl = Urls.creditAnalysisAdd
    private var enumUrl = Urls.getCreditManagerCyList
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentContarctInfoBinding.inflate(inflater, container, false).apply {
            presenterClick = this@ContractFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.mRefreshLayout.setOnRefreshListener(this)
        mAdapter = ItemBaseListCardAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.setOnItemChildClickListener(this)
        viewBind.mRecyclerView.adapter = mAdapter
    }

    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btMore?.visibility = View.VISIBLE
        viewBind?.btSeeOnly?.visibility = View.VISIBLE
        viewBind?.btChange?.visibility = View.VISIBLE
        viewBind?.btSeeOnly?.text = "删除"

    }

    override fun initData() {

        getListUrl = Urls.htqy_listApp
        getUrl = Urls.getEdit_CreditManager
        saveUrl = Urls.save_CreditManager
        deleteUrl = Urls.delete_CreditManager
        submitUrl = Urls.creditAnalysisAdd
        enumUrl = Urls.getCreditManagerCyList

        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.getCreditManagerList(requireActivity(), getListUrl, keyId = viewModel.keyId, businessType = businessType) {
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

    override fun onLoadMore() {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData()
    }

    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit = { adapter, it, view ->
        calculateJTCY(adapter, it, view)
    }

    private fun calculateJTCY(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>, view: View) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "hkfs",
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
                "zyjjfs",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculate2(it, bean, adapter)
                            }
                        }
                    })
                    calculate2(it, bean, adapter)
                }

            }
        }
    }

    private fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "jxrlx","jxpl",
                -> {
                    if(bean.valueName == "二"){
                        typeBean.visibility = true
                        adapter.notifyItemChanged(index)
                    }else {
                        typeBean.visibility = false
                        adapter.notifyItemChanged(index)
                    }

                }
                "fqhkpl",
                -> {
                    if(bean.valueName == "三"){
                        typeBean.visibility = true
                        adapter.notifyItemChanged(index)
                    }else {
                        typeBean.visibility = false
                        adapter.notifyItemChanged(index)
                    }
                }
                "debxbjhkpl",
                "hkpllx",
                "hkjzlx",
                "debjbxhkfs",
                -> {
                    if(bean.valueName == "四"){
                        typeBean.visibility = true
                        adapter.notifyItemChanged(index)
                    }else {
                        typeBean.visibility = false
                        adapter.notifyItemChanged(index)
                    }
                }
                "zcjgqc",
                -> {
                    if(bean.valueName == "一"){
                        typeBean.visibility = false
                        adapter.notifyItemChanged(index)
                    }else {
                        typeBean.visibility = true
                        adapter.notifyItemChanged(index)
                    }
                }

            }
        }
    }
    private fun calculate2(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {

                "zcjgqc",
                -> {
                    if(bean.valueName == "一"){
                        typeBean.visibility = false
                        adapter.notifyItemChanged(index)
                    }else {
                        typeBean.visibility = true
                        adapter.notifyItemChanged(index)
                    }
                }

            }
        }
    }

    override fun onClick(v: View?) {
//        CreditManagerPop(context, this, "新增", getUrl = getUrl, saveUrl = saveUrl, enumUrl = enumUrl, keyId = viewModel.keyId) {
//            onRefresh(viewBind.mRefreshLayout)
//            if (isAdded) (activity as BaseActivity).refreshData()
//        }.show(childFragmentManager, this.javaClass.name)
        LinShiPop(context, businessType = ApplyModel.BUSINESS_TYPE_HTQY) {
            BaseTypePop(context, this, "新增", Urls.htqy_get, Urls.htqy_add, null, keyId = viewModel.keyId, mId = "", contractType = it.toString(), businessType = "500", subscribe = subscribe) { adapter, resultStr ->
                onRefresh(viewBind.mRefreshLayout)
                if (isAdded) (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
        }.show(childFragmentManager, this.javaClass.name)
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("预览")

    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        when (v.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "预览" -> {
                            DataCtrlClass.HTQYNet.getYhht(context, Urls.htqy_previewApp, idenNo = SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                if (it.toString().isNotEmpty()) ARouter.getInstance()
                                    .build("/com/MyWebActivity") //                        .withString(Intent_WebUrl, "http://192.168.3.32:8081/onlinePreview?url=http%3A%2F%2F212.129.130.163%3A3000%2Ftscepdf.pdf&officePreviewType=pdf")
                                    //                        .withString(Intent_WebUrl, "http://debugtbs.qq.com")
                                    .withString(MyWebActivity.Intent_WebUrl, SZWUtils.getIntactUrl(it.toString()))
                                    .withBoolean("isPDF", true)
                                    .withString(MyWebActivity.Intent_WebTitle, "合同预览").navigation()
                                else SZWUtils.showSnakeBarMsg("暂无合同预览")
                            }

                        }
                        "查看征信PDF" -> {
                            ARouter.getInstance()
                                .build("/com/MyWebActivity") //                        .withString(Intent_WebUrl, "http://192.168.3.32:8081/onlinePreview?url=http%3A%2F%2F212.129.130.163%3A3000%2Ftscepdf.pdf&officePreviewType=pdf")
                                //                        .withString(Intent_WebUrl, "http://debugtbs.qq.com")
                                .withString(MyWebActivity.Intent_WebUrl, SZWUtils.getIntactUrl("zx/zx/queryZxPdf?id=${SZWUtils.getJsonObjectString(jsonObject, "id")}" + "&userName=${(if (MyApplication.user == null) User() else MyApplication.user as User).userInfo?.username}"))
                                .withBoolean("isPDF", true)
                                .withString(MyWebActivity.Intent_WebTitle, "征信PDF").navigation()
                        }
                        "查看征信解析" -> {
                            IRouter.goF(v, R.id.action_to_navActivity, "查看征信解析", viewModel.keyId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "提交" -> {

                            when (viewModel.businessType) {
                                ApplyModel.BUSINESS_TYPE_APPLY,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                                -> {
                                    if (SZWUtils.getJsonObjectString(jsonObject, "state") == "500") {
                                        DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
                                            refreshData()
                                        }
                                    } else {
                                        GHQYJPop(context, jsonObject, ApplyModel.BUSINESS_TYPE_APPLY) {
                                            jsonObject.addProperty("sprgh", "" + it)
                                            DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
                                                refreshData()
                                            }
                                        }.show(childFragmentManager, this.javaClass.name)
                                    }

                                }
                                else -> {
                                    DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
                                        refreshData()
                                    }
                                }
                            }


                        }
                        "删除" -> {
                            ConfirmPop(context, "确定删除?") {
                                if (it) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deleteUrl, SZWUtils.getJsonObjectString(jsonObject, "id"), keyId = viewModel.keyId) {
                                    onRefresh(viewBind.mRefreshLayout)
                                    if (isAdded) (activity as BaseActivity).refreshData()
                                }
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        else -> {
                        }
                    } as Unit?
                }.showPopupWindow(v)
            }
            R.id.bt_seeOnly -> {
                ConfirmPop(context, "确认删除") { confirm ->
                    if (confirm) {
                        DataCtrlClass.KHGLNet.deleteBaseTypePoPList(context, Urls.htqy_delete, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), jsonObject) {
                            onRefresh(viewBind.mRefreshLayout)
                            if (isAdded) (activity as BaseActivity).refreshData()
                        }
                    }
                }.show(childFragmentManager, "adapter")
            }
            R.id.bt_change -> {
//                CreditManagerPop(context, this, "修改", getUrl = getUrl, saveUrl = saveUrl, enumUrl = enumUrl, keyId = viewModel.keyId, json = jsonObject) {
//                    onRefresh(viewBind.mRefreshLayout)
//                    if (isAdded) (activity as BaseActivity).refreshData()
//                }.show(childFragmentManager, this.javaClass.name)
                BaseTypePop(context, this, "修改", Urls.htqy_get, Urls.htqy_add, jsonObject, keyId = viewModel.keyId, mId = SZWUtils.getJsonObjectString(jsonObject, "id"), contractType = SZWUtils.getJsonObjectString(jsonObject, "contractType"), businessType = "500", subscribe = subscribe) { adapter, resultStr ->
                    onRefresh(viewBind.mRefreshLayout)
                    if (isAdded) (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)
            }

        }
    }
}